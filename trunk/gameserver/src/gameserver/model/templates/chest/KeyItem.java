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
package gameserver.model.templates.chest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyItem")
public class KeyItem {

	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "itemid")
	protected int itemid;
	@XmlAttribute(name = "quantity")
	protected int quantity;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the itemid
	 */
	public int getItemId() {
		return itemid;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
}
