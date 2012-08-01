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
package gameserver.model.stats.calc.functions;

import gameserver.model.stats.calc.Stat2;
import gameserver.model.stats.calc.StatOwner;
import gameserver.model.stats.container.StatEnum;

/**
 * @author ATracer
 */
public interface IStatFunction extends Comparable<IStatFunction>{

	StatEnum getName();

	boolean isBonus();

	int getPriority();

	int getValue();

	boolean validate(Stat2 stat, IStatFunction statFunction);

	void apply(Stat2 stat);
	
	StatOwner getOwner();
}
