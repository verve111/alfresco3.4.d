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

package org.alfresco.repo.forms.processor.node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;

/**
 * Mock implementation of the repository ClassDefinition.
 * 
 * @since 3.4
 * @author Nick Smith
 */
public class MockClassAttributeDefinition implements PropertyDefinition, AssociationDefinition
{

    private final QName name;
    private DataTypeDefinition dataType = mock(DataTypeDefinition.class);
    private ClassDefinition targetClass = mock(ClassDefinition.class);
    private String description = null;
    private String defaultValue = null;
    private String title = null;

    private boolean targetMandatory = false;
    private boolean targetMany = false;
    private boolean isProtected = false;
    private boolean mandatory = false;
    private boolean multiValued = false;

    private MockClassAttributeDefinition(QName name)
    {
        this.name = name;
    }

    private MockClassAttributeDefinition(QName name, String title, String description, boolean isProtected)
    {
        this(name);
        this.title = title;
        this.description = description;
        this.isProtected = isProtected;
    }

    public static MockClassAttributeDefinition mockPropertyDefinition(QName name, QName dataTypeName)
    {
        MockClassAttributeDefinition mock = new MockClassAttributeDefinition(name);
        mockDataTypeName(dataTypeName, mock);
        return mock;
    }
    
    public static MockClassAttributeDefinition mockPropertyDefinition(QName name, QName dataTypeName, String defaultValue)
    {
        MockClassAttributeDefinition mock = new MockClassAttributeDefinition(name);
        mockDataTypeName(dataTypeName, mock);
        mock.defaultValue = defaultValue;
        return mock;
    }

    public static MockClassAttributeDefinition mockPropertyDefinition(QName name,// 
                QName dataTypeName,//
                String title,//
                String description,//
                boolean isProtected,//
                String defaultValue,//
                boolean Mandatory,//
                boolean multiValued)
    {
        MockClassAttributeDefinition mock = new MockClassAttributeDefinition(name, title, description, isProtected);
        mockDataTypeName(dataTypeName, mock);
        mock.defaultValue = defaultValue;
        mock.mandatory = Mandatory;
        mock.multiValued = multiValued;
        return mock;
    }

    public static MockClassAttributeDefinition mockAssociationDefinition(QName name, QName targetClassName)
    {
        MockClassAttributeDefinition mock = new MockClassAttributeDefinition(name);
        mockTargetClassName(targetClassName, mock);
        return mock;
    }

    public static MockClassAttributeDefinition mockAssociationDefinition(QName name,// 
                QName targetClassName,//
                String title,//
                String description,//
                boolean isProtected,//
                boolean targetMandatory,//
                boolean targetMany)
    {
        MockClassAttributeDefinition mock = new MockClassAttributeDefinition(name, title, description, isProtected);
        mockTargetClassName(targetClassName, mock);
        mock.targetMandatory = targetMandatory;
        mock.targetMany = targetMany;
        return mock;
    }

    private static void mockDataTypeName(QName dataTypeName, MockClassAttributeDefinition mock)
    {
        when(mock.dataType.getName()).thenReturn(dataTypeName);
    }

    private static void mockTargetClassName(QName targetClassName, MockClassAttributeDefinition mock)
    {
        when(mock.targetClass.getName()).thenReturn(targetClassName);
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getConstraints()
     */
    public List<ConstraintDefinition> getConstraints()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getContainerClass
     * ()
     */
    public ClassDefinition getContainerClass()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getDataType()
     */
    public DataTypeDefinition getDataType()
    {
        return dataType;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getDefaultValue()
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getIndexTokenisationMode()
     */
    public IndexTokenisationMode getIndexTokenisationMode()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getModel()
     */
    public ModelDefinition getModel()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getName()
     */
    public QName getName()
    {
        return name;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isIndexed()
     */
    public boolean isIndexed()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isIndexedAtomically()
     */
    public boolean isIndexedAtomically()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isMandatory()
     */
    public boolean isMandatory()
    {
        return mandatory;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isMandatoryEnforced()
     */
    public boolean isMandatoryEnforced()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return multiValued;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isOverride()
     */
    public boolean isOverride()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return isProtected;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.PropertyDefinition#isStoredInIndex()
     */
    public boolean isStoredInIndex()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getSourceClass()
     */
    public ClassDefinition getSourceClass()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getSourceRoleName()
     */
    public QName getSourceRoleName()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getTargetClass()
     */
    public ClassDefinition getTargetClass()
    {
        return targetClass;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getTargetRoleName()
     */
    public QName getTargetRoleName()
    {
        return null;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isChild()
     */
    public boolean isChild()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isSourceMandatory()
     */
    public boolean isSourceMandatory()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isSourceMany()
     */
    public boolean isSourceMany()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isTargetMandatory()
     */
    public boolean isTargetMandatory()
    {
        return targetMandatory;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isTargetMandatoryEnforced()
     */
    public boolean isTargetMandatoryEnforced()
    {
        return false;
    }

    /*
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#isTargetMany()
     */
    public boolean isTargetMany()
    {
        return targetMany;
    }

}
