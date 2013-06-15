/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/journal/impl/SharedFilesystemJournalStorage.java $
 * $Id: SharedFilesystemJournalStorage.java 71220 2010-01-09 12:11:05Z david.horwitz@uct.ac.za $
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

package org.sakaiproject.search.journal.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.search.journal.api.JournalStorage;
import org.sakaiproject.search.journal.api.JournalStorageState;
import org.sakaiproject.search.transaction.api.IndexTransaction;
import org.sakaiproject.search.transaction.api.IndexTransactionException;
import org.sakaiproject.search.util.FileUtils;

/**
 * @author ieb TODO Unit test
 */
public class SharedFilesystemJournalStorage implements JournalStorage
{

	public void init()
	{

	}

	public void destroy()
	{

	}

	/**
	 * @author ieb
	 */
	private static class JournalStorageStateImpl implements JournalStorageState
	{

		protected File tmpZip;

		protected File journalZip;

		/**
		 * @param tmpZip
		 * @param journalZip
		 */
		public JournalStorageStateImpl(File tmpZip, File journalZip)
		{
			this.tmpZip = tmpZip;
			this.journalZip = journalZip;
		}

	}

	private static final Log log = LogFactory
			.getLog(SharedFilesystemJournalStorage.class);

	private JournalSettings journalSettings;

	/**
	 * Tries to find the latest version of a shared transaction file, limiting
	 * to 1000 versions of any file. The last existing version is in the first
	 * element, the next file is in the second element.
	 * 
	 * @param transactionId
	 * @return
	 */
	private File[] getTransactionFile(long transactionId)
	{
		File f = new File(journalSettings.getJournalLocation(), transactionId + ".zip");
		File testFile = f;
		int i = 0;
		while (testFile.exists() && i < 1000)
		{
			f = testFile;
			testFile = new File(journalSettings.getJournalLocation(), transactionId + "-"
					+ i + ".zip");
			i++;
		}
		File[] result = new File[2];
		result[0] = f;
		result[1] = testFile;
		if (log.isDebugEnabled())
		{
			log.debug("F0:" + result[0] + ":" + result[0].exists() + " F1:" + result[1]
					+ ":" + result[1].exists());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.search.journal.api.JournalStorage#prepareSave(java.lang.String,
	 *      long)
	 */
	public JournalStorageState prepareSave(String location, long transactionId)
			throws IOException
	{
		File indexLocation = new File(location);
		log.info("++++++ Saving " + indexLocation + " to shared");
		File tmpZip = new File(journalSettings.getJournalLocation(), transactionId
				+ ".zip." + System.currentTimeMillis());
		
		//this will fail if the parent dirs exist
		if (!tmpZip.getParentFile().exists())
		{
			if (!tmpZip.getParentFile().mkdirs())
			{
				log.warn("Couldn't create directory " + tmpZip.getParentFile().getPath());
			}
		}
		String basePath = indexLocation.getPath();
		String replacePath = String.valueOf(transactionId);

		FileOutputStream zout = new FileOutputStream(tmpZip);
		FileUtils.pack(indexLocation, basePath, replacePath, zout, journalSettings
				.getCompressShared());
		zout.close();

		File[] journalZip = getTransactionFile(transactionId);

		// save into the new space
		return new JournalStorageStateImpl(tmpZip, journalZip[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.search.journal.api.JournalStorage#commitSave(org.sakaiproject.search.journal.api.JournalStorageState)
	 */
	public void commitSave(JournalStorageState jss) throws IOException
	{
		File journalZip = ((JournalStorageStateImpl) jss).journalZip;
		File tmpZip = ((JournalStorageStateImpl) jss).tmpZip;
		if (!tmpZip.renameTo(journalZip))
		{
			log.warn("couldn't rename "  + tmpZip.getPath() + " to " + journalZip.getPath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.search.journal.api.JournalStorage#rollbackSave(org.sakaiproject.search.journal.api.JournalStorageState)
	 */
	public void rollbackSave(JournalStorageState jss)
	{
		if (jss != null)
		{
			File journalZip = ((JournalStorageStateImpl) jss).journalZip;
			File tmpZip = ((JournalStorageStateImpl) jss).tmpZip;
			if (tmpZip != null && tmpZip.exists())
			{
				if (!tmpZip.delete())
				{
					log.warn("counldn't delete " + tmpZip.getPath());
				}
			}
			if (journalZip != null && journalZip.exists())
			{
				if (!journalZip.delete())
				{
					log.warn("counldn't delete " + journalZip.getPath());
				}
			}
		}
	}

	/**
	 * @throws IOException
	 * @throws IOException
	 * @see org.sakaiproject.search.maintanence.api.JournalStorage#retrieveLaterSavePoints(long[],
	 *      java.lang.String)
	 */
	public void retrieveSavePoint(long savePoint, String workingSpace) throws IOException
	{
		File ws = new File(workingSpace);
		if (!ws.exists())
		{	
			if (!ws.mkdirs())
			{
				log.warn("Couldn't create directory " + ws.getPath());
			}
		}
		File[] f = getTransactionFile(savePoint);
		// retrieve the existing transaction file
		if (!f[0].exists())
		{
			log.error("======================================= Lost Shared Segment ================= \n" +
					"\t"+f[0] + "\n" +
					"\tThe above file does not exist, this should not happen and should be investigated ");
		} 
		else 
		{
			FileInputStream source = new FileInputStream(f[0]);
	 		try 
 			{
				FileUtils.unpack(source, ws);
	 		} 
 			finally 
 			{
 				source.close();
 			}
		}

	}

	/**
	 * @see org.sakaiproject.search.transaction.api.TransactionListener#close(org.sakaiproject.search.transaction.api.IndexTransaction)
	 */
	public void close(IndexTransaction transaction) throws IndexTransactionException
	{
	}

	/**
	 * @param savePoint
	 * @param workingSpace
	 * @return
	 */
	public File getLocalJournalLocation(long savePoint, String workingSpace)
	{
		return new File(workingSpace, String.valueOf(savePoint));
	}

	/**
	 * @param savePoint
	 * @throws IOException
	 */
	public void removeJournal(long savePoint) throws IOException
	{

		File[] f = getTransactionFile(savePoint);
		// remove all existing transaction versions
		while (f[0].exists())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Removing " + f[0]);
			}
			FileUtils.deleteAll(f[0]);
			f = getTransactionFile(savePoint);
		}

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

}
