/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/indexer/impl/ConcurrentSearchIndexBuilderWorkerImpl.java $
 * $Id: ConcurrentSearchIndexBuilderWorkerImpl.java 102142 2011-12-19 10:58:54Z david.horwitz@uct.ac.za $
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

package org.sakaiproject.search.indexer.impl;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.search.indexer.api.IndexWorker;
import org.sakaiproject.search.indexer.api.IndexWorkerDocumentListener;
import org.sakaiproject.search.journal.api.ManagementOperation;
import org.sakaiproject.search.journal.impl.JournalSettings;
import org.sakaiproject.user.api.UserDirectoryService;

/**
 * A management operation to perform indexing to the journal
 * 
 * @author ieb 
 */
public class ConcurrentSearchIndexBuilderWorkerImpl implements ManagementOperation,
		IndexWorkerDocumentListener
{

	private static final Log log = LogFactory
			.getLog(ConcurrentSearchIndexBuilderWorkerImpl.class);

	private boolean enabled = false;


	/**
	 * dependency
	 */
	private UserDirectoryService userDirectoryService;

	/**
	 * dependency
	 */
	private SearchService searchService;

	/**
	 * dependency
	 */
	private IndexWorker indexWorker;


	/**
	 * dependency
	 */
	private ComponentManager componentManager;

	
	/**
	 * Dependency
	 */
	private ServerConfigurationService serverConfigurationService;


	/**
	 * we need to watch local events to guage activity
	 */
	private EventTrackingService eventTrackingService;

	/**
	 * Setting A load factor 1 is full load, 100 is normal The load factor
	 * controlls the backoff of the indexer threads. If the load Factor is high,
	 * the search threads back off more.
	 */
	private long loadFactor = 1000L;

	/**
	 * Has been started once at least
	 */
	private boolean started = false;

	/**
	 * threads should run
	 */
	private boolean runThreads = false;

	/**
	 * The last time an index run was performed on this node
	 */
	private long lastIndexRun = System.currentTimeMillis();

	/**
	 * The time the last event was seen
	 */
	private long lastEvent;

	private ThreadLocal<String> nowIndexing = new ThreadLocal<String>();

	private JournalSettings journalSettings;

	private SqlService sqlService;

	private SecurityService securityService;

	public void destroy()
	{

	}

	public void init()
	{
		if (started && !runThreads)
		{
			log.warn("JVM Shutdown in progress, will not startup");
			return;
		}
		if (componentManager.hasBeenClosed())
		{
			log.warn("Component manager Shutdown in progress, will not startup");
			return;
		}
		
		enabled = searchService.isEnabled() && "true".equals(serverConfigurationService.getString("search.indexqueue", "true"));

		started = true;
		runThreads = true;

		if ( !enabled ) {
			return;
		}

		eventTrackingService.addLocalObserver(new Observer()
		{

			public void update(Observable arg0, Object arg1)
			{
				lastEvent = System.currentTimeMillis();
			}

		});

	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void runOnce()
	{
		if (!enabled)
		{
			return;
		}
		if (componentManager.hasBeenClosed())
		{
			log.info("Component Manager has been closed");
			return;
		}


		nowIndexing.set("-");
		if (journalSettings.getSoakTest() && (searchService.getPendingDocs() == 0))
		{
			log.error("SOAK TEST---SOAK TEST---SOAK TEST. Index Rebuild Started");
			searchService.rebuildInstance();
		}

		log.debug("Run Processing Thread");
		int totalDocs = searchService.getPendingDocs();

		long now = System.currentTimeMillis();
		long interval = now - lastEvent;
		boolean process = false;
		boolean createIndex = false;

		if (totalDocs > 200)
		{
			loadFactor = 10L;
		}
		else
		{
			loadFactor = 1000L;
		}
		if (totalDocs == 0)
		{
			process = false;
		}
		else if (totalDocs < 20 && interval > (20 * loadFactor))
		{
			process = true;
		}
		else if (totalDocs >= 20 && totalDocs < 50 && interval > (10 * loadFactor))
		{
			process = true;
		}
		else if (totalDocs >= 50 && totalDocs < 90 && interval > (5 * loadFactor))
		{
			process = true;
		}
		else if (totalDocs > ((90 * loadFactor) / 1000))
		{
			process = true;
		}

		// should this node consider taking the lock ?
		long lastIndexInterval = (System.currentTimeMillis() - lastIndexRun);
		long lastIndexMetric = lastIndexInterval * totalDocs;

		// if we have 1000 docs, then indexing should happen
		// after 10 seconds break
		// 1000*10000 10000000
		// 500 docs/ 20 seconds
		//

		log.debug("Activity " + (lastIndexMetric > (10000L * loadFactor)) + " "
				+ (lastIndexInterval > (60L * loadFactor)) + " " + createIndex);

		if (true)
		{
			log.debug("===" + process + "=============PROCESSING ");
			if (process)
			{

				lastIndexRun = System.currentTimeMillis();

				int batchSize = 100;
				if (totalDocs > 100000)
				{
					batchSize = 2000;
				}
				else if (totalDocs > 10000)
				{
					batchSize = 1000;
				}
				else if (totalDocs > 1000)
				{
					batchSize = 500;
				}
				else if (totalDocs > 500)
				{
					batchSize = 200;
				}
				
				securityService.pushAdvisor(new SecurityAdvisor() {

					public SecurityAdvice isAllowed(String userId, String function, String reference)
					{
						return SecurityAdvice.ALLOWED;
					}
					
				});
				try
				{
					indexWorker.process(batchSize);

				}
				finally
				{
					securityService.popAdvisor();
				}

				lastIndexRun = System.currentTimeMillis();

			}
		}

	}

	/**
	 * @see org.sakaiproject.search.indexer.api.IndexWorkerDocumentListener#indexDocumentEnd(org.sakaiproject.search.indexer.api.IndexWorker,
	 *      java.lang.String)
	 */
	public void indexDocumentEnd(IndexWorker worker, String ref)
	{
		nowIndexing.set("-");
	}

	/**
	 * @see org.sakaiproject.search.indexer.api.IndexWorkerDocumentListener#indexDocumentStart(org.sakaiproject.search.indexer.api.IndexWorker,
	 *      java.lang.String)
	 */
	public void indexDocumentStart(IndexWorker worker, String ref)
	{
		nowIndexing.set(ref);

	}

	/**
	 * @return the componentManager
	 */
	public ComponentManager getComponentManager()
	{
		return componentManager;
	}

	/**
	 * @param componentManager
	 *        the componentManager to set
	 */
	public void setComponentManager(ComponentManager componentManager)
	{
		this.componentManager = componentManager;
	}

	/**
	 * @return the eventTrackingService
	 */
	public EventTrackingService getEventTrackingService()
	{
		return eventTrackingService;
	}

	/**
	 * @param eventTrackingService
	 *        the eventTrackingService to set
	 */
	public void setEventTrackingService(EventTrackingService eventTrackingService)
	{
		this.eventTrackingService = eventTrackingService;
	}

	/**
	 * @return the indexWorker
	 */
	public IndexWorker getIndexWorker()
	{
		return indexWorker;
	}

	/**
	 * @param indexWorker
	 *        the indexWorker to set
	 */
	public void setIndexWorker(IndexWorker indexWorker)
	{
		this.indexWorker = indexWorker;
	}

	/**
	 * @return the loadFactor
	 */
	public long getLoadFactor()
	{
		return loadFactor;
	}

	/**
	 * @param loadFactor
	 *        the loadFactor to set
	 */
	public void setLoadFactor(long loadFactor)
	{
		this.loadFactor = loadFactor;
	}

	/**
	 * @return the searchService
	 */
	public SearchService getSearchService()
	{
		return searchService;
	}

	/**
	 * @param searchService
	 *        the searchService to set
	 */
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}


	
	/**
	 * @return the userDirectoryService
	 */
	public UserDirectoryService getUserDirectoryService()
	{
		return userDirectoryService;
	}

	/**
	 * @param userDirectoryService
	 *        the userDirectoryService to set
	 */
	public void setUserDirectoryService(UserDirectoryService userDirectoryService)
	{
		this.userDirectoryService = userDirectoryService;
	}
	
	/**
	 * 
	 * @param serverConfigurationService
	 */
	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	/**
	 * @return the journalSettings
	 */
	public JournalSettings getJournalSettings()
	{
		return journalSettings;
	}

	/**
	 * @param journalSettings
	 *        the journalSettings to set
	 */
	public void setJournalSettings(JournalSettings journalSettings)
	{
		this.journalSettings = journalSettings;
	}

	/**
	 * @return the sqlService
	 */
	public SqlService getSqlService()
	{
		return sqlService;
	}

	/**
	 * @param sqlService the sqlService to set
	 */
	public void setSqlService(SqlService sqlService)
	{
		this.sqlService = sqlService;
	}

	/**
	 * @return the securityService
	 */
	public SecurityService getSecurityService()
	{
		return securityService;
	}

	/**
	 * @param securityService the securityService to set
	 */
	public void setSecurityService(SecurityService securityService)
	{
		this.securityService = securityService;
	}
	

}
