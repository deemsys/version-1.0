/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/sections-service/sections-impl/sakai/model/src/java/org/sakaiproject/component/section/sakai/GroupParticipantImpl.java $
 * $Id: GroupParticipantImpl.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
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

import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.facade.Role;

public class GroupParticipantImpl extends ParticipationRecordImpl implements Serializable{

	private static final long serialVersionUID = 1L;

	protected Role role;
	
	public GroupParticipantImpl(LearningContext learningContext, User user, Role role) {
		this.learningContext = learningContext;
		this.user = user;
		this.userUid =user.getUserUid();
		this.role = role;
	}

	public Role getRole() {
		return role;
	}
}
