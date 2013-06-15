/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/providers/branches/sakai-2.9.x/jldap/src/java/edu/amc/sakai/user/InvalidEmailAddressException.java $
 * $Id: InvalidEmailAddressException.java 61856 2009-05-05 17:53:41Z dmccallum@unicon.net $
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

package edu.amc.sakai.user;

@SuppressWarnings("serial")
public class InvalidEmailAddressException extends RuntimeException {

	public InvalidEmailAddressException() {
		super();
	}
	public InvalidEmailAddressException(String message) {
		super(message);
	}
	public InvalidEmailAddressException(String message, Throwable cause) {
		super(message,cause);
	}
	public InvalidEmailAddressException(Throwable cause) {
		super(cause);
	}
	
}
