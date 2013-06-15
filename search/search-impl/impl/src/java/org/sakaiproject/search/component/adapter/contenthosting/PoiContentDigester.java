/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/component/adapter/contenthosting/PoiContentDigester.java $
 * $Id: PoiContentDigester.java 73517 2010-02-15 13:35:25Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008, 2009 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.search.component.adapter.contenthosting;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.sakaiproject.content.api.ContentResource;

/**
 * @author ieb
 */
public class PoiContentDigester extends BaseContentDigester
{
	private static Log log = LogFactory.getLog(PoiContentDigester.class);

	static
	{
		System.setProperty("org.apache.poi.util.POILogger",
				"org.apache.poi.util.NullLogger");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.search.component.adapter.contenthosting.BaseContentDigester#getContent(org.sakaiproject.content.api.ContentResource)
	 */

	public String getContent(ContentResource contentResource)
	{
		log.debug("Digesting with PoiContentDigester");
		
		if (contentResource == null) {
			throw new RuntimeException("Attempt to digest null document!");
		}
		
		if (contentResource != null && contentResource.getContentLength() > maxDigestSize)
		{
			throw new RuntimeException("Attempt to get too much content as a string on "
					+ contentResource.getReference());
		}
		InputStream contentStream = null;

		try
		{
			contentStream = contentResource.streamContent();			
			POITextExtractor DocExt = ExtractorFactory.createExtractor(contentStream);

			return DocExt.getText();
		}
		catch (Exception e)
		{
			log.warn("Poi can't digest: " + contentResource.getId() + " POI returned: " + e);
			throw new RuntimeException("Failed to read content for indexing ", e);
		}
		finally
		{
			if (contentStream != null)
			{
				try
				{
					contentStream.close();
				}
				catch (IOException e)
				{
					log.debug(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.search.component.adapter.contenthosting.BaseContentDigester#getContentReader(org.sakaiproject.content.api.ContentResource)
	 */

	public Reader getContentReader(ContentResource contentResource)
	{
		return new StringReader(getContent(contentResource));
	}

}
