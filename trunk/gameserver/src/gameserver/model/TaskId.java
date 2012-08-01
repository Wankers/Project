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
package gameserver.model;

/**
 * @author ATracer
 */
public enum TaskId {
	DECAY,
	PRISON,
	PROTECTION_ACTIVE,
	DROWN,
	DESPAWN,
	/**
	 * Quest task with timer
	 */
	QUEST_TIMER,
	/**
	 * Follow task checker
	 */
	QUEST_FOLLOW,
	PLAYER_UPDATE,
	INVENTORY_UPDATE,
	GAG,
	ITEM_USE,
	ACTION_ITEM_NPC,
	SKILL_USE,
	PORTAL,
	GATHERABLE,
	PET_UPDATE
}
