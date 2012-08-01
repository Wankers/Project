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

import java.util.Collections;
import java.util.concurrent.Future;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.GSConfig;
import gameserver.configs.main.HTMLConfig;
import gameserver.configs.main.MembershipConfig;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Pet;
import gameserver.model.gameobjects.StaticObject;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.AbyssRank;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.gameobjects.state.CreatureVisualState;
import gameserver.model.skill.PlayerSkillEntry;
import gameserver.model.stats.container.PlayerGameStats;
import gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import gameserver.model.templates.flypath.FlyPathEntry;
import gameserver.model.templates.quest.QuestItems;
import gameserver.model.templates.stats.PlayerStatsTemplate;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.network.aion.serverpackets.SM_DELETE;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import gameserver.network.aion.serverpackets.SM_MOTION;
import gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import gameserver.network.aion.serverpackets.SM_NPC_INFO;
import gameserver.network.aion.serverpackets.SM_PET;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import gameserver.network.aion.serverpackets.SM_SUMMON_PANEL;
import gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.restrictions.RestrictionsManager;
import gameserver.services.ClassChangeService;
import gameserver.services.CraftSkillUpdateService;
import gameserver.services.DuelService;
import gameserver.services.HTMLService;
import gameserver.services.LegionService;
import gameserver.services.PvpService;
import gameserver.services.QuestService;
import gameserver.services.SkillLearnService;
import gameserver.services.abyss.AbyssService;
import gameserver.services.instance.InstanceService;
import gameserver.services.item.ItemService;
import gameserver.skillengine.SkillEngine;
import gameserver.skillengine.model.*;
import gameserver.skillengine.model.Skill.SkillMethod;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import gameserver.taskmanager.tasks.TeamEffectUpdater;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.audit.AuditLogger;
import gameserver.world.MapRegion;
import gameserver.world.WorldType;
import gameserver.world.geo.GeoService;
import gameserver.world.zone.ZoneInstance;
import gameserver.world.zone.ZoneName;

/**
 * This class is for controlling players.
 * 
 * @author -Nemesiss-, ATracer, xavier, Sarynth, RotO, xTz, KID modified by Sippolo
 */
public class PlayerController extends CreatureController<Player> {

	private Logger log = LoggerFactory.getLogger(PlayerController.class);
	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		if (object instanceof Player) {
			Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isEnemy(player)));
			PacketSendUtility.sendPacket(getOwner(), new SM_MOTION(player.getObjectId(), player.getMotions()
				.getActiveMotions()));
			if (player.getPet() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug(
					"Player " + getOwner().getName() + " sees " + object.getName() + " that has toypet");
				PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getPet()));
			}
			player.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Kisk) {
			Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace())
				PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
		}
		else if (object instanceof Npc) {
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			if (!npc.getEffectController().isEmpty())
				npc.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Summon) {
			Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc));
			if (!npc.getEffectController().isEmpty())
				npc.getEffectController().sendEffectIconsTo(getOwner());
		}
		else if (object instanceof Gatherable || object instanceof StaticObject) {
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		}
		else if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, (Pet) object));
		}
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(4, (Pet) object));
		}
		else {
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 15));
		}
	}

	public void updateNearbyQuests() {
		FastMap<Integer, Integer> nearbyQuestList = FastMap.newInstance();
                int level;
                
                
		for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds()) {
			if (QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0))) {
				if (CustomConfig.ENABLE_SIMPLE_2NDCLASS && !getOwner().getCommonData().getPlayerClass().isStartingClass()) {
                                    level = 9;
                                }else{
                                    level = getOwner().getCommonData().getLevel();
                                }
                            if (!nearbyQuestList.containsKey(questId)) {
					nearbyQuestList.put(questId,
						QuestService.checkLevelRequirement(questId, level) ? 0 : 2);
				}
			}
		}
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(nearbyQuestList));
	}

	@Override
	public void onEnterZone(ZoneInstance zone) {
		Player player = getOwner();
		InstanceService.onEnterZone(player, zone);
		if (zone.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zone.getAreaTemplate().getWorldId());
		}
		else {
			QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zone.getAreaTemplate().getZoneName());
		}
	}

	@Override
	public void onLeaveZone(ZoneInstance zone) {
		Player player = getOwner();
		InstanceService.onLeaveZone(player, zone);
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}

	/**
	 * {@inheritDoc} Should only be triggered from one place (life stats)
	 */
	//TODO [AT] move
	public void onEnterWorld() {

		InstanceService.onEnterInstance(getOwner());
		if (getOwner().getPosition().getWorldMapInstance().getParent().isExceptBuff()) {
			getOwner().getEffectController().removeAllEffects();
		}
		// remove abyss transformation if worldtype != abyss && worldtype != balaurea
		if (getOwner().getWorldType() != WorldType.ABYSS && getOwner().getWorldType() != WorldType.BALAUREA
			|| getOwner().isInInstance()) {
			for (Effect ef : getOwner().getEffectController().getAbnormalEffects()) {
				if (ef.isAvatar()) {
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			}
		}
	}

	//TODO [AT] move
	public void onLeaveWorld() {
		InstanceService.onLeaveInstance(getOwner());
	}

	public void onDie(Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		player.getController().cancelCurrentSkill();
		boolean hasSelfRezEffect = getOwner().haveSelfRezEffect();
		player.setRebirthRevive(hasSelfRezEffect);
		Creature master = null;
		if (lastAttacker != null)
			master = lastAttacker.getMaster();

		if (master instanceof Player) {
			Player killer = (Player) master;
			// High ranked kill announce
			AbyssRank ar = player.getAbyssRank();
			if (AbyssService.isOnPvpMap(killer) && ar != null) {
				if (ar.getRank().getId() >= 10)
					AbyssService.rankedKillAnnounce(player);
			}
		}

		if (DuelService.getInstance().isDueling(player.getObjectId())) {
			if (master != null && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId())) {
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHp(player.getLifeStats().getMaxHp() / 3);
				return;
			}
			DuelService.getInstance().loseDuel(player);
		}

		if (player.isInInstance()) {
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker)) {
				super.onDie(lastAttacker);
				return;
			}
		}

		MapRegion mapRegion = player.getPosition().getMapRegion();
		if (mapRegion != null && mapRegion.onDie(lastAttacker, getOwner())) {
			return;
		}

		this.doReward();

		if (master instanceof Npc || master == player) {
			if (player.getLevel() > 4)
				player.getCommonData().calculateExpLoss();
		}

		/**
		 * Release summon
		 */
		Summon summon = player.getSummon();
		if (summon != null)
			summon.getController().release(UnsummonType.UNSPECIFIED);

		//setIsFlyingBeforeDead for PlayerReviveService
		if (player.isInState(CreatureState.FLYING)) {
			player.setIsFlyingBeforeDeath(true);
		}

		//unsetflying
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);

		//unset active
		player.unsetState(CreatureState.ACTIVE);

		// Effects removed with super.onDie()
		super.onDie(lastAttacker);

		//send sm_emotion with DIE
		//have to be send after state is updated!
		sendDieFromCreature(lastAttacker, showPacket, hasSelfRezEffect);

		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
            
		if (player.isInGroup2()) {
			player.getPlayerGroup2().sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()),
				new ExcludePlayerFilter(player));
		}
	}

	@Override
	public void onDie(Creature lastAttacker) {
		this.onDie(lastAttacker, true);
	}

	public void sendDie() {
		sendDieFromCreature(null);
	}

	private void sendDieFromCreature(Creature lastAttacker) {
		sendDieFromCreature(lastAttacker, true, false);
	}

	private void sendDieFromCreature(Creature lastAttacker, boolean showPacket, boolean hasSelfRezEffect) {
		Player player = this.getOwner();

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()), true);
		if (showPacket) {
			int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
			PacketSendUtility.sendPacket(player, new SM_DIE(hasSelfRezEffect, player.haveSelfRezItem(), kiskTimeRemaining, 0));
		}
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_DEATH);
	}

	@Override
	public void doReward() {
		Player victim = getOwner();

		switch (victim.getWorldId()) {
			case 300210000:
			case 300110000:
				return;
		}
		PvpService.getInstance().doReward(victim);

		// DP reward
		// TODO: Figure out what DP reward should be for PvP
		// int currentDp = winner.getCommonData().getDp();
		// int dpReward = StatFunctions.calculateSoloDPReward(winner, getOwner());
		// winner.getCommonData().setDp(dpReward + currentDp);
	}

	@Override
	public void onBeforeSpawn() {
		super.onBeforeSpawn();
		startProtectionActiveTask();
		if (getOwner().getIsFlyingBeforeDeath())
			getOwner().unsetState(CreatureState.FLOATING_CORPSE);
		else
			getOwner().unsetState(CreatureState.DEAD);
		getOwner().setState(CreatureState.ACTIVE);
	}

	@Override
	public void attackTarget(Creature target, int time) {

		PlayerGameStats gameStats = getOwner().getGameStats();

		if (!RestrictionsManager.canAttack(getOwner(), target))
			return;

		// Normal attack is already limited client side (ex. Press C and attacker approaches target)
		// but need a check server side too also for Z axis issue

		if (!MathUtil.isInAttackRange(getOwner(), target,
			(float) (getOwner().getGameStats().getAttackRange().getCurrent() / 1000f) + 1))
			return;

		if (!GeoService.getInstance().canSee(getOwner(), target)) {
			PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.STR_ATTACK_OBSTACLE_EXIST);
			return;
		}

		int attackSpeed = gameStats.getAttackSpeed().getCurrent();

		long milis = System.currentTimeMillis();
		// network ping..
		if (milis - lastAttackMilis + 300 < attackSpeed) {
			// hack
			return;
		}
		lastAttackMilis = milis;

		/**
		 * notify attack observers
		 */
		super.attackTarget(target, time);

	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;

		if (getOwner().isInvul() || getOwner().isProtectionActive())
			damage = 0;

		cancelUseItem();
		cancelGathering();
		cancelActionItemNpc();
		cancelPortalUseItem();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);

		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage, log), true);

		lastAttackedMilis = System.currentTimeMillis();
	}

	/**
	 * @param skillId
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time) {
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}

	/**
	 * @param template
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 * @param clientHitTime
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime) {
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
			QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, template.getSkillId());
		}
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}

	@Override
	public void onStopMove() {
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		cancelPortalUseItem();
		super.onStartMove();
	}

	@Override
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}

		Player player = getOwner();
		Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_CANCEL(player, castingSkill.getSkillTemplate().getSkillId()));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED);
		}
		else if (castingSkill.getSkillMethod() == SkillMethod.ITEM) {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getDelayId());
			PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill
				.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(),
				castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0));
		}
	}

	@Override
	public void cancelUseItem() {
		Player player = getOwner();
		Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE)) {
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usingItem == null ? 0
				: usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0));
		}
	}

	@Override
	public void cancelActionItemNpc() {
		Player player = getOwner();
		if (hasTask(TaskId.ACTION_ITEM_NPC)) {
			cancelTask(TaskId.ACTION_ITEM_NPC);
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getOwner()
				.getObjectId()), true);
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(), player
				.getActionItemNpc().getTalkDelay(), player.getActionItemNpc().getEndCondition()));
		}
	}

	@Override
	public void cancelPortalUseItem() {
		Player player = getOwner();
		if (hasTask(TaskId.PORTAL)) {
			cancelTask(TaskId.PORTAL);
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getOwner().getObjectId(), 0, 0));
		}
	}

	public void cancelGathering() {
		Player player = getOwner();
		if (player.getTarget() instanceof Gatherable) {
			Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}

	public void updatePassiveStats(){
		Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills()) {
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if (skill != null && skill.isPassive()) {
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HealType healType, int value) {
		super.onRestore(healType, value);
		switch (healType) {
			case DP:
				getOwner().getCommonData().addDp(value);
				break;
		}
	}

	/**
	 * @param player
	 * @return
	 */
	//TODO [AT] move to Player
	public boolean isDueling(Player player) {
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}

	//TODO [AT] rename or remove
	public boolean isInShutdownProgress() {
		return isInShutdownProgress;
	}

//TODO [AT] rename or remove
	public void setInShutdownProgress(boolean isInShutdownProgress) {
		this.isInShutdownProgress = isInShutdownProgress;
	}

	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
		switch (dialogId) {
			case 2:
				PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore(), player));
				break;
		}
	}

	public void upgradePlayer() {
		Player player = getOwner();
		byte level = player.getLevel();
                
		PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);

		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();

		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);

		// Guides Html on level up
		if (HTMLConfig.ENABLE_GUIDES)
			HTMLService.sendGuideHtml(player);

		// Temporal
		ClassChangeService.showClassChangeDialog(player);

		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		updateNearbyQuests();

		// add new skills
		SkillLearnService.addNewSkills(player);

		if (CustomConfig.ADVSTIGMA_ONLVLUP) {
			switch (level) {
				case 45:
					if (player.getCommonData().getAdvancedStigmaSlotSize() != 2)
						player.getCommonData().setAdvancedStigmaSlotSize(2);
					break;
				case 50:
					if (player.getCommonData().getAdvancedStigmaSlotSize() != 3)
						player.getCommonData().setAdvancedStigmaSlotSize(3);
					break;
				case 52:
					if (player.getCommonData().getAdvancedStigmaSlotSize() != 4)
						player.getCommonData().setAdvancedStigmaSlotSize(4);
					break;
				case 55:
					if (player.getCommonData().getAdvancedStigmaSlotSize() != 5)
						player.getCommonData().setAdvancedStigmaSlotSize(5);
					break;
				case 60:
					if (player.getCommonData().getAdvancedStigmaSlotSize() != 6)
						player.getCommonData().setAdvancedStigmaSlotSize(6);
					break;
			}
		}

		// TODO M4 improve here performance
		updatePassiveStats();

		// add recipe for morph
		if (level == 10)
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);

		if (player.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(player);
		}
		if (player.isLegionMember())
			LegionService.getInstance().updateMemberInfo(player);
		player.getNpcFactions().onLevelUp();
	}

	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an
	 * action char stops blinking. - Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask() {
		if (!getOwner().isProtectionActive()) {
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					stopProtectionActiveTask();
				}

			}, 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}

	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask() {
		cancelTask(TaskId.PROTECTION_ACTIVE);
		Player player = getOwner();
		if (player != null && player.isSpawned()) {
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			notifyAIOnMove();
		}
	}

	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd() {
		Player player = getOwner();
		player.unsetState(CreatureState.FLIGHT_TELEPORT);
		player.setFlightTeleportId(0);

		if (GSConfig.ENABLE_FLYPATH_VALIDATOR) {
			long diff = (System.currentTimeMillis() - player.getFlyStartTime());
			FlyPathEntry path = player.getCurrentFlyPath();

			if (path == null) {
				AuditLogger.info(player, "Try to use null flyPath #" + player.getFlightTeleportId());
			}

			if (player.getWorldId() != path.getEndWorldId()) {
				AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world "
					+ player.getWorldId() + ". expected " + path.getEndWorldId());
			}

			if (diff < path.getTimeInMs()) {
				AuditLogger.info(player,
					"Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
				/*
				 * todo if works teleport player to start_* xyz, or even ban
				 */
			}
		}
		player.setCurrentFlypath(null);
		player.setFlightDistance(0);
		player.setState(CreatureState.ACTIVE);
		updateZone();
	}

	@Override
	public void createSummon(int npcId, int skillId, int skillLevel) {
		Player master = getOwner();
		if (master.getSummon() != null) {
			master.getSummon().getController().delete();
			master.getSummon().setMaster(null);
			master.setSummon(null);
		}
		Summon summon = VisibleObjectSpawner.spawnSummon(master, npcId, skillId, skillLevel);
		master.setSummon(summon);
		PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL(summon));
		PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2));
		PacketSendUtility.broadcastPacket(summon, new SM_SUMMON_UPDATE(summon));
	}

	public boolean addItems(int itemId, int count) {
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}

	public void startStance(final int skillId) {
		stance = skillId;
	}

	public void stopStance() {
		getOwner().getEffectController().removeEffect(stance);
		PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_STANCE(getOwner(), 0));
		stance = 0;
	}

	public int getStanceSkillId() {
		return stance;
	}

	public boolean isUnderStance() {
		return stance != 0;
	}

	public void updateSoulSickness() {
		Player player = getOwner();
		if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS)) {
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10) {
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			}
			SkillEngine.getInstance().getSkill(player, 8291, deathCount, player).useSkill();
		}
	}

	/**
	 * Player is considered in combat if he's been attacked or has attacked less or equal 10s before
	 * 
	 * @return true if the player is actively in combat
	 */
	public boolean isInCombat() {
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}
}
