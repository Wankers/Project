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
package gameserver.ai2.event;

/**
 * @author ATracer
 */
public enum AIEventType {
	ACTIVATE,
	DEACTIVATE,

	/**
	 * Creature is being attacked (internal)
	 */
	ATTACK, 
	/**
	 * Creature's attack part is complete (internal)
	 */
	ATTACK_COMPLETE,
	/**
	 * Creature's stopping attack (internal)
	 */
	ATTACK_FINISH,
	/**
	 * Some neighbor creature is being attacked (broadcast)
	 */
	CREATURE_ATTACKED,
	
	/**
	 * Creature is attacking (broadcast)
	 */
	CREATURE_ATTACKING,

	MOVE_VALIDATE,
	MOVE_ARRIVED,
    THINK,
	CREATURE_SEE,
	CREATURE_MOVED,
	CREATURE_AGGRO,
	SPAWNED,
	RESPAWNED,
	DESPAWNED,
	DIED,

	TARGET_REACHED,
	TARGET_TOOFAR,
	TARGET_GIVEUP,
	TARGET_CHANGED,
	FOLLOW_ME,
	STOP_FOLLOW_ME,

	NOT_AT_HOME,
	BACK_HOME,

	DIALOG_START,
	DIALOG_FINISH,

	DROP_REGISTERED
}
