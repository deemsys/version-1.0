/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/common/branches/common-1.2.x/import-impl/src/java/org/sakaiproject/importer/impl/ManifestResource.java $
 * $Id: ManifestResource.java 59673 2009-04-03 23:02:03Z arwhyte@umich.edu $
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

package org.sakaiproject.importer.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface ManifestResource {
	
	String getTitle(Node resourceNode);
	
	String getType(Node resourceNode);
	
	String getDescription(Node resourceNode);
	
	String getId(Node resourceNode);
	
	Document getDescriptor(Node resourceNode);
	
	boolean isFolder(Document descriptor);

}
