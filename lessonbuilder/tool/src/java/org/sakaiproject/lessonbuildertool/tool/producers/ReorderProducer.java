/**********************************************************************************
 * $URL: $
 * $Id: $
 ***********************************************************************************
 *
 * Author: Eric Jeney, jeney@rutgers.edu
 *
 * Copyright (c) 2010 Rutgers, the State University of New Jersey
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");                                                                
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.lessonbuildertool.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.util.FormattedText;
import org.sakaiproject.lessonbuildertool.SimplePage;
import org.sakaiproject.lessonbuildertool.SimplePageItem;
import org.sakaiproject.lessonbuildertool.model.SimplePageToolDao;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean;
import org.sakaiproject.lessonbuildertool.tool.beans.SimplePageBean.Status;
import org.sakaiproject.lessonbuildertool.tool.view.GeneralViewParameters;

import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.localeutil.LocaleGetter;                                                                                          
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;                                                               
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

/**
 * Uses the Fluid reorderer to reorder elements on the page.
 * 
 * @author Eric Jeney <jeney@rutgers.edu>
 * 
 */
public class ReorderProducer implements ViewComponentProducer, NavigationCaseReporter, ViewParamsReporter {
	private SimplePageBean simplePageBean;
	private SimplePageToolDao simplePageToolDao;
	private ShowPageProducer showPageProducer;

	public MessageLocator messageLocator;
	public LocaleGetter localeGetter;                                                                                             
	public static final String VIEW_ID = "Reorder";

	public String getViewID() {
		return VIEW_ID;
	}

	public void fillComponents(UIContainer tofill, ViewParameters params, ComponentChecker checker) {

		if (((GeneralViewParameters) params).getSendingPage() != -1) {
		    // will fail if page not in this site
		    // security then depends upon making sure that we only deal with this page
			try {
				simplePageBean.updatePageObject(((GeneralViewParameters) params).getSendingPage());
			} catch (Exception e) {
				System.out.println("Reorder permission exception " + e);
				return;
			}
		}

                UIOutput.make(tofill, "html").decorate(new UIFreeAttributeDecorator("lang", localeGetter.get().getLanguage()))
		    .decorate(new UIFreeAttributeDecorator("xml:lang", localeGetter.get().getLanguage()));        

		ToolSession toolSession = SessionManager.getCurrentToolSession();
		String secondPageString = (String)toolSession.getAttribute("lessonbuilder.selectedpage");
		Long secondPageId = null;
		if (secondPageString != null) 
		    secondPageId = Long.parseLong(secondPageString);

		toolSession.setAttribute("lessonbuilder.selectedpage", null);

		// may have been updated by sendingpage
		SimplePage currentPage = simplePageBean.getCurrentPage();

		// doesn't use any item parameters, so this should be safe

		if (simplePageBean.canEditPage()) {

		    // make sure the order is right
			simplePageBean.fixorder();

			SimplePage page = simplePageBean.getCurrentPage();
			List<SimplePageItem> items = simplePageToolDao.findItemsOnPage(page.getPageId());
			
		        SimplePage secondPage = null;
			if (secondPageId != null)
			    secondPage = simplePageBean.getPage(secondPageId);

			// are they hacking us? other page should be in the same site, in which case
			// we don't need to check rights further. also same owner (typicall null; owner is for student pages)

			if (secondPage != null && !secondPage.getSiteId().equals(page.getSiteId()))
			    secondPage = null;
			if (secondPage != null &&
			    ((secondPage.getOwner() == null && page.getOwner() != null) ||
			     (secondPage.getOwner() != null && page.getOwner() == null) ||
			     (secondPage.getOwner() != null && !secondPage.getOwner().equals(page.getOwner()))))
			    secondPage = null;
			
			// Some items are tacked onto the end automatically by setting the sequence to
			// something less than or equal to 0.  This takes them out of the Reorder tool.
			while(items.size() > 0 && items.get(0).getSequence() <= 0) {
				items.remove(0);
			}

			if (secondPage != null) {
			    List<SimplePageItem> moreItems = simplePageToolDao.findItemsOnPage(secondPageId);

			    if (moreItems != null && moreItems.size() > 0) {
				items.add(null); //marker
				while(moreItems.size() > 0 && moreItems.get(0).getSequence() <= 0) {
				    moreItems.remove(0);
				}
				items.addAll(moreItems);
			    }
			} else
			    items.add(null); // if no 2nd page, put marker at the end

			UIOutput.make(tofill, "intro", messageLocator.getMessage("simplepage.reorder_header"));
			UIOutput.make(tofill, "instructions", messageLocator.getMessage("simplepage.reorder_instructions"));

			UIOutput.make(tofill, "itemTable");

			boolean second = false;
			for (SimplePageItem i : items) {

				if (i == null) {
				    // marker between used and not used
				    UIContainer row = UIBranchContainer.make(tofill, "item:");
				    UIOutput.make(row, "seq", "---");
				    UIOutput.make(row, "description", messageLocator.getMessage(secondPageId == null ? "simplepage.reorder-belowdelete" : "simplepage.reorder-aboveuse"));
				    second = true;
				    continue;
				}

				if (i.getType() == 7) {
					i.setType(1); // Temporarily change multimedia to standard resource
								  // so that links work properly.
				}


				UIContainer row = UIBranchContainer.make(tofill, "item:");
				// * prefix indicates items are from the other page, and have to be copied.
				UIOutput.make(row, "seq", (second ? "*" : "") +
					                   String.valueOf(i.getSequence()));
				UIOutput.make(row, "description", i.getDescription());
				if (i.getType() == 5) {
					String text = FormattedText.convertFormattedTextToPlaintext(i.getHtml());
					if (text.length() > 100) {
					    UIOutput.make(row, "text-snippet", text.substring(0,100));
					} else {
					    UIOutput.make(row, "text-snippet", text);
					}
				} else {
				    showPageProducer.makeLink(row, "link", i, simplePageBean, simplePageToolDao, messageLocator, true, currentPage, false, Status.NOT_REQUIRED);
				}
			}


			// don't offer to add from other page if we already have second page items
			// our bookkeeping can't keep track of more than one extra page
			if(currentPage.getOwner() == null && secondPageId == null) {
			    GeneralViewParameters view = new GeneralViewParameters(PagePickerProducer.VIEW_ID);
			    view.setReturnView("reorder"); // flag to pagepicker that it needs to come back
			    UIOutput.make(tofill, "subpage-div");
			    UIInternalLink.make(tofill, "subpage-choose", messageLocator.getMessage("simplepage.reorder-addpage"), view);
			    view.setSendingPage(currentPage.getPageId());
			}

			UIForm form = UIForm.make(tofill, "form");
			if (secondPageId != null)
			    UIInput.make(form, "otherpage", "#{simplePageBean.selectedEntity}", secondPageId.toString());
			UIInput.make(form, "order", "#{simplePageBean.order}");
			UICommand.make(form, "save", messageLocator.getMessage("simplepage.save_message"), "#{simplePageBean.reorder}");
			UICommand.make(form, "cancel", messageLocator.getMessage("simplepage.cancel_message"), "#{simplePageBean.cancel}");
		}
	}

	public void setSimplePageBean(SimplePageBean simplePageBean) {
		this.simplePageBean = simplePageBean;
	}

	public void setSimplePageToolDao(SimplePageToolDao simplePageToolDao) {
		this.simplePageToolDao = simplePageToolDao;
	}

	public void setShowPageProducer(ShowPageProducer p) {
		this.showPageProducer = p;
	}

	public ViewParameters getViewParameters() {
		return new GeneralViewParameters();
	}

	public List reportNavigationCases() {
		List<NavigationCase> togo = new ArrayList<NavigationCase>();
		togo.add(new NavigationCase(null, new SimpleViewParameters(ShowPageProducer.VIEW_ID)));
		togo.add(new NavigationCase("success", new SimpleViewParameters(ReloadPageProducer.VIEW_ID)));
		togo.add(new NavigationCase("cancel", new SimpleViewParameters(ShowPageProducer.VIEW_ID)));

		return togo;
	}
}
