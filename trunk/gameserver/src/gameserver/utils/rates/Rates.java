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
package gameserver.utils.rates;

/**
 * @author ATracer
 */
public abstract class Rates {

	public abstract float getGroupXpRate();

	public abstract float getXpRate();

	public abstract float getApNpcRate();

	public abstract float getApPlayerGainRate();

	public abstract float getXpPlayerGainRate();
	
	public abstract float getApPlayerLossRate();

	public abstract float getGatheringXPRate();

	public abstract float getCraftingXPRate();

	public abstract float getDropRate();

	public abstract float getPetFeedingRate();

	public abstract float getQuestXpRate();

	public abstract float getQuestKinahRate();

	public abstract float getQuestApRate();

	public abstract float getDpNpcRate();

	public abstract float getDpPlayerRate();

	/**
	 * @param membership
	 * @return Rates
	 */
	public static Rates getRatesFor(byte membership) {
		switch (membership) {
			case 0:
				return new RegularRates();
			case 1:
				return new PremiumRates();
			case 2:
				return new VipRates();
			default:
				return new VipRates();
		}
	}
}
