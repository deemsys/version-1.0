/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/kernel/branches/kernel-1.3.x/api/src/main/java/org/sakaiproject/memory/api/MultiRefCache.java $
 * $Id: MultiRefCache.java 93282 2011-05-26 15:17:24Z matthew.buckett@oucs.ox.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008 Sakai Foundation
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

package org.sakaiproject.memory.api;

import java.util.Collection;

/**
 * <p>
 * MultiRefCache is a cache that holds objects and a set of references that the cached entry are dependent on - if any change, the entry is invalidated.
 * The additional references can only be AuthzGroups.
 * @see GenericMultiRefCache
 * @deprecated Since Kernel 1.2
 * </p>
 */
public interface MultiRefCache extends Cache
{
	/**
	 * Cache an object
	 * 
	 * @param key
	 *        The key with which to find the object.
	 * @param payload
	 *        The object to cache.
	 * @param duration
	 *        The time to cache the object (seconds).
	 * @param ref
	 *        One entity reference that, if changed, will invalidate this entry.
	 * @param azgIds
	 *        AuthzGroup ids that, if the changed, will invalidate this entry.
	 */
	void put(Object key, Object payload, int duration, String ref, Collection<String> azgIds);

}
