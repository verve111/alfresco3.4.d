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
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>. */
package org.alfresco.web.pr;

import java.util.*;
import javax.servlet.jsp.PageContext;
import org.w3c.dom.*;
import org.alfresco.web.forms.*;

/**
 * Bean for getting data for company footers which are included within press release forms.
 * It's used by /media/releases/get_company_footer_choices_simple_type.jsp to aggregate all
 * forms created by press-release.xsd with company_footer as the root tag name
 * in /media/releases/content and generate an xsd simpleType enumeration which is used within
 * the press release form.
 * press-release.xsl then uses the selected company footers and loads the xml assets and
 * includes their content within the generated press release renditions.
 */
public class CompanyFooterBean
{

   /**
    * Loads all xml files generated by company-footer.xsd in /media/releases/content
    * populates CompanyFooterBeans with their contents.  This function is exposed to the
    * jsp by /WEB-INF/pr.tld.
    *
    * @param pageContext the page context from the jsp, needed for accessing the 
    * servlet context for the ServletContextFormDataFunctionsAdapter class.
    *
    * @return a list of populated CompanyFooterBeans.
    */
   public static List<CompanyFooterBean> getCompanyFooterChoices(final PageContext pageContext)
      throws Exception
   {
      final FormDataFunctions ef = 
         new ServletContextFormDataFunctionsAdapter(pageContext.getServletContext());

      final Map<String, Document> entries = 
         ef.parseXMLDocuments("company-footer", "/media/releases/content");
      final List<CompanyFooterBean> result = new ArrayList<CompanyFooterBean>(entries.size());
      for (Map.Entry<String, Document> entry : entries.entrySet())
      {
         final String fileName = entry.getKey();
         final Document d = entry.getValue();
         final Element n = (Element)d.getElementsByTagName("pr:name").item(0);
         result.add(new CompanyFooterBean(n.getFirstChild().getNodeValue(),
                                          fileName));
      }
      return result;
   }

   private final String name;
   private final String fileName;

   public CompanyFooterBean(final String name, 
                            final String fileName)
   {
      this.name = name;
      this.fileName = fileName;
   }

   /**
    * Returns the name of the company.
    */
   public String getName() 
   { 
      return this.name; 
   }

   /**
    * Returns the fileName of the xml file describing this company footer
    *
    * @return the fileName of the xml file.
    */
   public String getFileName()
   {
      return this.fileName;
   }
}