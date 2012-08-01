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
import gameserver.ai2.AI2Request;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.poll.AIAnswer;
import gameserver.ai2.poll.AIAnswers;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.KiskService;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Source
 */
@AIName("kisk")
public class KiskAI2 extends NpcAI2 {

	@Override
	public Kisk getOwner() {
		return (Kisk) super.getOwner();
	}

	@Override
	protected void handleAttack(Creature creature) {
		if (getLifeStats().isFullyRestoredHp())
			for (Player member : getOwner().getCurrentMemberList())
				PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_ATTACKED);
	}

	@Override
	protected void handleDied() {
		if (isAlreadyDead()) {
			PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.DIE, 0, 0));
			getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_DESTROYED);
		}

		super.handleDied();
	}

	@Override
	protected void handleDespawned() {
		KiskService.removeKisk(getOwner());
		if (!isAlreadyDead())
			getOwner().broadcastPacket(SM_SYSTEM_MESSAGE.STR_BINDSTONE_IS_REMOVED);

		super.handleDespawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getKisk() == getOwner()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_BINDSTONE_ALREADY_REGISTERED);
			return;
		}

		if (getOwner().canBind(player)) {
			AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_BIND_TO_KISK, 0, new AI2Request() {

				@Override
				public void acceptRequest(Creature requester, Player responder) {
					// Check again if it's full (If they waited to press OK)
					if (!getOwner().canBind(responder)) {
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
						return;
					}
					KiskService.onBind(getOwner(), responder);
				}
			});

		}
		else if (getOwner().getCurrentMemberCount() >= getOwner().getMaxMembers())
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_FULL);
		else
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_BINDSTONE_HAVE_NO_AUTHORITY);
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

}
