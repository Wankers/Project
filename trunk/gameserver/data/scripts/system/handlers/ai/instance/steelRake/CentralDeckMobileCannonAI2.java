/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package ai.instance.steelRake;

import java.util.List;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("centralcannon")
public class CentralDeckMobileCannonAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (!player.getInventory().decreaseByItemId(185000052, 1)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111302));
			return;
		}
		WorldPosition worldPosition = player.getPosition();

		if (worldPosition.isInstanceMap()) {
			if (worldPosition.getMapId() == 300100000) {
				WorldMapInstance worldMapInstance = worldPosition.getWorldMapInstance();
				// need check
				// getOwner().getController().useSkill(18572);

				killNpc(worldMapInstance.getNpcs(215402));
				killNpc(worldMapInstance.getNpcs(215403));
				killNpc(worldMapInstance.getNpcs(215404));
				killNpc(worldMapInstance.getNpcs(215405));
			}
		}
	}

	private void killNpc(List<Npc> npcs) {
		for (Npc npc : npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}

}
