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

package gameserver.services;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.DuelResult;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.templates.zone.ZoneType;
import gameserver.network.aion.serverpackets.SM_DUEL;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skillengine.model.SkillTargetSlot;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;

/**
 * @author Simple, Sphinx, xTz
 */
public class DuelService {

	private static Logger log = LoggerFactory.getLogger(DuelService.class);
	private FastMap<Integer, Integer> duels;

	public static final DuelService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param duels
	 */
	private DuelService() {
		this.duels = new FastMap<Integer, Integer>().shared();
		log.info("DuelService started.");
	}

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester
	 *          the player who requested the duel
	 * @param responder
	 *          the player who respond to duel request
	 */
	public void onDuelRequest(Player requester, Player responder) {
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isInsideZoneType(ZoneType.PVP) || responder.isInsideZoneType(ZoneType.PVP)) {
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_PARTNER_INVALID(responder.getName()));
			return;
		}
		if (requester.isEnemy(responder) || isDueling(requester.getObjectId()) || isDueling(responder.getObjectId())) {
			PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
			return;
		}

		RequestResponseHandler rrh = new RequestResponseHandler(requester) {

			@Override
			public void denyRequest(Creature requester, Player responder) {
				rejectDuelRequest((Player) requester, responder);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				startDuel((Player) requester, responder);
			}
		};
		responder.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, rrh);
		PacketSendUtility.sendPacket(responder, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, 0, requester.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTED(requester.getName()));
	}

	/**
	 * Asks confirmation for the duel request
	 * 
	 * @param requester
	 *          the player whose the duel was requested
	 * @param responder
	 *          the player whose the duel was responded
	 */
	public void confirmDuelWith(Player requester, Player responder) {
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if (requester.isEnemy(responder))
			return;

		RequestResponseHandler rrh = new RequestResponseHandler(responder) {

			@Override
			public void denyRequest(Creature requester, Player responder) {
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				cancelDuelRequest(responder, (Player) requester);
			}
		};
		requester.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, rrh);
		PacketSendUtility.sendPacket(requester, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, 0, responder.getName()));
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_REQUEST_TO_PARTNER(responder.getName()));
	}

	/**
	 * Rejects the duel request
	 * 
	 * @param requester
	 *          the duel requester
	 * @param responder
	 *          the duel responder
	 */
	private void rejectDuelRequest(Player requester, Player responder) {
		log.debug("[Duel] Player " + responder.getName() + " rejected duel request from " + requester.getName());
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.STR_DUEL_HE_REJECT_DUEL(responder.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DUEL_REJECT_DUEL(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 * 
	 * @param target
	 *          the duel target
	 * @param requester
	 */
	private void cancelDuelRequest(Player owner, Player target) {
		log.debug("[Duel] Player " + owner.getName() + " cancelled his duel request with " + target.getName());
		PacketSendUtility.sendPacket(target, SM_SYSTEM_MESSAGE.STR_DUEL_REQUESTER_WITHDRAW_REQUEST(owner.getName()));
		PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_DUEL_WITHDRAW_REQUEST(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param requester
	 *          the player to start duel with
	 * @param responder
	 *          the other player
	 */
	private void startDuel(final Player requester, final Player responder) {
		PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		createDuel(requester.getObjectId(), responder.getObjectId());

		// Schedule for draw
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (isDueling(requester.getObjectId(), responder.getObjectId())) {
					PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, requester.getName()));
					PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_DRAW, responder.getName()));
					removeDuel(requester.getObjectId(), responder.getObjectId());
				}
			}
		}, 5 * 60 * 1000); // 5 minutes battle retail like
	}

	/**
	 * This method will make the selected player lose the duel
	 * 
	 * @param player
	 */
	public void loseDuel(Player player) {
		if (!isDueling(player.getObjectId()))
			return;
		int opponnentId = duels.get(player.getObjectId());
		removeDuel(player.getObjectId(), opponnentId);

		Player opponent = World.getInstance().findPlayer(opponnentId);

		if (opponent != null) {
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();

			PacketSendUtility.sendPacket(opponent, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, player.getName()));
			PacketSendUtility.sendPacket(player, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, opponent.getName()));
		}
		else {
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}

	}

	public void loseArenaDuel(Player player) {
		if (!isDueling(player.getObjectId()))
			return;

		/**
		 * all debuffs are removed from loser Stop casting or skill use
		 */
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
		player.getController().cancelCurrentSkill();

		int opponnentId = duels.get(player.getObjectId());
		Player opponent = World.getInstance().findPlayer(opponnentId);

		if (opponent != null) {
			/**
			 * all debuffs are removed from winner, but buffs will remain Stop casting or skill use
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			opponent.getController().cancelCurrentSkill();
		}
		else {
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}

		removeDuel(player.getObjectId(), opponnentId);
	}

	/**
	 * @param playerObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId) {
		return (duels.containsKey(playerObjId) && duels.containsValue(playerObjId));
	}

	/**
	 * @param playerObjId
	 * @param targetObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId, int targetObjId) {
		return duels.containsKey(playerObjId) && duels.get(playerObjId) == targetObjId;
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	public void createDuel(int requesterObjId, int responderObjId) {
		duels.put(requesterObjId, responderObjId);
		duels.put(responderObjId, requesterObjId);
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	private void removeDuel(int requesterObjId, int responderObjId) {
		duels.remove(requesterObjId);
		duels.remove(responderObjId);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DuelService instance = new DuelService();
	}
}
