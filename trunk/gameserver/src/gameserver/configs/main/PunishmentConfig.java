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
 * @author synchro2
 */
public class PunishmentConfig {

	@Property(key = "gameserver.punishment.enable", defaultValue = "false")
	public static boolean PUNISHMENT_ENABLE;

	@Property(key = "gameserver.punishment.type", defaultValue = "1")
	public static int PUNISHMENT_TYPE;

	@Property(key = "gameserver.punishment.time", defaultValue = "1440")
	public static int PUNISHMENT_TIME;
}