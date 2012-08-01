/*
 * This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 * along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author Source
 */
public class GroupToMe extends ChatCommand {

	public GroupToMe() {
		super("grouptome");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, null);
			return;
		}

		Player groupToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (groupToMove == null) {
			PacketSendUtility.sendMessage(admin, "The player is not online.");
			return;
		}

		if (!groupToMove.isInGroup2()) {
			PacketSendUtility.sendMessage(admin, groupToMove.getName() + " is not in group.");
			return;
		}

		for (Player target : groupToMove.getPlayerGroup2().getMembers())
			if (target != admin) {
				TeleportService.teleportTo(target, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(),
					admin.getZ(), admin.getHeading(), 3000, true);
				PacketSendUtility.sendMessage(target, "You have been summoned by " + admin.getName() + ".");
				PacketSendUtility.sendMessage(admin, "You summon " + target.getName() + ".");
			}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //grouptome <player>");
	}
}
