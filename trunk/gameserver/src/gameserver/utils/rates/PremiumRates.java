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

import gameserver.configs.main.RateConfig;

/**
 * @author ATracer
 */
public class PremiumRates extends Rates {

	@Override
	public float getGroupXpRate() {
		return RateConfig.PREMIUM_GROUPXP_RATE;
	}

	@Override
	public float getApNpcRate() {
		return RateConfig.PREMIUM_AP_NPC_RATE;
	}

	@Override
	public float getApPlayerGainRate() {
		return RateConfig.PREMIUM_AP_PLAYER_GAIN_RATE;
	}

	@Override
	public float getXpPlayerGainRate() {
		return RateConfig.PREMIUM_XP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getApPlayerLossRate() {
		return RateConfig.PREMIUM_AP_PLAYER_LOSS_RATE;
	}

	@Override
	public float getDropRate() {
		return RateConfig.PREMIUM_DROP_RATE;
	}

	@Override
	public float getQuestKinahRate() {
		return RateConfig.PREMIUM_QUEST_KINAH_RATE;
	}

	@Override
	public float getQuestXpRate() {
		return RateConfig.PREMIUM_QUEST_XP_RATE;
	}

	@Override
	public float getQuestApRate() {
		return RateConfig.PREMIUM_QUEST_AP_RATE;
	}

	@Override
	public float getXpRate() {
		return RateConfig.PREMIUM_XP_RATE;
	}

	/*
	 * (non-Javadoc)
	 * @see gameserver.utils.rates.Rates#getCraftingXPRate()
	 */
	@Override
	public float getCraftingXPRate() {
		return RateConfig.PREMIUM_CRAFTING_XP_RATE;
	}

	/*
	 * (non-Javadoc)
	 * @see gameserver.utils.rates.Rates#getGatheringXPRate()
	 */
	@Override
	public float getGatheringXPRate() {
		return RateConfig.PREMIUM_GATHERING_XP_RATE;
	}

	@Override
	public float getPetFeedingRate() {
		return RateConfig.PREMIUM_PET_FEEDING_RATE;
	}

	@Override
	public float getDpNpcRate() {
		return RateConfig.PREMIUM_DP_NPC_RATE;
	}

	@Override
	public float getDpPlayerRate() {
		return RateConfig.PREMIUM_DP_PLAYER_RATE;
	}
}
