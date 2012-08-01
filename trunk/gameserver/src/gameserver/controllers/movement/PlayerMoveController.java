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
package gameserver.controllers.movement;

import gameserver.configs.main.FallDamageConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 */
public class PlayerMoveController extends PlayableMoveController<Player> {

	private float fallDistance;
	private float lastFallZ;
	
	public PlayerMoveController(Player owner) {
		super(owner);
	}

	public void updateFalling(float newZ) {
		if (lastFallZ != 0) {
			fallDistance += lastFallZ - newZ;
			if (fallDistance >= FallDamageConfig.MAXIMUM_DISTANCE_MIDAIR) {
				StatFunctions.calculateFallDamage(owner, fallDistance, false);
			}
		}
		lastFallZ = newZ;
	}

	public void stopFalling(float newZ) {
		if (lastFallZ != 0) {
			if (!owner.isFlying()) {
				StatFunctions.calculateFallDamage(owner, fallDistance, true);
			}
			fallDistance = 0;
			lastFallZ = 0;
		}
	}

}
