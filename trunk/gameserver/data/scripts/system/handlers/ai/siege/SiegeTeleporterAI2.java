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
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.siege;

import ai.GeneralNpcAI2;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.siege.SiegeNpc;
import gameserver.network.aion.serverpackets.SM_FORTRESS_INFO;
import gameserver.services.SiegeService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.knownlist.Visitor;

/**
 * @author Source
 */
@AIName("siege_teleporter")
public class SiegeTeleporterAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleDespawned() {
		canTeleport(false);
		super.handleDespawned();
	}

	@Override
	protected void handleSpawned() {
		canTeleport(true);
		super.handleSpawned();
	}

	private void canTeleport(final boolean status) {
		final int id = ((SiegeNpc) getOwner()).getSiegeId();
		SiegeService.getInstance().getFortress(id).setCanTeleport(status);

		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(id, status));
			}

		});
	}

}