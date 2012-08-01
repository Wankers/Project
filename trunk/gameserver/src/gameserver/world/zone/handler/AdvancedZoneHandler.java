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
package gameserver.world.zone.handler;

import gameserver.model.gameobjects.Creature;
import gameserver.world.zone.ZoneInstance;


/**
 * @author MrPoke
 *
 */
public interface AdvancedZoneHandler extends ZoneHandler {
	
	/**
	 * This call if creature die in zone.
	 * @param attacker
	 * @param target
	 * @return TRUE if hadle die event.
	 */
	public boolean onDie(Creature attacker, Creature target, ZoneInstance zone);
	
}
