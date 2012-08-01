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
package gameserver.questEngine.task;

import gameserver.ai2.event.AIEventType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
public class FollowingNpcCheckTask implements Runnable {

	private final QuestEnv env;
	private final DestinationChecker destinationChecker;

	/**
	 * @param player
	 * @param npc
	 * @param destinationChecker
	 */
	FollowingNpcCheckTask(QuestEnv env, DestinationChecker destinationChecker) {
		this.env = env;
		this.destinationChecker = destinationChecker;
	}

	@Override
	public void run() {
		final Player player = env.getPlayer();
		final Npc npc = (Npc) env.getVisibleObject();
		if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead()) {
			onFail(env);
		}
		if (!MathUtil.isIn3dRange(player, npc, 20)) {
			onFail(env);
		}

		if (destinationChecker.check()) {
			onSuccess(env);
		}
	}

	/**
	 * Following task succeeded, proceed with quest
	 */
	private final void onSuccess(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcReachTarget(env);
	}

	/**
	 * Following task failed, abort further progress
	 */
	protected void onFail(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcLostTarget(env);
	}

	private final void stopFollowing(QuestEnv env) {
		Player player = env.getPlayer();
		Npc npc = (Npc) env.getVisibleObject();
		player.getController().cancelTask(TaskId.QUEST_FOLLOW);
		npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
	}
}

abstract class DestinationChecker {

	abstract boolean check();
}

final class TargetDestinationChecker extends DestinationChecker {

	private final Creature follower;
	private final Creature target;

	/**
	 * @param follower
	 * @param target
	 */
	TargetDestinationChecker(Creature follower, Creature target) {
		this.follower = follower;
		this.target = target;
	}

	@Override
	boolean check() {
		return MathUtil.isIn3dRange(target, follower, 3);
	}

}

final class CoordinateDestinationChecker extends DestinationChecker {

	private final Creature follower;
	private final float x;
	private final float y;
	private final float z;

	/**
	 * @param follower
	 * @param x
	 * @param y
	 * @param z
	 */
	CoordinateDestinationChecker(Creature follower, float x, float y, float z) {
		this.follower = follower;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	boolean check() {
		return MathUtil.isNearCoordinates(follower, x, y, z, 3);
	}

}
