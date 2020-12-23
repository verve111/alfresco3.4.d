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
package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.MimetypeMap;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParser;

/**
 * Uses {@link http://tika.apache.org/ Apache Tika} and
 *  {@link http://pdfbox.apache.org/ Apache PDFBox} to perform
 *  conversions from PDF documents.
 * 
 * @author Nick Burch
 * @author Derek Hulley
 */
public class PdfBoxContentTransformer extends TikaPoweredContentTransformer
{
    public PdfBoxContentTransformer() {
       super(new String[] {
             MimetypeMap.MIMETYPE_PDF
       });
    }

    @Override
    protected Parser getParser() {
       return new PDFParser();
    }
}
