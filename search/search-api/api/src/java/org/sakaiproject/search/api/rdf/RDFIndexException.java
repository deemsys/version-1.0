/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/search/branches/search-1.4.x/search-api/api/src/java/org/sakaiproject/search/api/rdf/RDFIndexException.java $
 * $Id: RDFIndexException.java 59685 2009-04-03 23:36:24Z arwhyte@umich.edu $
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
package org.sakaiproject.search.api.rdf;


/**
 * @author ieb
 *
 */
public class RDFIndexException extends RDFException
{

	public RDFIndexException()
	{
		super();
	}

	public RDFIndexException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public RDFIndexException(String arg0)
	{
		super(arg0);
	}

	public RDFIndexException(Throwable arg0)
	{
		super(arg0);
	}
	

}
