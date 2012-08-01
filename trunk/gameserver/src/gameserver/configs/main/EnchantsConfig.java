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

public class EnchantsConfig {

	/**
	 * Supplement Additional Rates
	 */
	@Property(key = "gameserver.supplement.lesser", defaultValue = "10")
	public static int LSUP;
	@Property(key = "gameserver.supplement.regular", defaultValue = "20")
	public static int RSUP;
	@Property(key = "gameserver.supplement.greater", defaultValue = "30")
	public static int GSUP;
	@Property(key = "gameserver.supplement.socketing", defaultValue = "100")
	public static int FESO;

	/**
	 * Max enchant level
	 */
	@Property(key = "gameserver.enchant.type1", defaultValue = "10")
	public static int ENCHANT_MAX_LEVEL_TYPE1;
	@Property(key = "gameserver.enchant.type2", defaultValue = "15")
	public static int ENCHANT_MAX_LEVEL_TYPE2;

	/**
	 * ManaStone Rates
	 */
	@Property(key = "gameserver.base.manastone", defaultValue = "50")
	public static float MANA_STONE;
	@Property(key = "gameserver.base.enchant", defaultValue = "60")
	public static float ENCHANT_STONE;

	@Property(key = "gameserver.noextract_item", defaultValue = "100000040,100000041")
	public static String NO_EXTRACT_ITEM;
}