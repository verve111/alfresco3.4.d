/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.transfer.NodeCrawler;
import org.alfresco.service.cmr.transfer.NodeCrawlerFactory;
import org.alfresco.service.cmr.transfer.TransferDefinition;
import org.alfresco.service.cmr.transfer.TransferService2;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublishServiceImpl implements PublishService
{
    private final static Log log = LogFactory.getLog(PublishServiceImpl.class);
    private final static String PUBLISH_QUEUE_NAME = "publishingQueue";
    
    private SiteHelper siteHelper;
    private NodeService nodeService;
    private TransferService2 transferService;
    private TransferPathMapper pathMapper;
    private NodeCrawlerFactory nodeCrawlerFactory;
    private NodeCrawlerConfigurer crawlerConfigurer;
    private String transferTargetName = "Internal Target";

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setTransferService(TransferService2 transferService)
    {
        this.transferService = transferService;
    }

    public void setTransferTargetName(String transferTargetName)
    {
        this.transferTargetName = transferTargetName;
    }

    public void setPathMapper(TransferPathMapper pathMapper)
    {
        this.pathMapper = pathMapper;
    }

    public void setNodeCrawlerFactory(NodeCrawlerFactory nodeCrawlerFactory)
    {
        this.nodeCrawlerFactory = nodeCrawlerFactory;
    }

    public void setCrawlerConfigurer(NodeCrawlerConfigurer crawlerConfigurer)
    {
        this.crawlerConfigurer = crawlerConfigurer;
    }

    public void enqueuePublishedNodes(final NodeRef... nodes)
    {
        if ((nodes != null) && (nodes.length > 0))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Request to enqueue these nodes for publishing: " + Arrays.asList(nodes));
            }
            NodeRef publishingQueue = siteHelper.getWebSiteContainer(nodes[0], PUBLISH_QUEUE_NAME);
            if (publishingQueue != null)
            {
                for (NodeRef node : nodes)
                {
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                    String name = GUID.generate();
                    props.put(ContentModel.PROP_NAME, name);
                    //Storing noderefs of deleted nodes doesn't work, so we'll store a text representation instead...
                    props.put(WebSiteModel.PROP_QUEUED_NODE, node.toString());
                    nodeService.createNode(publishingQueue, ContentModel.ASSOC_CONTAINS, QName.createQName(
                            WebSiteModel.NAMESPACE, name), WebSiteModel.TYPE_PUBLISH_QUEUE_ENTRY, props);
                }
            }
        }
    }

    public void enqueuePublishedNodes(Collection<NodeRef> nodes)
    {
        enqueuePublishedNodes(nodes.toArray(new NodeRef[nodes.size()]));
    }

    public void enqueueRemovedNodes(NodeRef... nodes)
    {
        // Currently handles in the same way as published nodes.
        enqueuePublishedNodes(nodes);
    }

    public void enqueueRemovedNodes(Collection<NodeRef> nodes)
    {
        // Currently handles in the same way as published nodes.
        enqueuePublishedNodes(nodes);
    }

    public void publishQueue(final NodeRef websiteId)
    {
        if (websiteId == null)
        {
            throw new IllegalArgumentException("websiteId == " + websiteId);
        }
        // Locate the target location, and set up an appropriate transfer path
        // mapping
        NodeRef targetSite = null;
        List<AssociationRef> targets = nodeService.getTargetAssocs(websiteId,
                WebSiteModel.ASSOC_PUBLISH_TARGET);
        if (!targets.isEmpty())
        {
            targetSite = targets.get(0).getTargetRef();
            Path sourcePath = nodeService.getPath(websiteId);
            Path targetPath = nodeService.getPath(targetSite);
            pathMapper.addPathMapping(sourcePath, targetPath);

            Set<NodeRef> nodesToTransfer = new HashSet<NodeRef>(89);
            NodeRef queue = siteHelper.getWebSiteContainer(websiteId, PUBLISH_QUEUE_NAME);
            if (queue != null)
            {
                List<ChildAssociationRef> publishedNodes = nodeService.getChildAssocs(queue,
                        ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef assoc : publishedNodes)
                {
                    NodeRef queueEntry = assoc.getChildRef();
                    NodeRef node = new NodeRef((String) nodeService.getProperty(queueEntry, WebSiteModel.PROP_QUEUED_NODE));
                    nodesToTransfer.add(node);
                }
                if (!nodesToTransfer.isEmpty())
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("PublishService is about to crawl these nodes: " + nodesToTransfer);
                    }
                    //Given the nodes that have been supplied, find any others that we will want to transfer too
                    NodeCrawler crawler = nodeCrawlerFactory.getNodeCrawler();
                    configureNodeCrawler(crawler);
                    nodesToTransfer = crawler.crawl(nodesToTransfer);

                    if (log.isDebugEnabled())
                    {
                        log.debug("PublishService has crawled the queued nodes and is about to transfer these nodes: " + nodesToTransfer);
                    }
                    
                    TransferDefinition def = new TransferDefinition();
                    def.setNodes(nodesToTransfer);
                    transferService.transfer(transferTargetName, def);
    
                    // If we get here then the transfer must have completed. Delete
                    // the queue entries that we have processed
                    for (ChildAssociationRef assoc : publishedNodes)
                    {
                        nodeService.deleteNode(assoc.getChildRef());
                    }
                }
            }
            else
            {
                log.warn("Discovered a website node that is outside of a Share site. Skipping. " + websiteId);
            }

        } 
        else
        {
            log.warn("Request has been made to publish from a site that has no target configured: "
                    + websiteId);
        }
    }
    
    /**
     * Set up the supplied node crawler to find other nodes that should be published too.
     * Override this if necessary, or (preferably) inject a different configurer
     * @param crawler
     */
    protected void configureNodeCrawler(NodeCrawler crawler)
    {
        crawlerConfigurer.configure(crawler);
    }

    public String getTransferTargetName()
    {
        return transferTargetName;
    }
}
