package gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import gameserver.controllers.ObserveController;
import gameserver.controllers.PlayerController;
import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.DescriptionId;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SealUnSealAction")
public class SealUnSealAction extends AbstractItemAction
{

  @XmlAttribute(name="time")
  protected int time;

  @XmlAttribute(name="type")
  protected SealType type;
  @Override
  public boolean canAct(Player player, Item usedItem, Item targetItem)
  {
    if (this.type == SealType.UNSEAL)
    {
      if (targetItem.getUnSeal() == 1)
        return true;
    }
    else
    {
      if (targetItem.getUnSeal() == 0)
        return true;
      PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400810, new Object[0]));
    }
    return false;
  }
  @Override
  public void act(final Player player, final Item usedItem, final Item targetItem)
  {
    final int sealStatus = targetItem.getUnSeal();
	PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usedItem.getObjectId(), usedItem.getItemTemplate().getTemplateId(), 3000, 0, 0));
    player.getController().cancelTask(TaskId.ITEM_USE);
    final ActionObserver moveObserver = new ActionObserver(ObserverType.MOVE) {
		@Override
		public void moved() {
			player.getController().cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_SEAL_START_CANCEL(new DescriptionId(targetItem.getNameID())));
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usedItem.getObjectId(), usedItem.getItemTemplate().getTemplateId(), 0, 2, 0));
			targetItem.setUnseal(sealStatus);
		}
	};
	player.getObserveController().attach(moveObserver);
	
    player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
    {
      @Override
      public void run()
      {
        if (SealUnSealAction.this.type == SealUnSealAction.SealType.UNSEAL)
          targetItem.setUnseal(0);
        else
          targetItem.setUnseal(1);

        player.getObserveController().removeObserver(moveObserver);
        player.getController().cancelTask(TaskId.ITEM_USE);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usedItem.getObjectId(), usedItem.getItemTemplate().getTemplateId(), 0, 1, 0));
        player.getInventory().decreaseByItemId(usedItem.getItemId(), 1);
        PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_SEAL_START_DONE(new DescriptionId(targetItem.getNameID())));
        targetItem.setPersistentState(PersistentState.UPDATE_REQUIRED);        
      }
    }
    , 3000));
  }

  public static enum SealType
  {
    UNSEAL, SEAL
  }
}