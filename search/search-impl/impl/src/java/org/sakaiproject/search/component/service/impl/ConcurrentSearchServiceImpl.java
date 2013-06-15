/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/component/service/impl/ConcurrentSearchServiceImpl.java $
 * $Id: ConcurrentSearchServiceImpl.java 71220 2010-01-09 12:11:05Z david.horwitz@uct.ac.za $
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

package org.sakaiproject.search.component.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiproject.search.api.SearchStatus;
import org.sakaiproject.search.component.Messages;

/**
 * The search service
 * 
 * @author ieb
 */
public class ConcurrentSearchServiceImpl extends BaseSearchServiceImpl
{

	private static Log log = LogFactory.getLog(ConcurrentSearchServiceImpl.class);


	/**
	 * Register a notification action to listen to events and modify the search
	 * index
	 */
	@Override
	public void init()
	{

		super.init();
		

		try
		{

			try
			{
				if (autoDdl)
				{
					SqlService.getInstance().ddl(this.getClass().getClassLoader(),
							"sakai_search_parallel");
				}
			}
			catch (Exception ex)
			{
				log.error("Perform additional SQL setup", ex);
				
				
			}

			initComplete = true;

			

		}
		catch (Throwable t)
		{
			log.error("Failed to start ", t); //$NON-NLS-1$
		}

	}


	@Override
	public String getStatus()
	{

		String lastLoad = (new Date(indexStorage.getLastLoad())).toString();
		String loadTime = String.valueOf((double) (0.001 * (indexStorage.getLastLoadTime())));

		return Messages.getString("SearchServiceImpl.40") + lastLoad + Messages.getString("SearchServiceImpl.38") + loadTime + Messages.getString("SearchServiceImpl.37"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public SearchStatus getSearchStatus()
	{
		String ll = Messages.getString("SearchServiceImpl.36"); //$NON-NLS-1$
		String lt = ""; //$NON-NLS-1$
		long reloadEnd = indexStorage.getLastLoad();
		if (reloadEnd != 0)
		{
			ll = (new Date(reloadEnd)).toString();
			lt = String.valueOf((double) (0.001 * (indexStorage.getLastLoadTime())));
		}
		final String lastLoad = ll;
		final String loadTime = lt;
		// final SearchWriterLock lock =
		// searchIndexBuilderWorker.getCurrentLock();
		// final List lockNodes = searchIndexBuilderWorker.getNodeStatus();
		final String pdocs = String.valueOf(getPendingDocs());
		final String ndocs = String.valueOf(getNDocs());

		return new SearchStatus()
		{
			public String getLastLoad()
			{
				return lastLoad;
			}

			public String getLoadTime()
			{
				return loadTime;
			}

			public String getCurrentWorker()
			{
				return "concurrent indexing, no locks ";
			}

			public String getCurrentWorkerETC()
			{
				if (SecurityService.isSuperUser())
				{
					return "List of current activity, superuser";
					/*
					 * MessageFormat.format(Messages
					 * .getString("SearchServiceImpl.35"), //$NON-NLS-1$ new
					 * Object[] { lock.getExpires(),
					 * searchIndexBuilderWorker.getLastDocument(),
					 * searchIndexBuilderWorker.getLastElapsed(),
					 * searchIndexBuilderWorker.getCurrentDocument(),
					 * searchIndexBuilderWorker.getCurrentElapsed(),
					 * serverConfigurationService.getServerIdInstance() });
					 */
				}
				else
				{
					return "List of current activity, normal user ";
					/*
					 * MessageFormat.format(Messages
					 * .getString("SearchServiceImpl.39"), new Object[] { lock
					 * //$NON-NLS-1$ .getExpires() });
					 */
				}
			}

			public List<Object[]> getWorkerNodes()
			{
				List<Object[]> l = new ArrayList<Object[]>();
				l.add(new Object[] { "NodeName", new Date(), "running status " });
				/*
				 * for (Iterator i = lockNodes.iterator(); i.hasNext();) {
				 * SearchWriterLock swl = (SearchWriterLock) i.next(); Object[]
				 * result = new Object[3]; result[0] = swl.getNodename();
				 * result[1] = swl.getExpires(); if
				 * (lock.getNodename().equals(swl.getNodename())) { result[2] =
				 * Messages.getString("SearchServiceImpl.47"); //$NON-NLS-1$ }
				 * else { result[2] =
				 * Messages.getString("SearchServiceImpl.48"); //$NON-NLS-1$ }
				 * l.add(result); }
				 */
				return l;
			}

			public String getNDocuments()
			{
				return ndocs;
			}

			public String getPDocuments()
			{
				return pdocs;
			}

		};

	}

	@Override
	public boolean removeWorkerLock()
	{
		// no locks
		return true;

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.search.component.service.impl.BaseSearchServiceImpl#isEnabled()
	 */
	@Override
	public boolean isEnabled()
	{
		String resource = SqlService.getVendor() + "/sakai_search_parallel.sql";
		// find the resource from the loader
		if ( this.getClass().getClassLoader().getResourceAsStream(resource) == null ) {
			return false;
		}
		return super.isEnabled();
	}


}
