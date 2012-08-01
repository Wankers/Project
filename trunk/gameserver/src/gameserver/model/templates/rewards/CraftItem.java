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
package gameserver.model.templates.rewards;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 *
 */

/**
 * <p>
 * Java class for CraftItem complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CraftItem">
 *   &lt;complexContent>
 *     &lt;extension base="{}CraftReward">
 *       &lt;attribute name="minLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maxLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CraftItem")
public class CraftItem extends CraftReward {

	@XmlAttribute(name = "minLevel", required = true)
	protected int minLevel;

	@XmlAttribute(name = "maxLevel", required = true)
	protected int maxLevel;

	/**
	 * Gets the value of the minLevel property.
	 */
	public int getMinLevel() {
		return minLevel;
	}

	/**
	 * Gets the value of the maxLevel property.
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

}
