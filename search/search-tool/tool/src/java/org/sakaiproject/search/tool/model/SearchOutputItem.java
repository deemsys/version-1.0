/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-tool/tool/src/java/org/sakaiproject/search/tool/model/SearchOutputItem.java $
 * $Id: SearchOutputItem.java 59685 2009-04-03 23:36:24Z arwhyte@umich.edu $
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

package org.sakaiproject.search.tool.model;

/**
 * @author ieb
 */
public interface SearchOutputItem
{

	/**
	 * the tool the hit comes from
	 * @return
	 */
	String getTool();

	/**
	 * the url for the hit
	 * @return
	 */
	String getUrl();

	/**
	 * the title of the hit
	 * @return
	 */
	String getTitle();

	/**
	 * the highlighted content of the hit
	 * @return
	 */
	String getSearchResult();
	
	String getSiteTitle();
	
	String getSiteURL();
	/**
	 * This item is visible to to the user viewing - used for optional rendering in the tool view
	 * @return
	 */
	boolean isVisible();
	
	/**
	 * This item has a proper portal view url
	 * @return
	 */
	boolean hasPortalUrl();

}
