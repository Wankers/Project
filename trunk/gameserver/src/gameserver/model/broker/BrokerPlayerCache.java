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
package gameserver.model.broker;

import java.util.ArrayList;
import java.util.List;

import gameserver.model.gameobjects.BrokerItem;

/**
 * @author ATracer
 */
public class BrokerPlayerCache {

	private BrokerItem[] brokerListCache = new BrokerItem[0];
	private int brokerMaskCache;
	private int brokerSoftTypeCache;
	private int brokerStartPageCache;
	private List<Integer> itemList = new ArrayList<Integer>();

	/**
	 * @return the brokerListCache
	 */
	public BrokerItem[] getBrokerListCache() {
		return brokerListCache;
	}

	/**
	 * @param brokerListCache
	 *          the brokerListCache to set
	 */
	public void setBrokerListCache(BrokerItem[] brokerListCache) {
		this.brokerListCache = brokerListCache;
	}

	/**
	 * @return the brokerMaskCache
	 */
	public int getBrokerMaskCache() {
		return brokerMaskCache;
	}

	/**
	 * @param brokerMaskCache
	 *          the brokerMaskCache to set
	 */
	public void setBrokerMaskCache(int brokerMaskCache) {
		this.brokerMaskCache = brokerMaskCache;
	}

	/**
	 * @return the brokerSoftTypeCache
	 */
	public int getBrokerSortTypeCache() {
		return brokerSoftTypeCache;
	}

	/**
	 * @param brokerSoftTypeCache
	 *          the brokerSoftTypeCache to set
	 */
	public void setBrokerSortTypeCache(int brokerSoftTypeCache) {
		this.brokerSoftTypeCache = brokerSoftTypeCache;
	}

	/**
	 * @return the brokerStartPageCache
	 */
	public int getBrokerStartPageCache() {
		return brokerStartPageCache;
	}

	/**
	 * @param the
	 *          getSearchItemList
	 */
	public List<Integer> getSearchItemList() {
		if (this.itemList == null)
			return null;
		return this.itemList;
	}

	/**
	 * @param brokerStartPageCache
	 *          the brokerStartPageCache to set
	 */
	public void setBrokerStartPageCache(int brokerStartPageCache) {
		this.brokerStartPageCache = brokerStartPageCache;
	}

	/**
	 * @param setSearchItemsList
	 *          the searched item list to set
	 */
	public void setSearchItemsList(List<Integer> itemList) {
		this.itemList = itemList;
	}
}
