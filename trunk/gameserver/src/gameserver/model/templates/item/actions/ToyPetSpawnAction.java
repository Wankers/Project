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

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.ItemUseObserver;
import gameserver.model.DescriptionId;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.KiskService;
import gameserver.spawnengine.SpawnEngine;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author Sarynth, Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToyPetSpawnAction")
public class ToyPetSpawnAction extends AbstractItemAction {

	@XmlAttribute
	protected int npcid;

	@XmlAttribute
	protected int time;

	/**
	 * @return the Npc Id
	 */
	public int getNpcId() {
		return npcid;
	}

	public int getTime() {
		return time;
	}

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getFlyState() != 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_BINDSTONE_ITEM_WHILE_FLYING);
			return false;
		}
		if (player.isInInstance()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FAR_FROM_NPC);
			return false;
		}
		//TODO Kisk zone reparse
		/*if (player.getWorldType() == WorldType.BALAUREA || player.getWorldType() == WorldType.ABYSS)
			if (player.isInsideZoneType(ZoneType.SIEGE)) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_LOCATION);
				return false;
			}*/
		switch (player.getWorldId()) {
			case 110010000:
			case 120010000:
			case 110020000:
			case 120020000:
			case 600010000:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_LOCATION);
				return false;
			default:
				break;
		}

		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		// ShowAction
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
				parentItem.getObjectId(), parentItem.getItemId(), 10000, 0, 0), true);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getDelayId());
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
			}
		};
		
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player,
					new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 1), true);
				player.getObserveController().removeObserver(observer);
				// RemoveKisk
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
					return;
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				float x = player.getX();
				float y = player.getY();
				float z = player.getZ();
				byte heading = (byte) ((player.getHeading() + 60) % 120);
				int worldId = player.getWorldId();
				int instanceId = player.getInstanceId();
				SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcid, x, y, z, heading);

				final Kisk kisk = VisibleObjectSpawner.spawnKisk(spawn, instanceId, player);

				// Schedule Despawn Action
				Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						kisk.getController().onDelete();
					}
				}, 7200000);
				// Fixed 2 hours 2 * 60 * 60 * 1000
				kisk.getController().addTask(TaskId.DESPAWN, task);

				// ShowFinalAction
				//TODO Bad idea...
				//player.getController().cancelUseItem();
				player.getController().cancelTask(TaskId.ITEM_USE);
				

				if (kisk.getMaxMembers() > 1)
					kisk.getController().onDialogRequest(player);
				else
					KiskService.onBind(kisk, player);
			}
		}, 10000));
	}
}
