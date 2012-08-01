/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.model.templates.item.actions;

import gameserver.dataholders.DataManager;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.model.templates.assembly.AssemblyTemplate;
import gameserver.model.templates.assembly.Part;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pixfid
 */
public class AssemblyItemAction extends AbstractItemAction {

    private static final Logger log = LoggerFactory.getLogger(AssemblyItemAction.class);
    private List<Part> part;

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        return true;
    }

    @Override
    public void act(final Player player, final Item parentItem, Item targetItem) {

        final Storage storage = player.getInventory();
        ItemTemplate stoneTemplate = ItemService.getItemTemplate(parentItem.getItemId());



        if (stoneTemplate.getAid() != 0) {
            int aid = stoneTemplate.getAid();
            final int item = DataManager.ASSEMBLY_DATA.findaid(aid);
            final AssemblyTemplate assembly = DataManager.ASSEMBLY_DATA.getAssemblyTemplate(item);
            part = assembly.getPart();
            for (int i = assembly.getParts_count(); i-- > 0;) {
                long count = storage.getItemCountByItemId(part.get(i).getId());
                if (storage.getFreeSlots() < count - 1) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
                    break;
                }
                if (count == 0) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.ASSEMBLY_ITEM_NOT_ENOUGH);
                    break;
                } else {

                    PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
                            parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 5000, 0, 0));
                    player.getController().cancelTask(TaskId.ITEM_USE);
                    player.getController().addTask(TaskId.ITEM_USE,
                            ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 1, 0));
                            for (int t = assembly.getParts_count(); t-- > 0;) {
                                player.getInventory().decreaseByItemId(part.get(t).getId(), 1);
                            }
                            ItemService.addItem(player, item, 1);
                        }
                    }, 5000));

                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.ASSEMBLY_ITEM_SUCCEEDED);
                    break;
                }
            }
        }

    }
}
