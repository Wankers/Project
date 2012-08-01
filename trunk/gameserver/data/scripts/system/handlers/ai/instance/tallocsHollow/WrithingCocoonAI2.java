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
package ai.instance.tallocsHollow;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("writhingcocoon")
public class WrithingCocoonAI2 extends NpcAI2 {

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId) {
		if (dialogId == 1012 && player.getInventory().decreaseByItemId(185000088, 1)) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId()) {
				case 730232:
					Npc npc = getPosition().getWorldMapInstance().getNpc(730233);
					if (npc != null) {
						npc.getController().onDelete();
					}
					spawn(799500, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(390510)); //Will you accompany me? Tell me if you will.
					break;
				case 730233:
					Npc npc1 = getPosition().getWorldMapInstance().getNpc(730232);
					if (npc1 != null) {
						npc1.getController().onDelete();
					}
					spawn(799501, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(390511)); //Let me know if you need my help.
					break;
			}
			AI2Actions.deleteOwner(this);
		}
		else if (dialogId == 1012) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1097));
		}
		return true;
	}

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}
}
