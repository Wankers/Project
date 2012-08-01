/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.beshmundirTemple;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AI2Request;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.services.teleport.PortalService;
import gameserver.utils.PacketSendUtility;

/**
 * @author Gigi, vlog
 */
@AIName("beshmundirswalk")
public class BeshmundirsWalkAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10)); // Initial dialog
	}

	@Override
	public boolean onDialogSelect(Player player, final int dialogId, int questId) {
		AI2Request request = new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(getNpcId());
				if (portalTemplate != null) {
					// TODO: create an instance, depending on difficulty level
					PortalService.port(portalTemplate, responder, getObjectId(), getObjectTemplate().getTalkDelay());
				}
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// Do nothing: just close the dialog
			}
		};
		switch (dialogId) {
			case 60: { // I'm ready to enter
				if (player.isInGroup2() && player.getPlayerGroup2().isLeader(player)) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 4762)); // Path selection
				}
				else if (isAGroupMemberInInstance(player)) {
					PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(getNpcId());
					if (portalTemplate != null) {
						PortalService.port(portalTemplate, player, getObjectId(), getObjectTemplate().getTalkDelay());
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10)); // Initial dialog
				}
				break;
			}
			case 4763: { // I'll take the safer path
				AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM,
					getObjectId(), request, new DescriptionId(1804103));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 4762)); // Path selection
				break;
			}
			case 4848: { // Give me the dangerous path
				AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM,
					getObjectId(), request, new DescriptionId(1804105));
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 4762)); // Path selection
				break;
			}
		}
		return true;
	}

	private boolean isAGroupMemberInInstance(Player player) {
		if (player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getMembers()) {
				if (member.getWorldId() == 300170000) {
					return true;
				}
			}
		}
		return false;
	}
}
