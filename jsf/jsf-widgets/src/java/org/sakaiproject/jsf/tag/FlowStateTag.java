/**********************************************************************************
*
* $Id: FlowStateTag.java 68846 2009-11-13 12:27:32Z arwhyte@umich.edu $
*
***********************************************************************************
 * Copyright (c) 2005, 2006, 2008 The Sakai Foundation
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
**********************************************************************************/

package org.sakaiproject.jsf.tag;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FlowStateTag extends UIComponentTag {
	private static final Log logger = LogFactory.getLog(FlowStateTag.class);

	private String bean;

	public void setBean(String bean) {
		this.bean = bean;
	}

	protected void setProperties(UIComponent component) {
		if (logger.isDebugEnabled()) logger.debug("setProperties " + bean);
		super.setProperties(component);

		FacesContext context = getFacesContext();

		if (bean != null) {
			if (UIComponentTag.isValueReference(bean)) {
				ValueBinding vb = context.getApplication().createValueBinding(bean);
				component.setValueBinding("bean", vb);
			} else {
				logger.error("Invalid expression " + bean);
			}
		}
	}

	public String getComponentType() {
		return "org.sakaiproject.jsf.FlowState";
	}

	public String getRendererType() {
		return null;
	}
}


