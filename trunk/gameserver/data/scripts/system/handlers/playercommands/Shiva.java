package playercommands;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.ItemType;
import gameserver.services.item.ItemPacketService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Tago modified by Wakizashi and Ney
 */
public class Shiva extends ChatCommand {

	public Shiva() {
		super("shiva");
	}

	@Override
	public void execute(Player player, String... params) {	
                int enchant = 0;
                try {
                        enchant = params[0] == null ? enchant : Integer.parseInt(params[0]);
                }
                catch (Exception ex) {
                        onFail(player, "Fail");
                        return;
                }
                enchant(player, enchant);
                return;	
	}
	private void enchant(Player player, int enchant) {
		for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isUpgradeble(targetItem)) {
				if (targetItem.getEnchantLevel() == enchant)
					continue;
				if (enchant > 15)
					enchant = 15;
				if (enchant < 0)
					enchant = 0;

				targetItem.setEnchantLevel(enchant);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		}
                
		PacketSendUtility.sendMessage(player, "Tous vos objets equipes sont enchantes au level  " + enchant);
	}

	/**
	 * Verify if the item is enchantble and/or socketble
	 * 
	 * @param item
	 */
	public static boolean isUpgradeble(Item item) {
		if (item.getItemTemplate().isNoEnchant())
			return false;
		if (item.getItemTemplate().isWeapon())
			return true;
		if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
			at == 2 || /* Sub Hand */
			at == 8 || /* Jacket */
			at == 16 || /* Gloves */
			at == 32 || /* Boots */
			at == 2048 || /* Shoulder */
			at == 4096 || /* Pants */
			at == 131072 || /* Main Off Hand */
			at == 262144) /* Sub Off Hand */
				return true;
		}
		return false;

	}

	/**
	 * Returns the max number of manastones that can be socketed
	 * 
	 * @param item
	 */
	public static int getMaxSlots(Item item) {
		int slots;
		switch (item.getItemTemplate().getItemQuality()) {
			case COMMON:
			case JUNK:
				slots = 1;
				break;
			case RARE:
				slots = 2;
				break;
			case LEGEND:
				slots = 3;
				break;
			case UNIQUE:
				slots = 4;
				break;
			case EPIC:
				slots = 5;
				break;
			default:
				slots = 0;
				break;
		}
		if (item.getItemTemplate().getItemType() == ItemType.DRACONIC)
			slots += 1;
		if (item.getItemTemplate().getItemType() == ItemType.ABYSS)
			slots += 2;
		return slots;
	}


	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntaxe .shiva : \n"
			+ "  Utilisez .shiva <value>.\n"
			+ "  Rappel : cette commande enchantera vos objets equipes au level <value>.\n"
			+ "  Par exemple: (.shiva 15) enchantera vos objets equipes au level +15");
	}
}
