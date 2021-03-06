/**
 * $Id: EntityEncodingException.java 59674 2009-04-03 23:05:58Z arwhyte@umich.edu $
 * $URL: https://source.sakaiproject.org/svn/entitybroker/branches/entitybroker-1.5.x/api/src/java/org/sakaiproject/entitybroker/exception/EntityEncodingException.java $
 * EntityEncodingException.java - entity-broker - Apr 30, 2008 5:33:26 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 The Sakai Foundation
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
 */

package org.sakaiproject.entitybroker.exception;


/**
 * Throw to indicate that there was a failure during encoding/decoding an entity or related data
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public class EntityEncodingException extends EntityBrokerException {

   public EntityEncodingException(String message, String entityReference) {
      super(message, entityReference);
   }

   public EntityEncodingException(String message, String entityReference, Throwable cause) {
       super(message, entityReference, cause);
   }

}
