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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_wcmquickstart.publish.PublishService;
import org.alfresco.module.org_alfresco_module_wcmquickstart.rendition.RenditionHelper;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * ws:webasset aspect behaviours.
 * 
 * @author Brian
 */
public class WebAssetAspect implements WebSiteModel, CopyServicePolicies.OnCopyNodePolicy,
        ContentServicePolicies.OnContentUpdatePolicy, NodeServicePolicies.OnAddAspectPolicy,
        NodeServicePolicies.BeforeDeleteNodePolicy
{
    /** Policy component */
    private PolicyComponent policyComponent;

    /** Node service */
    private NodeService nodeService;

    /** Behaviour filter */
    private BehaviourFilter behaviourFilter;

    /** Publish service */
    private PublishService publishService;

    /** Rendition helper */
    private RenditionHelper renditionHelper;

    /**
     * Set the policy component
     * 
     * @param policyComponent
     *            policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set rendition helper
     * 
     * @param renditionHelper
     *            rendition helper
     */
    public void setRenditionHelper(RenditionHelper renditionHelper)
    {
        this.renditionHelper = renditionHelper;
    }

    /**
     * Set the behaviour filter
     * 
     * @param behaviourFilter
     *            behaviour filter
     */
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    /**
     * Set the publish service
     * 
     * @param publishService
     *            publish service
     */
    public void setPublishService(PublishService publishService)
    {
        this.publishService = publishService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(OnCopyNodePolicy.QNAME, ASPECT_WEBASSET, new JavaBehaviour(this,
                "getCopyCallback"));
        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentUpdatePolicy.QNAME, ASPECT_WEBASSET,
                new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME, ASPECT_WEBASSET,
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ASPECT_WEBASSET,
                new JavaBehaviour(this, "onUpdatePropertiesEachEvent", NotificationFrequency.EVERY_EVENT));
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, ASPECT_WEBASSET,
                new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT));
    }

    /**
     * @see org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy#getCopyCallback(org.alfresco.service.namespace.QName,
     *      org.alfresco.repo.copy.CopyDetails)
     */
    @Override
    public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        return WebAssetAspectCopyBehaviourCallback.INSTANCE;
    }

    /**
     * 
     * @param nodeRef
     * @param before
     * @param after
     */
    public void onUpdatePropertiesEachEvent(NodeRef nodeRef, Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        // If the "available" flag is changing to true, then set the published
        // time to "now".
        Boolean afterAvailable = (Boolean) after.get(PROP_AVAILABLE);
        Boolean beforeAvailable = (Boolean) before.get(PROP_AVAILABLE);
        if (afterAvailable != null && !afterAvailable.equals(beforeAvailable) && afterAvailable)
        {
            behaviourFilter.disableBehaviour(nodeRef, ASPECT_WEBASSET);
            try
            {
                nodeService.setProperty(nodeRef, PROP_PUBLISHED_TIME, new Date());
            }
            finally
            {
                behaviourFilter.enableBehaviour(nodeRef, ASPECT_WEBASSET);
            }
        }
    }

    /**
     * WebAsset aspect copy behaviour callback class
     */
    private static class WebAssetAspectCopyBehaviourCallback extends DefaultCopyBehaviourCallback
    {
        private static final CopyBehaviourCallback INSTANCE = new WebAssetAspectCopyBehaviourCallback();

        @Override
        public Map<QName, Serializable> getCopyProperties(QName classQName, CopyDetails copyDetails,
                Map<QName, Serializable> properties)
        {
            Map<QName, Serializable> propertiesToCopy = new HashMap<QName, Serializable>(properties);
            // We don't want to copy across the original node's record of the
            // website sections it's in.
            // These properties will be calculated afresh on the copy
            propertiesToCopy.remove(PROP_PARENT_SECTIONS);
            propertiesToCopy.remove(PROP_ANCESTOR_SECTIONS);
            return propertiesToCopy;
        }
    }

    /**
     * @see org.alfresco.repo.content.ContentServicePolicies.OnContentUpdatePolicy#onContentUpdate(org.alfresco.service.cmr.repository.NodeRef,
     *      boolean)
     */
    @Override
    public void onContentUpdate(NodeRef nodeRef, boolean newContent)
    {
        if (newContent && nodeService.exists(nodeRef))
        {
            renditionHelper.createRenditions(nodeRef);
        }
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy#onAddAspect(org.alfresco.service.cmr.repository.NodeRef,
     *      org.alfresco.service.namespace.QName)
     */
    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef))
        {
            nodeService.setProperty(nodeRef, PROP_AVAILABLE, Boolean.TRUE);
            nodeService.setProperty(nodeRef, PROP_PUBLISHED_TIME, new Date());
            renditionHelper.createRenditions(nodeRef);
        }
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy#beforeDeleteNode(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        // Enqueue nodes
        publishService.enqueueRemovedNodes(nodeRef);

        // Remove all referencing and referenced associations
        removeAll(nodeService.getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));
        removeAll(nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));
    }

    /**
     * Remove all the associations in the list
     * 
     * @param assocs
     *            list of associations
     */
    private void removeAll(List<AssociationRef> assocs)
    {
        for (AssociationRef assoc : assocs)
        {
            nodeService.removeAssociation(assoc.getSourceRef(), assoc.getTargetRef(), assoc.getTypeQName());
        }
    }
}
