/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/kernel-util/src/main/java/org/sakaiproject/util/IteratorEnumeration.java $
 * $Id: IteratorEnumeration.java 101696 2011-12-13 14:44:36Z aaronz@vt.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>
 * IteratorEnumeration is an enumeration over an iterator.
 * </p>
 * @deprecated use commons-collection instead, this will be removed after 2.9 - Dec 2011
 */
@Deprecated 
public class IteratorEnumeration implements Enumeration
{
	/** The iterator over which this enumerates. */
	protected Iterator m_iterator = null;

	public IteratorEnumeration(Iterator i)
	{
		m_iterator = i;
	}

	public boolean hasMoreElements()
	{
		return m_iterator.hasNext();
	}

	public Object nextElement()
	{
		return m_iterator.next();
	}
}
