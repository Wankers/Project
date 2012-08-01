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

import javax.xml.bind.annotation.*;

import gameserver.model.templates.rewards.CraftItem;
import gameserver.model.templates.rewards.IdReward;

/**
 * @author Rolandas
 *
 */
/**
 * <p>
 * Java class for CraftItemGroup complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CraftItemGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{}ItemGroup">
 *       &lt;sequence>
 *         &lt;element name="item" type="{}CraftItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CraftItemGroup")
public class CraftItemGroup extends CraftGroup {

	@XmlElement(name = "item")
	protected List<CraftItem> items;

	/**
	 * Gets the value of the item property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the item property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getItems().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link CraftItem }
	 */
	public List<CraftItem> getItems() {
		if (items == null) {
			items = new ArrayList<CraftItem>();
		}
		return this.items;
	}

	/*
	 * (non-Javadoc)
	 * @see gameserver.model.templates.itemgroups.ItemGroup#getRewards()
	 */
	@Override
	public IdReward[] getRewards() {
		return getItems().toArray(new IdReward[0]);
	}

}
