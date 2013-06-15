/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/branches/samigo-2.9.x/samigo-app/src/java/org/sakaiproject/tool/assessment/ui/listener/author/PreviewPublishedAssessmentListener.java $
 * $Id: PreviewPublishedAssessmentListener.java 59684 2009-04-03 23:33:27Z arwhyte@umich.edu $
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

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.author.PublishedAssessmentBeanie;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;

/**
 * <p>Title: Samigo</p>2
 * <p>Description: Sakai Assessment Manager</p>
 * @author Ed Smiley
 * @version $Id: PreviewPublishedAssessmentListener.java 59684 2009-04-03 23:33:27Z arwhyte@umich.edu $
 */

public class PreviewPublishedAssessmentListener
    implements ActionListener
{
  //private static Log log = LogFactory.getLog(PreviewPublishedAssessmentListener.class);

  public PreviewPublishedAssessmentListener()
  {
  }

  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    PublishedAssessmentBeanie assessmentBean = (PublishedAssessmentBeanie) ContextUtil.lookupBean(
                                          "publishedAssessmentBean");

    // #1a - load the assessment
    String publishedAssessmentId = (String) FacesContext.getCurrentInstance().
        getExternalContext().getRequestParameterMap().get("publishedAssessmentId");
    PublishedAssessmentService assessmentService = new PublishedAssessmentService();
    PublishedAssessmentFacade publishedAssessment = assessmentService.getPublishedAssessment(
        publishedAssessmentId);
    //log.info("** assessment = "+publishedAssessment);
    //log.info("** assessment Bean= "+assessmentBean);
    assessmentBean.setAssessment(publishedAssessment);

  }

}
