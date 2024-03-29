/*
 * This file is part of aion-unique <aion-unique.com>.
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
package gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.stats.calc.Stat2;
import gameserver.model.stats.calc.StatCondition;
import gameserver.model.stats.calc.functions.IStatFunction;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Condition")
public abstract class Condition implements StatCondition {

	/**
	 * Validate condition specified in template
	 * 
	 * @param env
	 * @return true or false
	 */
	public abstract boolean validate(Skill env);

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return true;
	}
	
	public boolean validate(Effect effect) {
		return true;
	}

}
