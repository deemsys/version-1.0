/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/api/src/main/java/org/sakaiproject/content/api/ContentCollectionEdit.java $
 * $Id: ContentCollectionEdit.java 76321 2010-04-19 10:09:10Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.content.api;

import java.util.Map;

import org.sakaiproject.entity.api.Edit;

/**
* <p>ContentCollectionEdit is an editable ContentCollection.</p>
*/
public interface ContentCollectionEdit
	extends ContentCollection, Edit, GroupAwareEdit
{

	/**
	 * @param priorities
	 */
	void setPriorityMap(Map<String, Integer> priorities);
	
}	// ContentCollectionEdit



