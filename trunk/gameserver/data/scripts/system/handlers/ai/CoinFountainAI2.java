package ai;

import commons.utils.Rnd;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.configs.main.RateConfig;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RewardType;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;



@AIName("coinfountain")
public class CoinFountainAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getCommonData().getLevel() >= 50) {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011, 21050));
        } else {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId) {
        switch (dialogId) {
            case 10000:
                if (hasItem(player, 186000030)) {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 5, 21050));
                } else {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
                }
                break;
            case 18:
                Item item = player.getInventory().getFirstItemByItemId(186000030);
                player.getInventory().decreaseByItemId(item.getObjectId(), 1);
                giveItem(player);
                player.getCommonData().addExp(1043900, RewardType.QUEST);
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1008, 21050));
                break;
        }
        return true;
    }

    private boolean hasItem(Player player, int itemId) {
        return player.getInventory().getItemCountByItemId(itemId) > 0;
    }

    private void giveItem(Player player) {
        int rnd = Rnd.get(0, 100);
        if (rnd < 5) {
            ItemService.addItem(player, 186000096, 1);
        } else {
            ItemService.addItem(player, 182005205, 1);
        }
    }
}
