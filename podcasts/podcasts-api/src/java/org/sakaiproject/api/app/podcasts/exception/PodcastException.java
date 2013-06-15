/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/podcasts/branches/sakai-2.9.x/podcasts-api/src/java/org/sakaiproject/api/app/podcasts/exception/PodcastException.java $
 * $Id: PodcastException.java 89238 2011-03-03 10:20:55Z david.horwitz@uct.ac.za $
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


package org.sakaiproject.api.app.podcasts.exception;

public class PodcastException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8224145440816592584L;

	public PodcastException() {
		super();
	}
	
	public PodcastException(Throwable cause) {
		super(cause);
	}
	
}
