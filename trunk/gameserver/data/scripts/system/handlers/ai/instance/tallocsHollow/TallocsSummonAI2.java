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

import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.controllers.SummonController;
import gameserver.controllers.effect.EffectController;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_TRANSFORM_IN_SUMMON;
import gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("tallocssummon")
public class TallocsSummonAI2 extends NpcAI2 {

	private boolean isTransformed = false;

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId) {
		if (dialogId == 59 && !isTransformed) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			if (player.getSummon() != null) { // to do remove
				PacketSendUtility.sendMessage(player, "please dismiss your summon first.");
				return true;
			}

			Summon summon = new Summon(getObjectId(), new SummonController(), getSpawnTemplate(), getObjectTemplate(), getObjectTemplate().getLevel());
			player.setSummon(summon);
			summon.setMaster(player);
			summon.setTarget(player.getTarget());
			summon.setKnownlist(getKnownList());
			summon.setEffectController(new EffectController(summon));
			summon.setPosition(getPosition());
			summon.setLifeStats(getLifeStats());
			PacketSendUtility.sendPacket(player, new SM_TRANSFORM_IN_SUMMON(player, getObjectId()));
			PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getObjectId(), 0, 38, 0));
			isTransformed = true;
		}
		return true;
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (!isTransformed) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
	}
}
