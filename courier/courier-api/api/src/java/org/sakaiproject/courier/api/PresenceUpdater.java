/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/courier/branches/courier-2.9.x/courier-api/api/src/java/org/sakaiproject/courier/api/PresenceUpdater.java $
 * $Id: PresenceUpdater.java 101763 2011-12-14 19:43:07Z botimer@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation
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

package org.sakaiproject.courier.api;

/**
 * <p>
 * The PresenceUpdater interface is a simple mechanism to access the PresenceService
 * without a cross-module dependency. It only exposes the setPresence method, which
 * is the extent of Courier's interaction with Presence.
 * </p>
 */
public interface PresenceUpdater
{
	/**
	 * Establish or refresh the presence of the current session in a location.
	 * 
	 * @param locationId
	 *        A presence location id.
	 */
	void setPresence(String locationId);
}
