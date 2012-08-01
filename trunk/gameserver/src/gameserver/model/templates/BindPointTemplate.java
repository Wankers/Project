/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  Aion-Extreme Emulator is a free software: you can redistribute it and/or modify
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
package gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author avol
 */
@XmlRootElement(name = "bind_points")
@XmlAccessorType(XmlAccessType.NONE)
public class BindPointTemplate {

	@XmlAttribute(name = "name", required = true)
	private String name;

	@XmlAttribute(name = "npcid")
	private int npcId;

	@XmlAttribute(name = "price")
	private int price = 0;

	public String getName() {
		return name;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getPrice() {
		return price;
	}
}
