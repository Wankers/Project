/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gameserver.network.aion.serverpackets;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.iteminfo.ItemInfoBlob;
import gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author kosyachok
 * @author -Nemesiss-
 */
public class SM_WAREHOUSE_UPDATE_ITEM extends AionServerPacket {

	private Player player;
	private Item item;
	private int warehouseType;
	private ItemUpdateType updateType;

	public SM_WAREHOUSE_UPDATE_ITEM(Player player, Item item, int warehouseType, ItemUpdateType updateType) {
		this.player = player;
		this.item = item;
		this.warehouseType = warehouseType;
		this.updateType = updateType;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeC(warehouseType);
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = new ItemInfoBlob(player, item);
		itemInfoBlob.addBlobEntry(ItemBlobType.GENERAL_INFO);
		itemInfoBlob.writeMe(getBuf());

		if (updateType.isSendable())
			writeH(updateType.getMask());
	}
}
