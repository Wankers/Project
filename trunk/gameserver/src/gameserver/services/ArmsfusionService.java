/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion-Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.DB;
import commons.database.DatabaseFactory;
import commons.database.dao.DAOManager;
import gameserver.dao.InventoryDAO;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.StorageType;
import gameserver.model.templates.item.ItemQuality;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import gameserver.network.aion.serverpackets.SM_INVENTORY_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.item.ItemPacketService;
import gameserver.services.item.ItemSocketService;
import gameserver.services.trade.PricesService;
import gameserver.utils.PacketSendUtility;

/**
 * This class is responsible of Armsfusion-related tasks (fusion,breaking)
 * 
 * @author Wakizashi modified by Source & xTz
 */
public class ArmsfusionService {

	private static final Logger log = LoggerFactory.getLogger(ArmsfusionService.class);

	public static void fusionWeapons(Player player, int firstItemUniqueId, int secondItemUniqueId) {
		Item firstItem = player.getInventory().getItemByObjId(firstItemUniqueId);
		if (firstItem == null)
			firstItem = player.getEquipment().getEquippedItemByObjId(firstItemUniqueId);

		Item secondItem = player.getInventory().getItemByObjId(secondItemUniqueId);
		if (secondItem == null)
			secondItem = player.getEquipment().getEquippedItemByObjId(secondItemUniqueId);

		/*
		 * Check if item is in bag
		 */
		if (firstItem == null || secondItem == null || !(player.getTarget() instanceof Npc))
			return;

		double priceRate = PricesService.getGlobalPrices(player.getRace()) * .01;
		double taxRate = PricesService.getTaxes(player.getRace()) * .01;
		double rarity = rarityRate(firstItem.getItemTemplate().getItemQuality());
		int priceMod = PricesService.getGlobalPricesModifier() * 2;
		int level = firstItem.getItemTemplate().getLevel();

		int price = (int) (priceMod * priceRate * taxRate * rarity * level * level);
		log.debug("Rarete: " + rarity + " Prix Ratio: " + priceRate + " Tax: " + taxRate + " Mod: " + priceMod
			+ " NiveauDeLArme: " + level);
		log.debug("Prix: " + price);

		if (player.getInventory().getKinah() < price) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_ENOUGH_MONEY(firstItem.getNameID(), secondItem.getNameID()));
			return;
		}

		/*
		 * Fusioned weapons must be not fusioned
		 */
		if (firstItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameID()));
			return;
		}
		if (secondItem.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_NOT_AVAILABLE(secondItem.getNameID()));
			return;
		}

		if (!firstItem.getItemTemplate().isCanFuse() || !secondItem.getItemTemplate().isCanFuse()) {
			PacketSendUtility.sendMessage(player, "You performed illegal operation, admin will catch you");
			log.info("[AUDIT] Client hack with item fusion, player: " + player.getName());
			return;
		}

		if (!firstItem.getItemTemplate().isTwoHandWeapon()) {
			// TODO retail message
			PacketSendUtility.sendMessage(player, "Can't fusion one-hand weapons");
			return;
		}

		// Fusioned weapons must have same type
		if (firstItem.getItemTemplate().getWeaponType() != secondItem.getItemTemplate().getWeaponType()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_DIFFERENT_TYPE);
			return;
		}

		/*
		 * Second weapon must have inferior or equal lvl. in relation to first weapon
		 */
		if (secondItem.getItemTemplate().getLevel() > firstItem.getItemTemplate().getLevel()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_ERROR_MAIN_REQUIRE_HIGHER_LEVEL);
			return;
		}

		firstItem.setFusionedItemId(secondItem.getItemId());
		firstItem.setPersistentState(PersistentState.UPDATE_REQUIRED);

		ItemSocketService.removeAllFusionStone(player, firstItem);

		if (secondItem.hasOptionalSocket())
			firstItem.setOptionalFusionSocket(secondItem.getOptionalSocket());
		else
			firstItem.setOptionalFusionSocket(0);

		ItemSocketService.copyFusionStones(secondItem, firstItem);

		//DAOManager.getDAO(InventoryDAO.class).store(firstItem, player);

		if (!player.getInventory().decreaseByObjectId(secondItemUniqueId, 1))
			return;

		ItemPacketService.updateItemAfterInfoChange(player, firstItem);
		player.getInventory().decreaseKinah(price);

		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUND_SUCCESS(firstItem.getNameID(), secondItem.getNameID()));
	}

	private static double rarityRate(ItemQuality rarity) {
		switch (rarity) {
			case COMMON:
				return 1.0;
			case RARE:
				return 1.25;
			case LEGEND:
				return 1.5;
			case UNIQUE:
				return 2.0;
			case EPIC:
				return 2.5;
			default:
				return 1.0;
		}
	}

	public static void breakWeapons(Player player, int weaponToBreakUniqueId) {
		Item weaponToBreak = player.getInventory().getItemByObjId(weaponToBreakUniqueId);
		if (weaponToBreak == null)
			weaponToBreak = player.getEquipment().getEquippedItemByObjId(weaponToBreakUniqueId);

		if (weaponToBreak == null || !(player.getTarget() instanceof Npc))
			return;

		if (!weaponToBreak.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOUND_ERROR_NOT_AVAILABLE(weaponToBreak.getNameID()));
			return;
		}

		weaponToBreak.setFusionedItemId(0);

		/* TODO: Fix Hack: DB Hack, Find out why Item update to only armsbreaking does not persist to DB */
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE inventory SET fusioned_item = 0 WHERE item_unique_id = " + weaponToBreak.getObjectId());
			stmt.executeUpdate();
			stmt.close();
			con.close();
		} catch(Exception e) {
			log.error("Could not save armsbreaking!", e);
		} finally {
			DatabaseFactory.close(con);
		}
		
		ItemSocketService.removeAllFusionStone(player, weaponToBreak);
		//DAOManager.getDAO(InventoryDAO.class).store(weaponToBreak, player);
		
		//ItemPacketService.updateItemAfterInfoChange(player, weaponToBreak);
		// TODO: Fix Hack: Check this against retail, item update does not update combine status must refresh item in client for now
		PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(weaponToBreak.getObjectId()));
		ItemPacketService.sendStorageUpdatePacket(player, StorageType.CUBE, weaponToBreak);
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMPOUNDED_ITEM_DECOMPOUND_SUCCESS(weaponToBreak.getNameID()));
	}
}
