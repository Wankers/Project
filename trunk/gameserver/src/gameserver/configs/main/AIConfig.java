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
package gameserver.configs.main;

import commons.configuration.Property;

/**
 * @author ATracer
 */
public class AIConfig {

	/**
	 * Debug (for developers)
	 */
	@Property(key = "gameserver.ai.move.debug", defaultValue = "true")
	public static boolean MOVE_DEBUG;

	@Property(key = "gameserver.ai.event.debug", defaultValue = "false")
	public static boolean EVENT_DEBUG;

	@Property(key = "gameserver.ai.oncreate.debug", defaultValue = "false")
	public static boolean ONCREATE_DEBUG;

	/**
	 * Enable NPC movement
	 */
	@Property(key = "gameserver.npcmovement.enable", defaultValue = "true")
	public static boolean ACTIVE_NPC_MOVEMENT;

	/**
	 * Minimum movement delay
	 */
	@Property(key = "gameserver.npcmovement.delay.minimum", defaultValue = "3")
	public static int MINIMIMUM_DELAY;

	/**
	 * Maximum movement delay
	 */
	@Property(key = "gameserver.npcmovement.delay.maximum", defaultValue = "15")
	public static int MAXIMUM_DELAY;

	/**
	 * Npc Shouts activator
	 */
	@Property(key = "gameserver.npcshouts.enable", defaultValue = "false")
	public static boolean SHOUTS_ENABLE;
}
