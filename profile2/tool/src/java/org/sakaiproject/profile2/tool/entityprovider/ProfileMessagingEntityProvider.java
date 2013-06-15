package org.sakaiproject.profile2.tool.entityprovider;

import lombok.Setter;

import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.util.AbstractEntityProvider;
import org.sakaiproject.profile2.logic.ProfileMessagingLogic;
import org.sakaiproject.profile2.logic.SakaiProxy;

/**
 * This is the entity provider for messaging in Profile2
 * 
 * @author Steve Swinsburg (s.swinsburg@lancaster.ac.uk)
 *
 */
public class ProfileMessagingEntityProvider extends AbstractEntityProvider implements EntityProvider, AutoRegisterEntityProvider, Outputable, Describeable, ActionsExecutable{
	
	public final static String ENTITY_PREFIX = "profile-message";
	
	@Override
	public String getEntityPrefix() {
		return ENTITY_PREFIX;
	}
	
	@EntityCustomAction(action="unread-count",viewKey=EntityView.VIEW_LIST)
	public Object getUnreadMessageCount(EntityView view) {
		
		//get current user
		String uuid = sakaiProxy.getCurrentUserId();
		
		//get count & return
		int count = messagingLogic.getAllUnreadMessagesCount(uuid);
		return new ActionReturn(count);
	}
	
	@Override
	public String[] getHandledOutputFormats() {
		return new String[] {Formats.XML, Formats.JSON};
	}
	
	@Setter
	private SakaiProxy sakaiProxy;
	
	@Setter
	private ProfileMessagingLogic messagingLogic;
}
