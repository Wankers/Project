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
package gameserver.questEngine.handlers;

import java.util.List;

import gameserver.model.gameobjects.Item;
import gameserver.model.templates.quest.QuestItems;
import gameserver.model.templates.rewards.BonusType;
import gameserver.questEngine.model.QuestActionType;
import gameserver.questEngine.model.QuestEnv;
import gameserver.world.zone.ZoneName;

/**
 * The methods will be overridden in concrete quest handlers
 * 
 * @author vlog
 */
public abstract class AbstractQuestHandler {

	public abstract void register();

	public boolean onDialogEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onEnterWorldEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onEnterZoneEvent(QuestEnv questEnv, ZoneName zoneName) {
		return false;
	}

	public boolean onLeaveZoneEvent(QuestEnv questEnv, ZoneName zoneName) {
		return false;
	}

	public HandlerResult onItemUseEvent(QuestEnv questEnv, Item item) {
		return HandlerResult.UNKNOWN;
	}

	public boolean onGetItemEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onUseSkillEvent(QuestEnv questEnv, int skillId) {
		return false;
	}

	public boolean onKillEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onAttackEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onLvlUpEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return false;
	}

	public boolean onDieEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onLogOutEvent(QuestEnv env) {
		return false;
	}

	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return false;
	}

	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return false;
	}

	public boolean onMovieEndEvent(QuestEnv questEnv, int movieId) {
		return false;
	}

	public boolean onQuestTimerEndEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onPassFlyingRingEvent(QuestEnv questEnv, String flyingRing) {
		return false;
	}

	public boolean onKillRankedEvent(QuestEnv env) {
		return false;
	}

	public boolean onKillInWorldEvent(QuestEnv env) {
		return false;
	}

	public boolean onFailCraftEvent(QuestEnv env, int itemId) {
		return false;
	}

	public boolean onEquipItemEvent(QuestEnv env, int itemId) {
		return false;
	}
	
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
		return false;
	}
	
	public boolean onDredgionRewardEvent(QuestEnv env) {
		return false;
	}
	
	public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems) {
		return HandlerResult.UNKNOWN;
	}
}
