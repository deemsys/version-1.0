/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/sections-service/sections-impl/standalone/src/java/org/sakaiproject/component/section/InstructorRecordImpl.java $
 * $Id: InstructorRecordImpl.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
package org.sakaiproject.component.section;

import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.facade.Role;

/**
 * A detachable InstructorRecord for persistent storage.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class InstructorRecordImpl extends ParticipationRecordImpl {

	private static final long serialVersionUID = 1L;

	/**
	 * No-arg constructor needed for hibernate
	 */
	public InstructorRecordImpl() {		
	}
	
	public InstructorRecordImpl(LearningContext learningContext, User user) {
		this.learningContext = learningContext;
		this.user = user;
	}

	public Role getRole() {
		return Role.INSTRUCTOR;
	}
}

