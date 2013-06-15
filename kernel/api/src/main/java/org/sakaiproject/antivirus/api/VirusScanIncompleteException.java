/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/api/src/main/java/org/sakaiproject/antivirus/api/VirusScanIncompleteException.java $
 * $Id: VirusScanIncompleteException.java 76289 2010-04-17 10:20:37Z david.horwitz@uct.ac.za $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 Sakai Foundation
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

package org.sakaiproject.antivirus.api;

/**
 * Created by IntelliJ IDEA.
 * User: jbush
 * Date: Sep 6, 2006
 * Time: 10:17:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirusScanIncompleteException extends RuntimeException {
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public VirusScanIncompleteException(String message) {
      super(message);
   }
   
   public VirusScanIncompleteException(String message, Throwable cause) {
	   super(message);
	   super.initCause(cause);
   }
}
