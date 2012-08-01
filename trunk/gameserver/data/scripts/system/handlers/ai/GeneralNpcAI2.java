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

import gameserver.ai2.AIName;
import gameserver.ai2.AttackIntention;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.event.AIEventType;
import gameserver.ai2.handler.AggroEventHandler;
import gameserver.ai2.handler.AttackEventHandler;
import gameserver.ai2.handler.DiedEventHandler;
import gameserver.ai2.handler.MoveEventHandler;
import gameserver.ai2.handler.ReturningEventHandler;
import gameserver.ai2.handler.TalkEventHandler;
import gameserver.ai2.handler.TargetEventHandler;
import gameserver.ai2.handler.ThinkEventHandler;
import gameserver.ai2.manager.SkillAttackManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.skill.NpcSkillEntry;

/**
 * @author ATracer
 */
@AIName("general")
public class GeneralNpcAI2 extends NpcAI2 {

	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}

	@Override
	protected void handleDied() {
		DiedEventHandler.onDie(this);
	}

	@Override
	protected void handleAttack(Creature creature) {
		AttackEventHandler.onAttack(this, creature);
	}

	@Override
	protected void handleCreatureAttacked(Creature creature) {
		AggroEventHandler.onCreatureAttacked(this, creature);
	}

	@Override
	protected void handleDialogStart(Player player) {
		TalkEventHandler.onTalk(this, player);
	}

	@Override
	protected void handleDialogFinish(Player creature) {
		TalkEventHandler.onFinishTalk(this, creature);
	}

	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
	}

	@Override
	protected void handleAttackComplete() {
		AttackEventHandler.onAttackComplete(this);
	}

	@Override
	protected void handleTargetReached() {
		TargetEventHandler.onTargetReached(this);
	}

	@Override
	protected void handleNotAtHome() {
		ReturningEventHandler.onNotAtHome(this);
	}

	@Override
	protected void handleBackHome() {
		ReturningEventHandler.onBackHome(this);
	}

	@Override
	protected void handleTargetTooFar() {
		TargetEventHandler.onTargetTooFar(this);
	}

	@Override
	protected void handleTargetGiveup() {
		TargetEventHandler.onTargetGiveup(this);
	}

	@Override
	protected void handleTargetChanged(Creature creature) {
		super.handleTargetChanged(creature);
		TargetEventHandler.onTargetChange(this, creature);
	}

	@Override
	protected void handleMoveValidate() {
		MoveEventHandler.onMoveValidate(this);
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		MoveEventHandler.onMoveArrived(this);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
	}

	@Override
	public AttackIntention chooseAttackIntention() {
		VisibleObject currentTarget = getTarget();
		Creature mostHated = getAggroList().getMostHated();

		if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}

		if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		}

		NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
		if (skill != null) {
			skillId = skill.getSkillId();
			skillLevel = skill.getSkillLevel();
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
}
