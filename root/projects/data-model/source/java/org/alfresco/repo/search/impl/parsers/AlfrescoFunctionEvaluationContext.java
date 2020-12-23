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
package org.alfresco.repo.search.impl.parsers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.LuceneFunction;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.repo.search.impl.querymodel.FunctionArgument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;
import org.alfresco.repo.search.impl.querymodel.Selector;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Alfrecso function evaluation context for evaluating FTS expressions against lucene.
 * 
 * @author andyh
 */
public class AlfrescoFunctionEvaluationContext implements FunctionEvaluationContext
{
    private static HashSet<String> EXPOSED_FIELDS = new HashSet<String>();

    private NamespacePrefixResolver namespacePrefixResolver;

    private DictionaryService dictionaryService;

    private String defaultNamespace;

    static
    {
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_PATH);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_TEXT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ID);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ISROOT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ISNODE);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_TX);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_PARENT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_PRIMARYPARENT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_QNAME);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_CLASS);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_TYPE);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_EXACTTYPE);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ASPECT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_EXACTASPECT);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ALL);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ISUNSET);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ISNULL);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ISNOTNULL);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_FTSSTATUS);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_ASSOCTYPEQNAME);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_PRIMARYASSOCTYPEQNAME);
        EXPOSED_FIELDS.add(LuceneQueryParser.FIELD_DBID);
    }

    /**
     * @param namespacePrefixResolver
     * @param dictionaryService
     * @param defaultNamespace
     */
    public AlfrescoFunctionEvaluationContext(NamespacePrefixResolver namespacePrefixResolver, DictionaryService dictionaryService, String defaultNamespace)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
        this.dictionaryService = dictionaryService;
        this.defaultNamespace = defaultNamespace;
    }

    public Query buildLuceneEquality(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneExists(LuceneQueryParser lqp, String propertyName, Boolean not) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneGreaterThan(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneGreaterThanOrEquals(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction)
            throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneIn(LuceneQueryParser lqp, String propertyName, Collection<Serializable> values, Boolean not, PredicateMode mode) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneInequality(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneLessThan(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneLessThanOrEquals(LuceneQueryParser lqp, String propertyName, Serializable value, PredicateMode mode, LuceneFunction luceneFunction)
            throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public Query buildLuceneLike(LuceneQueryParser lqp, String propertyName, Serializable value, Boolean not) throws ParseException
    {
        throw new UnsupportedOperationException();
    }

    public String getLuceneSortField(LuceneQueryParser lqp, String propertyName)
    {
        // Score is special
        if (propertyName.equalsIgnoreCase("Score"))
        {
            return "Score";
        }
        String field = getLuceneFieldName(propertyName);
        // need to find the real field to use
        Locale sortLocale = null;
        if (field.startsWith(LuceneQueryParser.PROPERTY_FIELD_PREFIX))
        {
            PropertyDefinition propertyDef = dictionaryService.getProperty(QName.createQName(field.substring(1)));

            // Handle .size and .mimetype
            if(propertyDef == null)
            {   
                if(field.endsWith(LuceneQueryParser.FIELD_SIZE_SUFFIX))
                {
                    propertyDef = dictionaryService.getProperty(QName.createQName(field.substring(1, field.length()-LuceneQueryParser.FIELD_SIZE_SUFFIX.length())));
                    if (!propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                    {
                        throw new FTSQueryException("Order for "+LuceneQueryParser.FIELD_SIZE_SUFFIX+" only supported on content properties");
                    }
                    else
                    {
                        return field;
                    }
                }
                else if (field.endsWith(LuceneQueryParser.FIELD_MIMETYPE_SUFFIX))
                {
                    propertyDef = dictionaryService.getProperty(QName.createQName(field.substring(1, field.length()-LuceneQueryParser.FIELD_MIMETYPE_SUFFIX.length())));
                    if (!propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                    {
                        throw new FTSQueryException("Order for .mimetype only supported on content properties");
                    }
                    else
                    {
                        return field;
                    }
                }
                else
                {
                    return field;
                }
            }
            else
            {
                if (propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {
                    throw new FTSQueryException("Order on content properties is not curently supported");
                }
                else if ((propertyDef.getDataType().getName().equals(DataTypeDefinition.MLTEXT)) || (propertyDef.getDataType().getName().equals(DataTypeDefinition.TEXT)))
                {
                    List<Locale> locales = lqp.getSearchParameters().getLocales();
                    if (((locales == null) || (locales.size() == 0)))
                    {
                        locales = Collections.singletonList(I18NUtil.getLocale());
                    }

                    if (locales.size() > 1)
                    {
                        throw new FTSQueryException("Order on text/mltext properties with more than one locale is not curently supported");
                    }

                    sortLocale = locales.get(0);
                    // find best field match

                    HashSet<String> allowableLocales = new HashSet<String>();
                    MLAnalysisMode analysisMode = lqp.getDefaultSearchMLAnalysisMode();
                    for (Locale l : MLAnalysisMode.getLocales(analysisMode, sortLocale, false))
                    {
                        allowableLocales.add(l.toString());
                    }

                    String sortField = field;

                    for (Object current : lqp.getIndexReader().getFieldNames(FieldOption.INDEXED))
                    {
                        String currentString = (String) current;
                        if (currentString.startsWith(field) && currentString.endsWith(LuceneQueryParser.FIELD_SORT_SUFFIX))
                        {
                            String fieldLocale = currentString.substring(field.length() + 1, currentString.length() - 5);
                            if (allowableLocales.contains(fieldLocale))
                            {
                                if (fieldLocale.equals(sortLocale.toString()))
                                {
                                    sortField = currentString;
                                    break;
                                }
                                else if (sortLocale.toString().startsWith(fieldLocale))
                                {
                                    if (sortField.equals(field) || (currentString.length() < sortField.length()))
                                    {
                                        sortField = currentString;
                                    }
                                }
                                else if (fieldLocale.startsWith(sortLocale.toString()))
                                {
                                    if (sortField.equals(field) || (currentString.length() < sortField.length()))
                                    {
                                        sortField = currentString;
                                    }
                                }
                            }
                        }
                    }

                    field = sortField;

                }
                else if (propertyDef.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDef.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        field = field + LuceneQueryParser.FIELD_SORT_SUFFIX;
                    }
                }
            }
        }
        return field;
    }

    public Map<String, NodeRef> getNodeRefs()
    {
        throw new UnsupportedOperationException();
    }

    public NodeService getNodeService()
    {
        throw new UnsupportedOperationException();
    }

    public Serializable getProperty(NodeRef nodeRef, String propertyName)
    {
        throw new UnsupportedOperationException();
    }

    public Float getScore()
    {
        throw new UnsupportedOperationException();
    }

    public Map<String, Float> getScores()
    {
        throw new UnsupportedOperationException();
    }

    public boolean isObjectId(String propertyName)
    {
        return false;
    }

    public boolean isOrderable(String fieldName)
    {
        return true;
    }

    public boolean isQueryable(String fieldName)
    {
        return true;
    }

    public String getLuceneFieldName(String propertyName)
    {
        if (propertyName.startsWith(LuceneQueryParser.PROPERTY_FIELD_PREFIX))
        {
            // Leave it to the query parser to expand
            return propertyName;
        }

        if (propertyName.startsWith("{"))
        {
            QName fullQName = QName.createQName(propertyName);
            QName propertyQName = stripSuffixes(fullQName);
            if (dictionaryService.getProperty(propertyQName) != null)
            {
                return LuceneQueryParser.PROPERTY_FIELD_PREFIX + fullQName.toString();
            }
            else
            {
                throw new FTSQueryException("Unknown property: " + fullQName.toString());
            }
        }

        int index = propertyName.indexOf(':');
        if (index != -1)
        {
            // Try as a property, if invalid pass through
            QName fullQName = QName.createQName(propertyName, namespacePrefixResolver);
            QName propertyQName = stripSuffixes(fullQName);
            if (dictionaryService.getProperty(propertyQName) != null)
            {
                return LuceneQueryParser.PROPERTY_FIELD_PREFIX + fullQName.toString();
            }
            else
            {
                throw new FTSQueryException("Unknown property: " + fullQName.toString());
            }
        }

        index = propertyName.indexOf('_');
        if (index != -1)
        {
            // Try as a property, if invalid pass through
            QName fullQName = QName.createQName(propertyName.substring(0, index), propertyName.substring(index + 1), namespacePrefixResolver);
            QName propertyQName = stripSuffixes(fullQName);
            if (dictionaryService.getProperty(propertyQName) != null)
            {
                return LuceneQueryParser.PROPERTY_FIELD_PREFIX + fullQName.toString();
            }
            else
            {
                throw new FTSQueryException("Unknown property: " + fullQName.toString());
            }
        }

        if (EXPOSED_FIELDS.contains(propertyName))
        {
            return propertyName;
        }

        QName fullQName = QName.createQName(defaultNamespace, propertyName);
        QName propertyQName = stripSuffixes(fullQName);
        if (dictionaryService.getProperty(propertyQName) != null)
        {
            return LuceneQueryParser.PROPERTY_FIELD_PREFIX + fullQName.toString();
        }
        else
        {
            throw new FTSQueryException("Unknown property: " + fullQName.toString());
        }

    }
    
    public QName stripSuffixes(QName qname)
    {
        String field = qname.toString();
        if(field.endsWith(LuceneQueryParser.FIELD_SIZE_SUFFIX))
        {
            QName propertyField = QName.createQName(field.substring(0, field.length()-LuceneQueryParser.FIELD_SIZE_SUFFIX.length()));
            PropertyDefinition propertyDef = dictionaryService.getProperty(propertyField);
            if (!propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
            {
                throw new FTSQueryException(LuceneQueryParser.FIELD_SIZE_SUFFIX+" only supported on content properties");
            }
            else
            {
                return propertyField;
            }
        }
        else if(field.endsWith(LuceneQueryParser.FIELD_LOCALE_SUFFIX))
        {
            QName propertyField = QName.createQName(field.substring(0, field.length()-LuceneQueryParser.FIELD_LOCALE_SUFFIX.length()));
            PropertyDefinition propertyDef = dictionaryService.getProperty(propertyField);
            if (!propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
            {
                throw new FTSQueryException(LuceneQueryParser.FIELD_LOCALE_SUFFIX+" only supported on content properties");
            }
            else
            {
                return propertyField;
            }
        }
        else if(field.endsWith(LuceneQueryParser.FIELD_MIMETYPE_SUFFIX))
        {
            QName propertyField = QName.createQName(field.substring(0, field.length()-LuceneQueryParser.FIELD_MIMETYPE_SUFFIX.length()));
            PropertyDefinition propertyDef = dictionaryService.getProperty(propertyField);
            if (!propertyDef.getDataType().getName().equals(DataTypeDefinition.CONTENT))
            {
                throw new FTSQueryException(LuceneQueryParser.FIELD_MIMETYPE_SUFFIX+" only supported on content properties");
            }
            else
            {
                return propertyField;
            }
        }
        else
        {
            return qname;
        }
    }
    
    public LuceneFunction getLuceneFunction(FunctionArgument functionArgument)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#checkFieldApplies(org.alfresco.service.namespace
     * .QName, java.lang.String)
     */
    public void checkFieldApplies(Selector selector, String propertyName)
    {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext#isMultiValued(java.lang.String)
     */
    public boolean isMultiValued(String propertyName)
    {
        throw new UnsupportedOperationException();
    }

}
