/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/message/branches/message-2.9.x/message-api/api/src/java/org/sakaiproject/message/api/Message.java $
 * $Id: Message.java 59676 2009-04-03 23:18:23Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.message.api;

import org.sakaiproject.entity.api.Entity;

/**
 * <p>
 * Message is the base interface for all Sakai communications messages.
 * </p>
 */
public interface Message extends Entity, Comparable
{
	/**
	 * Access the message header.
	 * 
	 * @return The message header.
	 */
	public MessageHeader getHeader();

	/**
	 * Access the body, as a string.
	 * 
	 * @return The body, as a string.
	 */
	public String getBody();
}
