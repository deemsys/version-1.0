/**********************************************************************************
* $URL: https://source.sakaiproject.org/svn/sam/branches/samigo-2.9.x/samigo-app/src/test/org/sakaiproject/tool/assessment/jsf/LinksModelBean.java $
* $Id: LinksModelBean.java 113402 2012-09-21 20:39:56Z ottenhoff@longsight.com $
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
package org.sakaiproject.tool.assessment.jsf;
import java.io.Serializable;

/**
 * <p> test bean</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: LinksModelBean.java 113402 2012-09-21 20:39:56Z ottenhoff@longsight.com $
 */

public class LinksModelBean implements Serializable
{
  private java.util.ArrayList linkListList;

  public LinksModelBean()
  {
    linkListList = new java.util.ArrayList();
    for (int i = 0; i < 20; i++) {
      linkListList.add(new TestLinksBean());
    }
  }

  public java.util.ArrayList getLinkListList()
  {
    return linkListList;
  }
  public void setLinkListList(java.util.ArrayList linkListList)
  {
    this.linkListList = linkListList;
  }

  public static void main(String args[])
  {
    LinksModelBean bean = new LinksModelBean();
    java.util.ArrayList list = bean.getLinkListList();

    for (int i = 0; i < list.size(); i++) {
    }
  }


}