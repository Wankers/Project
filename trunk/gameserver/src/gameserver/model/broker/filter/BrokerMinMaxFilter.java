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
package gameserver.model.broker.filter;

import gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class BrokerMinMaxFilter extends BrokerFilter {

	private int min;
	private int max;

	/**
	 * @param min
	 * @param max
	 */
	public BrokerMinMaxFilter(int min, int max) {
		this.min = min * 100000;
		this.max = max * 100000;
	}

	@Override
	public boolean accept(ItemTemplate template) {
		return template.getTemplateId() >= min && template.getTemplateId() < max;
	}

}
