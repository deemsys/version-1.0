/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/api/src/main/java/org/sakaiproject/id/api/IdManager.java $
 * $Id: IdManager.java 51317 2008-08-24 04:38:02Z csev@umich.edu $
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

package org.sakaiproject.id.api;

/**
 * <p>
 * IdManager is the work interface for the Sakai Id Manager API to generates unique ids.
 * </p>
 */
public interface IdManager
{
	/**
	 * Allocate a unique Id.
	 * 
	 * @return A unique Id.
	 */
	String createUuid();
}