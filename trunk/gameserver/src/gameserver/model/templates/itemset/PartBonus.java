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
package gameserver.model.templates.itemset;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import gameserver.model.stats.calc.functions.StatFunction;
import gameserver.model.templates.stats.ModifiersTemplate;

/**
 * @author ATracer
 */
@XmlRootElement(name = "PartBonus")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartBonus {

	@XmlAttribute
	protected int count;
	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;

	public List<StatFunction> getModifiers() {
		return modifiers != null ? modifiers.getModifiers() : null;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
}
