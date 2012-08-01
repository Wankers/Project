/*
 * This file is part of Aion Extreme Emulator <www.aion-core.net>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

/**
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TitleAddAction")
public class TitleAddAction extends AbstractItemAction {

	@XmlAttribute
	protected int titleid;
  @XmlAttribute
  protected Integer minutes;

	/*
	 * (non-Javadoc)
	 * @see
	 * gameserver.itemengine.actions.AbstractItemAction#canAct(gameserver.model.gameobjects.player
	 * .Player, gameserver.model.gameobjects.Item, gameserver.model.gameobjects.Item)
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (titleid == 0 || parentItem == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (player.getTitleList().contains(titleid)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gameserver.itemengine.actions.AbstractItemAction#act(gameserver.model.gameobjects.player
	 * .Player, gameserver.model.gameobjects.Item, gameserver.model.gameobjects.Item)
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);

		if (player.getTitleList().addTitle(titleid, false, minutes == null ? 0 : ((int)(System.currentTimeMillis()/1000))+minutes*60)) {
			Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
			player.getInventory().delete(item);
		}
	}
}
