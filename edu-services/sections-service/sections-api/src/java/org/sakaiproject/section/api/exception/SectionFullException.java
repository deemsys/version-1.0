/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/sections-service/sections-api/src/java/org/sakaiproject/section/api/exception/SectionFullException.java $
 * $Id: SectionFullException.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2007, 2008 The Sakai Foundation
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
package org.sakaiproject.section.api.exception;

/**
 * Indicates that a section is full, and the attempt to add a user to it
 * was therefore unsuccessful. 
 * 
 * @author <a href="mailto:stephen.marquard@uct.ac.za">Stephen Marquard</a>
 *
 */
public class SectionFullException extends Exception {

	private static final long serialVersionUID = 1L;

	public SectionFullException(String message) {
    	super(message);
    }
}
