/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-api/api/src/java/org/sakaiproject/search/api/InvalidSearchQueryException.java $
 * $Id: InvalidSearchQueryException.java 75617 2010-04-05 09:51:02Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.search.api;


/**
 * An exception indicating that the underlying service was not able to pase the 
 * Query string as a valid query
 * @author dhorwitz
 *
 */
public class InvalidSearchQueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7518554001559319912L;

	public InvalidSearchQueryException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
