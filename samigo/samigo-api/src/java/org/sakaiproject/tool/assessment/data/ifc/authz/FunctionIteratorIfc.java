/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/sam/branches/samigo-2.9.x/samigo-api/src/java/org/sakaiproject/tool/assessment/data/ifc/authz/FunctionIteratorIfc.java $
 * $Id: FunctionIteratorIfc.java 59684 2009-04-03 23:33:27Z arwhyte@umich.edu $
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



package org.sakaiproject.tool.assessment.data.ifc.authz;

public  interface FunctionIteratorIfc
    extends java.io.Serializable
{
  boolean hasNextFunction();

  FunctionIfc nextFunction();
}
