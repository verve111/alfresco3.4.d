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
package org.alfresco.web.ui.common.tag;

import javax.servlet.jsp.PageContext;

import org.alfresco.web.ui.common.converter.XMLDateConverter;
import org.apache.myfaces.taglib.core.ConvertDateTimeTag;

/**
 * Tag definition to use the XMLDateConverter on a page 
 *  
 * @author gavinc
 */
public class XMLDateConverterTag extends ConvertDateTimeTag
{
   public void setPageContext(PageContext context) 
   {
      super.setPageContext(context);
      setConverterId(XMLDateConverter.CONVERTER_ID);
   }
}
