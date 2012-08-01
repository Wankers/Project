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
package gameserver.model.templates.item.actions;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.item.ItemChargeService;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeItemAction")
public class ChargeAction extends AbstractItemAction {

	@XmlAttribute
	protected int capacity;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, targetItem);
		return conditioningItems.size() > 0;
	}

	@Override
	public void act(final Player player, Item parentItem, Item targetItem) {
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
			return;
		Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, targetItem);
		ItemChargeService.chargeItems(player, conditioningItems, capacity);
	}

}
