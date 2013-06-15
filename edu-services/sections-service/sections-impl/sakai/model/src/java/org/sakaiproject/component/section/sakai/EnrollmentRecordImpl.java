/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/sections-service/sections-impl/sakai/model/src/java/org/sakaiproject/component/section/sakai/EnrollmentRecordImpl.java $
 * $Id: EnrollmentRecordImpl.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
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
package org.sakaiproject.component.section.sakai;

import java.io.Serializable;

import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.facade.Role;

/**
 * A detachable EnrollmentRecord for persistent storage.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class EnrollmentRecordImpl extends ParticipationRecordImpl implements EnrollmentRecord, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String status;

	/**
	 * No-arg constructor needed for hibernate
	 */
	public EnrollmentRecordImpl() {		
	}
	
	public EnrollmentRecordImpl(LearningContext learningContext, String status, User user) {
		this.learningContext = learningContext;
		this.status = status;
		this.user = user;
		this.userUid = user.getUserUid();
	}

	public Role getRole() {
		return Role.STUDENT;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
