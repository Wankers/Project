/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 * along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.geo.GeoService;


/**
 * @author MrPoke
 *
 */
public class Geo extends ChatCommand{

	public Geo() {
		super("geo");
	}

	@Override
	public void execute(Player player, String... params) {
		if ("z".startsWith(params[0])){
			PacketSendUtility.sendMessage(player, "GeoZ: "+GeoService.getInstance().getZ(player)+ " current Z: "+player.getZ());
		}
	}
	
}
