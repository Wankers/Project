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

import gameserver.ai2.event.AIEventType;
import gameserver.ai2.poll.AIQuestion;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RewardType;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.common.service.PlayerTeamDistributionService;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.services.DialogService;
import gameserver.services.RespawnService;
import gameserver.services.SiegeService;
import gameserver.services.abyss.AbyssPointsService;
import gameserver.services.drop.DropRegistrationService;
import gameserver.services.drop.DropService;
import gameserver.skillengine.model.SkillTemplate;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.stats.StatFunctions;
import gameserver.world.zone.ZoneInstance;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29), Sarynth modified by Wakizashi
 */

public class NpcController extends CreatureController<Npc> {

	private static final Logger log = LoggerFactory.getLogger(NpcController.class);

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Creature)
			getOwner().getAggroList().remove((Creature) object);
		// TODO not see player ai event
	}

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		Npc owner = getOwner();
		if (object instanceof Creature) {
			owner.getAi2().onCreatureEvent(AIEventType.CREATURE_SEE, (Creature) object);
		}
		if (object instanceof Player) {
			// TODO see player ai event
			if (owner.getLifeStats().isAlreadyDead())
				DropService.getInstance().see((Player) object, owner);
		}
		else if (object instanceof Summon) {
			// TODO see summon ai event
		}
	}

	@Override
	public void onBeforeSpawn() {
		super.onBeforeSpawn();
		Npc owner = getOwner();

		// set state from npc templates
		if (owner.getObjectTemplate().getState() != 0)
			owner.setState(owner.getObjectTemplate().getState());
		else
			owner.setState(CreatureState.NPC_IDLE);

		owner.getLifeStats().setCurrentHpPercent(100);
		owner.getAi2().onGeneralEvent(AIEventType.RESPAWNED);

		if (owner.getSpawn().canFly()) {
			owner.setState(CreatureState.FLYING);
		}
	}

	@Override
	public void onAfterSpawn() {
		super.onAfterSpawn();
		getOwner().getAi2().onGeneralEvent(AIEventType.SPAWNED);
	}

	@Override
	public void onDespawn() {
		Npc owner = getOwner();
		DropService.getInstance().unregisterDrop(getOwner());
		owner.getAi2().onGeneralEvent(AIEventType.DESPAWNED);
		super.onDespawn();
	}

	@Override
	public void onDie(Creature lastAttacker) {
		super.onDie(lastAttacker);
		Npc owner = getOwner();
		if (owner.getSpawn().hasPool()) {
			owner.getSpawn().setUse(false);
		}
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()));
		if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD)) {
			this.doReward();
		}

		owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
		owner.getAi2().onGeneralEvent(AIEventType.DIED);

		if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY)) {
			addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
		}
		if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN)
			&& !SiegeService.getInstance().isSiegeNpcInActiveSiege(getOwner())) {
			scheduleRespawn();
		}
		else {
			if (!hasScheduledTask(TaskId.DECAY)) {
				onDelete();
			}
		}
	}

	@Override
	public void doReward() {
		AionObject winner = getOwner().getAggroList().getMostDamage();

		if (winner == null)
			return;

		if (winner instanceof PlayerAlliance) {
			PlayerTeamDistributionService.doReward((PlayerAlliance) winner, getOwner());
		}
		else if (winner instanceof PlayerGroup) {
			PlayerTeamDistributionService.doReward((PlayerGroup) winner, getOwner());
		}
		else if (((Player) winner).isInGroup2()) {
			PlayerTeamDistributionService.doReward(((Player) winner).getPlayerGroup2(), getOwner());
		}
		else {
			super.doReward();

			Player player = (Player) ((Creature) winner).getActingCreature();;

			long expReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());
			player.getCommonData().addExp(expReward, RewardType.HUNTING, this.getOwner().getObjectTemplate().getNameId());

			int currentDp = player.getCommonData().getDp();
			int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
			player.getCommonData().setDp(dpReward + currentDp);

			if (getOwner().isRewardAP())
				AbyssPointsService.addAp(player, StatFunctions.calculatePvEApGained(player, getOwner()));

			QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0, 0));
			DropRegistrationService.getInstance().registerDrop(getOwner(), player, player.getLevel(), null);
			// notify instance script
		}
	}

	@Override
	public Npc getOwner() {
		return (Npc) super.getOwner();
	}

	@Override
	public void onDialogRequest(Player player) {
		// notify npc dialog request observer
		player.getObserveController().notifyRequestDialogObservers(getOwner());

		getOwner().getAi2().onCreatureEvent(AIEventType.DIALOG_START, player);
	}

	@Override
	public void onDialogSelect(int dialogId, final Player player, int questId, int extendedRewardIndex) {
		if (!MathUtil.isInRange(getOwner(), player, getOwner().getObjectTemplate().getTalkDistance() + 2)) {
			return;
		}
		if (!getOwner().getAi2().onDialogSelect(player, dialogId, questId)) {
			DialogService.onDialogSelect(dialogId, player, getOwner(), questId, extendedRewardIndex);
		}
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;
		final Creature actingCreature;
		
		//summon should gain its own aggro
		if (creature instanceof Summon)
			actingCreature = creature;
		else
			actingCreature = creature.getActingCreature();

		super.onAttack(actingCreature, skillId, type, damage, notifyAttack, log);

		Npc npc = getOwner();

		if (actingCreature instanceof Player) {
			QuestEngine.getInstance().onAttack(new QuestEnv(npc, (Player) actingCreature, 0, 0));
		}

		PacketSendUtility.broadcastPacket(npc, new SM_ATTACK_STATUS(npc, type, skillId, damage, log));
	}

	@Override
	public void onStopMove() {
		getOwner().getMoveController().setInMove(false);
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		super.onStartMove();
	}

	@Override
	public void onEnterZone(ZoneInstance zoneInstance) {
		Npc npc = getOwner();
		Player player = npc.getQuestPlayer();
		if (zoneInstance.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zoneInstance.getAreaTemplate().getWorldId());
		}
		else {
			QuestEngine.getInstance().onEnterZone(new QuestEnv(npc, player, 0, 0),
				zoneInstance.getAreaTemplate().getZoneName());
		}
	}

	/**
	 * Schedule respawn of npc In instances - no npc respawn
	 */
	public void scheduleRespawn() {
		if (!getOwner().getSpawn().isNoRespawn()) {
			RespawnService.scheduleRespawnTask(getOwner());
		}
	}
	
	public void onCreatureDie(Creature creature)
	{
		super.onDie(creature);
	}

	public final float getAttackDistanceToTarget() {
		return getOwner().getGameStats().getAttackRange().getCurrent() / 1000f;
	}

	@Override
	public boolean useSkill(int skillId, int skillLevel) {
		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (!getOwner().isSkillDisabled(skillTemplate)) {
			return super.useSkill(skillId, skillLevel);
		}
		return false;
	}

}
