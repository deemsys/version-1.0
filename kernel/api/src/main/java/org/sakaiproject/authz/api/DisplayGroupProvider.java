/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/api/src/main/java/org/sakaiproject/authz/api/DisplayGroupProvider.java $
 * $Id: DisplayGroupProvider.java 68306 2009-10-27 21:09:19Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 Sakai Foundation
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
package org.sakaiproject.authz.api;

/**
 * Allow for nice displaynames of groups provided to a site.
 * @author buckett
 *
 */
public interface DisplayGroupProvider {

	/**
	 * Get the nice display name for the provided group.
	 * @param groupId The group ID.
	 * @return A name to display to the user for the supplied group.
	 */
	public String getGroupName(String groupId);
}
