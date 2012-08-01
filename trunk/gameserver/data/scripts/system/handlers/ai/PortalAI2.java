/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
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
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package ai;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.services.teleport.PortalService;

/**
 * @author ATracer
 */
@AIName("portal")
public class PortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(getNpcId());
		AI2Actions.selectDialog(this, player, 0, -1);
		if (portalTemplate != null) {
			PortalService.port(portalTemplate, player, getObjectId(), getObjectTemplate().getTalkDelay());
		}
	}
}
