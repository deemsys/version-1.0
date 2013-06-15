/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/branches/edu-services-1.2.x/sections-service/sections-impl/integration-test/src/test/org/sakaiproject/test/section/SectionIntegrationTestSuite.java $
 * $Id: SectionIntegrationTestSuite.java 59686 2009-04-03 23:37:55Z arwhyte@umich.edu $
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
package org.sakaiproject.test.section;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.test.SakaiTestBase;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

public class SectionIntegrationTestSuite extends SakaiTestBase {
	
	private static final Log log = LogFactory.getLog(SectionIntegrationTestSuite.class);

	/**
	 * Runs only once for the entire TestSuite, so we can keep the same component manager
	 * rather than rebuilding it for each test case.
	 * 
	 * @return
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(SectionAwarenessImplTest.class);
		suite.addTestSuite(CourseManagementIntegrationTest.class);
		TestSetup setup = new TestSetup(suite) {
			protected void setUp() throws Exception {
				log.debug("starting setup");
				oneTimeSetup();
				log.debug("finished setup");
			}
			protected void tearDown() throws Exception {
				oneTimeTearDown();
			}
		};
		return setup;
	}

}
