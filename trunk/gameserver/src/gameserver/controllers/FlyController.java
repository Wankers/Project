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
package gameserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.EmotionType;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class FlyController {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FlyController.class);

	private static final long FLY_REUSE_TIME = 10000;
	private Player player;

	public FlyController(Player player) {
		this.player = player;
	}

	/**
	 * 
	 */
	public void onStopGliding(boolean removeWings) {
		if (player.isInState(CreatureState.GLIDING)) {
			player.unsetState(CreatureState.GLIDING);
			
			if (player.isInState(CreatureState.FLYING)) {
				player.setFlyState(1);
			}
			else {
				player.setFlyState(0);
				player.getLifeStats().triggerFpRestore();
				if (removeWings)
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);
			}

			player.getGameStats().updateStatsAndSpeedVisually();
		}
	}

	/**
	 * Ends flying 1) by CM_EMOTION (pageDown or fly button press) 2) from server side during teleportation (abyss gates
	 * should not break flying) 3) when FP is decreased to 0
	 */
	public void endFly() {
		// unset flying and gliding
		if (player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);
			player.unsetState(CreatureState.FLYING);
			player.unsetState(CreatureState.GLIDING);
			player.setFlyState(0);

			// this is probably needed to change back fly speed into speed.
			//TODO remove this and just send in update?
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
			player.getGameStats().updateStatsAndSpeedVisually();
			
			player.getLifeStats().triggerFpRestore();
		}
	}

	/**
	 * This method is called to start flying (called by CM_EMOTION when pageUp or pressed fly button)
	 */
	public void startFly() {
		if (player.getFlyReuseTime() > System.currentTimeMillis()){
			AuditLogger.info(player, "No Flight Cooldown Hack. Reuse time: "+((player.getFlyReuseTime()-System.currentTimeMillis())/1000));
			return;
		}
		player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
		player.setState(CreatureState.FLYING);
		player.setFlyState(1);
		player.getLifeStats().triggerFpReduce();
		//TODO remove it?
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
		player.getGameStats().updateStatsAndSpeedVisually();
	}

	/**
	 * Switching to glide mode (called by CM_MOVE with VALIDATE_GLIDE movement type) 1) from standing state 2) from flying
	 * state If from stand to glide - start fp reduce + emotions/stats if from fly to glide - only emotions/stats
	 */
	public boolean switchToGliding() {
		if (!player.isInState(CreatureState.GLIDING)) {
			if (player.getFlyReuseTime() > System.currentTimeMillis()){
				return false;
			}
			player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
			player.setState(CreatureState.GLIDING);
			if (player.getFlyState() == 0)
				player.getLifeStats().triggerFpReduce();
			player.setFlyState(2);

			player.getGameStats().updateStatsAndSpeedVisually();
		}
		return true;
	}
}