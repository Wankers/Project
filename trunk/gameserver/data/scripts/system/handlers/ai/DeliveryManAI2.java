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
package ai;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;


/**
 * @author-Nemesiss-
 *
 */
@AIName("deliveryman")
public class DeliveryManAI2 extends PostboxAI2 {
	public static int EVENT_SET_CREATOR_ID = 1;

	private static int SERVICE_TIME = 5*60*1000;
	private static int SPAWN_ACTION_DELAY = 1500;

	private int creatorId = -1;

	@Override
	protected void handleSpawned() {
		ThreadPoolManager.getInstance().schedule(new DeleteDeliveryMan(), SERVICE_TIME);
		ThreadPoolManager.getInstance().schedule(new DeliveryManSpawnAction(), SPAWN_ACTION_DELAY);

		super.handleSpawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getObjectId() == creatorId && player.getMailbox().haveUnread())
			super.handleDialogStart(player);
	}

	@Override
	protected void handleCustomEvent(int eventId, Object... args) {
		if(eventId == EVENT_SET_CREATOR_ID)
			creatorId = (Integer) args[0];
	}

	private final class DeleteDeliveryMan implements Runnable {
		@Override
		public void run() {
			AI2Actions.deleteOwner(DeliveryManAI2.this);
		}
	}

	private final class DeliveryManSpawnAction implements Runnable {
		@Override
		public void run() {
			PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(390266, getOwner()));
			getOwner().setTarget(getOwner().getKnownList().getObject(creatorId));
		}
	}
}