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
package org.alfresco.wcm.client.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Asset interface implementation
 * 
 * @author Roy Wetherall
 * @author Brian
 */
public class AssetImpl extends ResourceBaseImpl implements Asset
{
    private static final long serialVersionUID = 1L;

    private Map<String, List<String>> relationships = null;
    private Map<String, List<Asset>> relatedAssets;

    private List<String> parentSectionIds = Collections.emptyList();

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAssets()
     */
    @Override
    public Map<String, List<Asset>> getRelatedAssets()
    {
        /*
         * Note: This method call is expensive if used for every asset in a
         * collection as a query is performed. In mitigation the results are
         * cached within the object.
         */
        if (relatedAssets == null)
        {
            Map<String, List<Asset>> assetMap = new TreeMap<String, List<Asset>>();
            for (Entry<String, List<String>> entry : getRelationships().entrySet())
            {
                List<String> relatedAssetIds = entry.getValue();
                if (relatedAssetIds != null)
                {
                    List<Asset> assets = getAssetFactory().getAssetsById(relatedAssetIds);
                    if (assets.size() > 0)
                        assetMap.put(entry.getKey(), assets);
                }
            }
            relatedAssets = assetMap;
        }
        return relatedAssets;
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAssets(String)
     */
    @Override
    public List<Asset> getRelatedAssets(String relationshipName)
    {
        List<String> relatedAssetIds = getRelationships().get(relationshipName);
        if (relatedAssetIds == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return getAssetFactory().getAssetsById(relatedAssetIds);
        }
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getRelatedAsset(String)
     */
    @Override
    public Asset getRelatedAsset(String relationshipName)
    {
        Asset result = null;
        List<String> relatedAssetIds = getRelationships().get(relationshipName);
        if (relatedAssetIds != null && !relatedAssetIds.isEmpty())
        {
            result = getAssetFactory().getAssetById(relatedAssetIds.get(0));
        }
        return result;
    }

    /**
     * Set the parent sections id's
     * 
     * @param sectionIds
     *            collection of parent section id
     */
    public void setParentSectionIds(Collection<String> sectionIds)
    {
        this.parentSectionIds = new ArrayList<String>(sectionIds);
        if (sectionIds.size() > 0)
        {
            setPrimarySectionId(parentSectionIds.get(0));
        }
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getTags()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getTags()
    {
        return (List<String>) getProperties().get(PROPERTY_TAGS);
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getMimetype()
     */
    @Override
    public String getMimeType()
    {
        return (String) getProperties().get(PropertyIds.CONTENT_STREAM_MIME_TYPE);
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getSize()
     */
    @Override
    public long getSize()
    {
        BigInteger streamLength = (BigInteger) getProperties().get(PropertyIds.CONTENT_STREAM_LENGTH);
        if (streamLength == null)
            return 0;
        return streamLength.longValue();
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getContentAsInputStream()
     */
    @Override
    public ContentStream getContentAsInputStream()
    {
        // Get the request thread's session
        Session session = CmisSessionHelper.getSession();

        // Fetch the Document object for this asset
        CmisObject object = session.getObject(new ObjectIdImpl(getId()));
        if (!(object instanceof Document))
        {
            throw new IllegalArgumentException("Object referenced by the uuid is not a document");
        }
        Document doc = (Document) object;
        if (doc == null)
            return null;

        // Return the content as a stream
        return new ContentStreamCmisImpl(doc.getContentStream());
    }

    /**
     * @see org.alfresco.wcm.client.Asset#getTemplate()
     */
    @Override
    public String getTemplate()
    {
        String template = null;

        // Only "text" assets have templates associated with them
        String mimeType = getMimeType();
        if ((mimeType != null) && mimeType.startsWith("text"))
        {
            template = (String)properties.get(PROPERTY_TEMPLATE_NAME);
            if ((template == null) || template.trim().length() == 0)
            {
                Section section = getContainingSection();
                template = section.getTemplate(getType());
            }
        }
        return template;
    }

    private Map<String, List<String>> getRelationships()
    {
        if (relationships == null)
        {
            relationships = getAssetFactory().getSourceRelationships(getId());
        }
        return relationships;
    }

    @Override
    public Map<String, Rendition> getRenditions()
    {
        return assetFactory.getRenditions(getId());
    }
}
