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
package ai.instance.pvpArenas;

import ai.ActionItemNpcAI2;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.skillengine.SkillEngine;

/**
 *
 * @author xTz
 */
@AIName("antiaircraftgun")
public class AntiAirCraftGunAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		Npc owner = getOwner();
		TeleportService.teleportTo(player, owner.getWorldId(), owner.getInstanceId(), owner.getX(), owner.getY(), owner.getZ(), owner.getHeading(), 3000, true);
		player.getController().stopProtectionActiveTask();
		int morphSkillId = 0;
		int level = 0;
		switch(getNpcId()) {
			case 701185: // 46 lvl morph 218803
			case 701321:
				morphSkillId = 20048;
				level = 46;
				break;
			case 701199: // 51 lvl morph 218804
			case 701322:
				morphSkillId = 20049;
				level = 51;
				break;
			case 701213: // 56 lvl morph 218805
			case 701323:
				morphSkillId = 20050;
				level = 56;
				break;
		}
		AI2Actions.deleteOwner(this);
		SkillEngine.getInstance().getSkill(player, morphSkillId, level, player).useSkill();
	}
}