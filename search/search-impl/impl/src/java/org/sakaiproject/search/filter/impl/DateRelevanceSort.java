/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/filter/impl/DateRelevanceSort.java $
 * $Id: DateRelevanceSort.java 59685 2009-04-03 23:36:24Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.search.filter.impl;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.sakaiproject.search.api.SearchService;

/**
 * Sorts by date stamp then relevance in revese order (newst earliest)
 * @author ieb
 *
 */
public class DateRelevanceSort extends Sort
{
	public DateRelevanceSort() {
		super(new SortField[] {
				new SortField(SearchService.DATE_STAMP,true),
				SortField.FIELD_SCORE
		});
	}
}
