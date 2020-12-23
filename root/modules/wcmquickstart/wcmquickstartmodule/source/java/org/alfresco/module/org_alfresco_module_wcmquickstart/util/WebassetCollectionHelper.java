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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import java.util.Calendar;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class WebassetCollectionHelper implements WebSiteModel
{
    private static final Log log = LogFactory.getLog(WebassetCollectionHelper.class);
    
    private NodeService nodeService;
    private SearchService searchService;
    private NamespaceService namespaceService;
    private ContextParserService contextParserService;
    private String searchStore = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.toString();
    
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
     * Set the search service
     * 
     * @param searchService
     *            search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the search store, must be a valid store reference string
     * 
     * @param searchStore
     *            search store
     */
    public void setSearchStore(String searchStore)
    {
        this.searchStore = searchStore;
    }

    /**
     * 
     * @param contextParserService
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }

    /**
     * Clear collection
     * 
     * @param collection
     *            collection node reference
     */
    public void clearCollection(NodeRef collection)
    {
        List<AssociationRef> assocs = nodeService.getTargetAssocs(collection, ASSOC_WEBASSETS);
        for (AssociationRef assoc : assocs)
        {
            nodeService.removeAssociation(collection, assoc.getTargetRef(), ASSOC_WEBASSETS);
        }
    }

    /**
     * Refresh collection, clears all current members of the collection.
     * 
     * @param collection
     *            collection node reference
     */
    public void refreshCollection(NodeRef collection)
    {
        // Get the query language and max query size
        String queryLanguage = (String) nodeService.getProperty(collection, PROP_QUERY_LANGUAGE);
        String query = (String) nodeService.getProperty(collection, PROP_QUERY);
        Integer minsToRefresh = ((Integer) nodeService.getProperty(collection, PROP_MINS_TO_QUERY_REFRESH));
        minsToRefresh = minsToRefresh == null ? 30 : minsToRefresh;
        Integer maxQuerySize = ((Integer) nodeService.getProperty(collection, PROP_QUERY_RESULTS_MAX_SIZE));
        maxQuerySize = maxQuerySize == null ? 5 : maxQuerySize;

        if (query != null && query.trim().length() != 0)
        {
            // Clear the contents of the content collection
            clearCollection(collection);

            // Parse the query string
            query = contextParserService.parse(collection, query);

            SearchParameters searchParameters = new SearchParameters();

            if (queryLanguage.equals(SearchService.LANGUAGE_LUCENE))
            {
                //handle additional support for Lucene ordering with ORDER_ASC and ORDER_DESC
                String[] queryParts = query.split("\\s");
                for (String queryPart : queryParts)
                {
                    int firstColonIndex = queryPart.indexOf(':');
                    if (firstColonIndex == -1)
                    {
                        continue;
                    }
                    String name = queryPart.substring(0, firstColonIndex);
                    String value = (firstColonIndex < (queryPart.length() + 1)) ? queryPart.substring(firstColonIndex+1) : "";
                    boolean orderAscending = "ORDER_ASC".equals(name) || "ORDER".equals(name);
                    boolean orderDescending = "ORDER_DESC".equals(name);
                    if (!orderAscending && !orderDescending)
                    {
                        continue;
                    }
                    QName property = parsePropertyName(value);
                    if (property != null)
                    {
                        String sort = "@" + property.toString();
                        if (log.isDebugEnabled())
                        {
                            log.debug("Adding sort order: " + sort + (orderAscending ? " ASC" : " DESC"));
                        }
                        searchParameters.addSort(sort, orderAscending);
                    }
                }
                
            }

            // Build the query parameters
            searchParameters.addStore(new StoreRef(searchStore));
            searchParameters.setLanguage(queryLanguage);
            searchParameters.setMaxItems(maxQuerySize);
            searchParameters.setQuery(query);
            

            try
            {
                // Execute the query
                ResultSet resultSet = searchService.query(searchParameters);

                // Iterate over the results of the query
                int resultCount = 0;
                for (NodeRef result : resultSet.getNodeRefs())
                {
                    if (maxQuerySize < 1 || resultCount < maxQuerySize)
                    {
                        // Only add associations to webassets
                        if (nodeService.hasAspect(result, ASPECT_WEBASSET) == true)
                        {
                            nodeService.createAssociation(collection, result, ASSOC_WEBASSETS);
                        }
                        resultCount++;
                    }
                    else
                    {
                        break;
                    }
                }

                // Set the refreshAt property
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MINUTE, minsToRefresh);
                nodeService.setProperty(collection, PROP_REFRESH_AT, now.getTime());

            }
            catch (AlfrescoRuntimeException e)
            {
                // Rethrow
                throw new AlfrescoRuntimeException("Invalid collection query.  Please check query for syntax errors.",
                        e);
            }
        }
    }

    private QName parsePropertyName(String value)
    {
        QName result = null;
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Attempting to parse property name: " + value);
            }
            StringBuilder sb = new StringBuilder();
            char[] valueArray = value.toCharArray();
            for (char ch : valueArray)
            {
                switch (ch)
                {
                case '\"':
                    break;
                    
                default:
                    sb.append(ch);
                    break;
                }
            }
            result = QName.createQName(sb.toString(), namespaceService);
        }
        catch(InvalidQNameException ex)
        {
        }
        return result;
    }

}