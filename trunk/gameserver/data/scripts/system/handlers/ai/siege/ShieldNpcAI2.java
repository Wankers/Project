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

import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SHIELD_EFFECT;
import gameserver.services.SiegeService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.knownlist.Visitor;

/**
 * @author Source
 */
@AIName("siege_shieldnpc")
public class ShieldNpcAI2 extends SiegeNpcAI2 {

	@Override
	protected void handleDespawned() {
		sendShieldPacket(false);
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		sendShieldPacket(false);
		super.handleDied();
	}

	@Override
	protected void handleSpawned() {
		sendShieldPacket(true);
		super.handleSpawned();
	}

	private void sendShieldPacket(boolean shieldStatus) {
		int id = getSpawnTemplate().getSiegeId();
		SiegeService.getInstance().getFortress(id).setUnderShield(shieldStatus);

		final SM_SHIELD_EFFECT packet = new SM_SHIELD_EFFECT(id);
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

}