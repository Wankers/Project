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
package gameserver.model.templates.itemgroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javolution.util.FastMap;

import org.apache.commons.lang.math.IntRange;

import gameserver.model.templates.rewards.CraftReward;
import gameserver.model.templates.rewards.IdReward;

/**
 * @author Rolandas
 */
public abstract class CraftGroup extends ItemGroup {

	private FastMap<Integer, FastMap<IntRange, List<CraftReward>>> dataHolder;
	
	public IdReward[] getRewards(Integer skillId) {
		if (!dataHolder.containsKey(skillId))
			return new IdReward[0];
		List<IdReward> result = new ArrayList<IdReward>();
		for (List<CraftReward> items : dataHolder.get(skillId).values())
			result.addAll(items);
		return result.toArray(new IdReward[0]);		
	}
	
	public IdReward[] getRewards(Integer skillId, Integer skillPoints) {
		if (!dataHolder.containsKey(skillId))
			return new IdReward[0];
		List<IdReward> result = new ArrayList<IdReward>();
		for (Entry<IntRange, List<CraftReward>> entry : dataHolder.get(skillId).entrySet()) {
			if (!entry.getKey().containsInteger(skillPoints))
				continue;
			result.addAll(entry.getValue());
		}
		return result.toArray(new IdReward[0]);			
	}

	/**
	 * @return the dataHolder
	 */
	public FastMap<Integer, FastMap<IntRange, List<CraftReward>>> getDataHolder() {
		return dataHolder;
	}

	/**
	 * @param dataHolder the dataHolder to set
	 */
	public void setDataHolder(FastMap<Integer, FastMap<IntRange, List<CraftReward>>> dataHolder) {
		this.dataHolder = dataHolder;
	}
}
