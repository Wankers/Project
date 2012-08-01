/*
 * This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.kromedesTrial;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skillengine.SkillEngine;
import gameserver.utils.PacketSendUtility;

/**
 * @author Tiger0319, Gigi, xTz
 */
@AIName("krbuff")
public class KromedesBuffAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
			case 730336:
				SkillEngine.getInstance().getSkill(player, 19216, 1, player).useSkill();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400655));
				break;
			case 730337:
				SkillEngine.getInstance().getSkill(player, 19217, 1, player).useSkill();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400656));
				AI2Actions.deleteOwner(this);
				break;
			case 730338:
				SkillEngine.getInstance().getSkill(player, 19218, 1, player).useSkill();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400657));
				AI2Actions.deleteOwner(this);
				break;
			case 730339:
				SkillEngine.getInstance().getSkill(player, 19219, 1, player).useSkill();
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400658));
				break;
		}
	}
}