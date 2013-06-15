/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/branches/samigo-2.9.x/samigo-app/src/java/org/sakaiproject/tool/assessment/ui/listener/author/RemoveAssessmentThread.java $
 * $Id: RemoveAssessmentThread.java 118578 2013-01-22 16:55:32Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005, 2006, 2008 The Sakai Foundation
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

package org.sakaiproject.tool.assessment.ui.listener.author;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * Remove an assessment on a background thread so it's not taking time from the initiating end-user request thread.
 * 
 * @author Ed Smiley
 */
public class RemoveAssessmentThread extends Thread
{
	private static Log log = LogFactory.getLog(RemoveAssessmentThread.class);

	private String assessmentId;

	private String userId;
	
	private String context;

	public RemoveAssessmentThread(String assessmentId, String userId, String context)
	{
		this.assessmentId = assessmentId;
		this.userId = userId;
		this.context = context;
	}

	public void run()
	{
		try
		{
			// bind the current user to the thread's session (created if needed) so security etc. works if called
			Session s = SessionManager.getCurrentSession();
			if (s != null)
			{
				s.setUserId(userId);
			}
			else
			{
				log.warn("run - no SessionManager.getCurrentSession, cannot set user");
			}

			AssessmentService assessmentService = new AssessmentService();
			log.info("** remove assessmentId= " + this.assessmentId + " in context: " + context);
			assessmentService.removeAssessment(this.assessmentId);
			//SAM-2004 we need to set the context
			
			
			EventTrackingService.post(EventTrackingService.newEvent("sam.assessment.remove", "assessmentId=" + assessmentId, context, true, NotificationService.NOTI_NONE));
		      
		}
		finally
		{
			// clear out any current thread bound objects
			ThreadLocalManager.clear();
		}
	}
}
