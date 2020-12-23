/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.jcr.importer;

import org.alfresco.repo.importer.view.ElementContext;
import org.alfresco.service.namespace.QName;

/**
 * Maintains state about currently imported value
 * 
 * @author David Caruana
 */
public class ValueContext extends ElementContext
{
    private PropertyContext property;

    
    /**
     * Construct
     * 
     * @param elementName
     * @param property
     */
    public ValueContext(QName elementName, PropertyContext property)
    {
        super(elementName, property.getDictionaryService(), property.getImporter());
        this.property = property;
    }

    /**
     * Get property
     * 
     * @return  property holding value
     */
    public PropertyContext getProperty()
    {
        return property;
    }
    
}
