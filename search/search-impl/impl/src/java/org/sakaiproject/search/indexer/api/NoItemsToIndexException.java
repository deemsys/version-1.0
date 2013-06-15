/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-impl/impl/src/java/org/sakaiproject/search/indexer/api/NoItemsToIndexException.java $
 * $Id: NoItemsToIndexException.java 59685 2009-04-03 23:36:24Z arwhyte@umich.edu $
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

package org.sakaiproject.search.indexer.api;

import org.sakaiproject.search.transaction.api.IndexTransactionException;

/**
 * When there are no items to index, this Exception is thrown. The Indexer
 * should not start a transaction
 * 
 * @author ieb
 */
public class NoItemsToIndexException extends IndexTransactionException
{

	/**
	 * 
	 */
	public NoItemsToIndexException()
	{
	}

	/**
	 * @param arg0
	 */
	public NoItemsToIndexException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public NoItemsToIndexException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public NoItemsToIndexException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
