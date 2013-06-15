/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/indexer/debug/DebugIndexWorkerListener.java $
 * $Id: DebugIndexWorkerListener.java 59685 2009-04-03 23:36:24Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation
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

package org.sakaiproject.search.indexer.debug;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.search.indexer.api.IndexWorker;
import org.sakaiproject.search.indexer.api.IndexWorkerListener;

/**
 * Debug listener to the index operations
 * 
 * @author ieb Unit test
 * @see org.sakaiproject.search.indexer.impl.test.TransactionalIndexWorkerTest
 */
public class DebugIndexWorkerListener implements IndexWorkerListener
{

	private static final Log log = LogFactory.getLog(DebugIndexWorkerListener.class);

	public void init()
	{

	}

	public void destroy()
	{

	}

	/**
	 * @see org.sakaiproject.search.indexer.api.IndexWorkerListener#indexWorkerEnd(org.sakaiproject.search.indexer.api.IndexWorker)
	 */
	public void indexWorkerEnd(IndexWorker worker)
	{
		log.debug("Worker [" + worker + "] Index End ");

	}

	/**
	 * @see org.sakaiproject.search.indexer.api.IndexWorkerListener#indexWorkerStart(org.sakaiproject.search.indexer.api.IndexWorker)
	 */
	public void indexWorkerStart(IndexWorker worker)
	{
		log.debug("Worker [" + worker + "] Index Start ");
	}

}
