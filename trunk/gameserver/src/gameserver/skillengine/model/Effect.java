/*
 * This file is part of aion-unique <aion-unique.com>.
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
package gameserver.skillengine.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javolution.util.FastMap;

import commons.utils.Rnd;
import gameserver.controllers.attack.AttackStatus;
import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.AttackCalcObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.stats.calc.StatOwner;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import gameserver.network.aion.serverpackets.SM_SKILL_ACTIVATION;
import gameserver.skillengine.condition.Conditions;
import gameserver.skillengine.effect.DamageEffect;
import gameserver.skillengine.effect.DelayedSpellAttackInstantEffect;
import gameserver.skillengine.effect.EffectTemplate;
import gameserver.skillengine.effect.Effects;
import gameserver.skillengine.effect.FearEffect;
import gameserver.skillengine.effect.TransformEffect;
import gameserver.skillengine.periodicaction.PeriodicAction;
import gameserver.skillengine.periodicaction.PeriodicActions;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * @modified by Wakizashi
 * @modified by Sippolo
 * @modified by kecimis
 */
public class Effect implements StatOwner {

	private Skill skill;
	private SkillTemplate skillTemplate;
	private int skillLevel;
	private int duration;
	private long endTime;
	private PeriodicActions periodicActions;
	private SkillMoveType skillMoveType = SkillMoveType.DEFAULT;
	private Creature effected;
	private Creature effector;
	private Future<?> checkTask = null;
	private Future<?> task = null;
	private Future<?>[] periodicTasks = null;
	private Future<?> periodicActionsTask = null;

	private float targetX = 0;
	private float targetY = 0;
	private float targetZ = 0;
	/**
	 * Used for damage/heal values
	 */
	private int reserved1;
	/**
	 * Used for shield total hit damage;
	 */
	private int reserved2;
	/**
	 * Used for shield hit damage
	 */
	private int reserved3;
	/**
	 * Used for tick heals from HoT's (Heal Over Time)
	 */
	private int reserved4;
	/**
	 * Used for tick damages from DoT's (Damage Over Time)
	 */
	private int reserved5;

	private int[] reservedInts;

	/**
	 * Spell Status 1 : stumble 2 : knockback 4 : open aerial 8 : close aerial 16 : spin 32 : block 64 : parry 128 : dodge
	 * 256 : resist
	 */
	private SpellStatus spellStatus = SpellStatus.NONE;
	private DashStatus dashStatus = DashStatus.NONE;

	private AttackStatus attackStatus = AttackStatus.NORMALHIT;
	
	/**
	 * shield effects related
	 */
	private int shieldDefense;
	private int reflectedDamage = 0;
	private int reflectedSkillId = 0;
	private int protectedSkillId = 0;
	private int protectedDamage = 0;
	private int protectorId = 0;

	private boolean addedToController;
	private AttackCalcObserver[] attackStatusObserver;

	private AttackCalcObserver[] attackShieldObserver;

	private boolean launchSubEffect = true;
	private Effect subEffect;

	private boolean isStopped;

	private boolean isDelayedDamage;
	
	private boolean isDamageEffect;

	private boolean isXpBoost;

	private boolean isCancelOnDmg;

	private float criticalRatio = 1.0f;

	private boolean subEffectAbortedBySubConditions;

	private ItemTemplate itemTemplate;

	/**
	 * Hate that will be placed on effected list
	 */
	private int tauntHate;
	/**
	 * Total hate that will be broadcasted
	 */
	private int effectHate;

	private Map<Integer,EffectTemplate> sucessEffects = new FastMap<Integer,EffectTemplate>().shared();

	private int carvedSignet = 0;
	
	private int signetBurstedCount = 0;
	
	protected int abnormals;

	/**
	 * Action observer that should be removed after effect end
	 */
	private ActionObserver[] actionObserver;

	float x, y, z;
	int worldId, instanceId;
	
	/**
	 * used to force duration, you should be very careful when to use it
	 */
	private boolean forcedDuration = false;
	
	/**
	 * power of effect ( used for dispels)
	 */
	private int power = 10;
	private boolean isAura;

	
	public final Skill getSkill() {
		return skill;
	}

	public void setAbnormal(int mask) {
		abnormals |= mask;
	}

	public int getAbnormals() {
		return abnormals;
	}

	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration) {
		this.effector = effector;
		this.effected = effected;
		this.skillTemplate = skillTemplate;
		this.skillLevel = skillLevel;
		this.duration = duration;
		this.periodicActions = skillTemplate.getPeriodicActions();
		
		this.power = initializePower(skillTemplate.getSkillId());
	}

	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration,
		ItemTemplate itemTemplate) {
		this(effector, effected, skillTemplate, skillLevel, duration);
		this.itemTemplate = itemTemplate;
	}

	public Effect(Skill skill, Creature effected, int duration, ItemTemplate itemTemplate) {
		this(skill.getEffector(), effected, skill.getSkillTemplate(), skill.getSkillLevel(), duration, itemTemplate);
		this.skill = skill;
	}

	public void setWorldPosition(int worldId, int instanceId, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldId = worldId;
		this.instanceId = instanceId;
	}

	public int getEffectorId() {
		return effector.getObjectId();
	}

	public int getSkillId() {
		return skillTemplate.getSkillId();
	}

	public String getSkillName() {
		return skillTemplate.getName();
	}

	public final SkillTemplate getSkillTemplate() {
		return skillTemplate;
	}

	public SkillSubType getSkillSubType() {
		return skillTemplate.getSubType();
	}

	public int getSkillSetException() {
		return skillTemplate.getSkillSetException();
	}

	public int getSkillSetMaxOccur() {
		return skillTemplate.getSkillSetMaxOccur();
	}

	public String getStack() {
		return skillTemplate.getStack();
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public int getSkillStackLvl() {
		return skillTemplate.getLvl();
	}

	public SkillType getSkillType() {
		return skillTemplate.getType();
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int newDuration) {
		this.duration = newDuration;
	}

	public Creature getEffected() {
		return effected;
	}

	public Creature getEffector() {
		return effector;
	}

	public boolean isPassive() {
		return skillTemplate.isPassive();
	}

	public void setTask(Future<?> task) {
		this.task = task;
	}

	public Future<?> getPeriodicTask(int i) {
		return periodicTasks[i - 1];
	}

	public void setPeriodicTask(Future<?> periodicTask, int i) {
		if (periodicTasks == null)
			periodicTasks = new Future<?>[4];
		this.periodicTasks[i - 1] = periodicTask;
	}

	public int getReserved1() {
		return reserved1;
	}

	public void setReserved1(int reserved1) {
		this.reserved1 = reserved1;
	}

	public int getReserved2() {
		return reserved2;
	}

	public void setReserved2(int reserved2) {
		this.reserved2 = reserved2;
	}

	public int getReserved3() {
		return reserved3;
	}

	public void setReserved3(int reserved3) {
		this.reserved3 = reserved3;
	}

	public int getReserved4() {
		return reserved4;
	}

	public void setReserved4(int reserved4) {
		this.reserved4 = reserved4;
	}

	public int getReserved5() {
		return reserved5;
	}

	public void setReserved5(int reserved5) {
		this.reserved5 = reserved5;
	}

	public AttackStatus getAttackStatus() {
		return attackStatus;
	}

	public void setAttackStatus(AttackStatus attackStatus) {
		this.attackStatus = attackStatus;
	}

	public List<EffectTemplate> getEffectTemplates() {
		return skillTemplate.getEffects().getEffects();
	}

	public boolean isMphealInstant() {
		Effects effects = skillTemplate.getEffects();
		return effects != null && effects.isMpHealInstant();
	}

	public boolean isToggle() {
		return skillTemplate.getActivationAttribute() == ActivationAttribute.TOGGLE;
	}

	public int getTargetSlot() {
		return skillTemplate.getTargetSlot().ordinal();
	}

	public SkillTargetSlot getTargetSlotEnum() {
		return skillTemplate.getTargetSlot();
	}

	public int getTargetSlotLevel() {
		return skillTemplate.getTargetSlotLevel();
	}

	public DispelCategoryType getDispelCategory() {
		return skillTemplate.getDispelCategory();
	}
	public int getReqDispelLevel() {
		return skillTemplate.getReqDispelLevel();
	}

	/**
	 * @param i
	 * @return attackStatusObserver for this effect template or null
	 */
	public AttackCalcObserver getAttackStatusObserver(int i) {
		return attackStatusObserver != null ? attackStatusObserver[i - 1] : null;
	}

	/**
	 * @param attackStatusObserver
	 *          the attackCalcObserver to set
	 */
	public void setAttackStatusObserver(AttackCalcObserver attackStatusObserver, int i) {
		if (this.attackStatusObserver == null)
			this.attackStatusObserver = new AttackCalcObserver[4];
		this.attackStatusObserver[i - 1] = attackStatusObserver;
	}

	/**
	 * @param i
	 * @return attackShieldObserver for this effect template or null
	 */
	public AttackCalcObserver getAttackShieldObserver(int i) {
		return attackShieldObserver != null ? attackShieldObserver[i - 1] : null;
	}

	/**
	 * @param attackShieldObserver
	 *          the attackShieldObserver to set
	 */
	public void setAttackShieldObserver(AttackCalcObserver attackShieldObserver, int i) {
		if (this.attackShieldObserver == null)
			this.attackShieldObserver = new AttackCalcObserver[4];
		this.attackShieldObserver[i - 1] = attackShieldObserver;
	}

	public int getReservedInt(int i) {
		return reservedInts != null ? reservedInts[i - 1] : 0;
	}

	public void setReservedInt(int i, int value) {
		if (this.reservedInts == null)
			this.reservedInts = new int[4];
		this.reservedInts[i - 1] = value;
	}

	/**
	 * @return the launchSubEffect
	 */
	public boolean isLaunchSubEffect() {
		return launchSubEffect;
	}

	/**
	 * @param launchSubEffect
	 *          the launchSubEffect to set
	 */
	public void setLaunchSubEffect(boolean launchSubEffect) {
		this.launchSubEffect = launchSubEffect;
	}

	/**
	 * @return the shieldDefense
	 */
	public int getShieldDefense() {
		return shieldDefense;
	}

	/**
	 * @param shieldDefense
	 *          the shieldDefense to set
	 */
	public void setShieldDefense(int shieldDefense) {
		this.shieldDefense = shieldDefense;
	}
	/**
	 * reflected damage
	 * 
	 * @return
	 */
	public int getReflectedDamage() {
		return this.reflectedDamage;
	}

	public void setReflectedDamage(int value) {
		this.reflectedDamage = value;
	}

	public int getReflectedSkillId() {
		return this.reflectedSkillId;
	}

	public void setReflectedSkillId(int value) {
		this.reflectedSkillId = value;
	}
	public int getProtectedSkillId() {
		return this.protectedSkillId;
	}

	public void setProtectedSkillId(int skillId) {
		this.protectedSkillId = skillId;
	}
	
	public int getProtectedDamage() {
		return this.protectedDamage;
	}

	public void setProtectedDamage(int protectedDamage) {
		this.protectedDamage = protectedDamage;
	}
	
	public int getProtectorId() {
		return this.protectorId;
	}

	public void setProtectorId(int protectorId) {
		this.protectorId = protectorId;
	}
	

	/**
	 * @return the spellStatus
	 */
	public SpellStatus getSpellStatus() {
		return spellStatus;
	}

	/**
	 * @param spellStatus
	 *          the spellStatus to set
	 */
	public void setSpellStatus(SpellStatus spellStatus) {
		this.spellStatus = spellStatus;
	}

	/**
	 * @return the dashStatus
	 */
	public DashStatus getDashStatus() {
		return dashStatus;
	}

	/**
	 * @param dashStatus
	 *          the dashStatus to set
	 */
	public void setDashStatus(DashStatus dashStatus) {
		this.dashStatus = dashStatus;
	}
	
	/**
	 * Number of signets carved on target
	 * @return
	 */
	public int getCarvedSignet() {
		return this.carvedSignet;
	}
	public void setCarvedSignet(int value) {
		this.carvedSignet = value;
	}

	/**
	 * @return the subEffect
	 */
	public Effect getSubEffect() {
		return subEffect;
	}

	/**
	 * @param subEffect
	 *          the subEffect to set
	 */
	public void setSubEffect(Effect subEffect) {
		this.subEffect = subEffect;
	}

	/**
	 * @param effectId
	 * @return true or false
	 */
	public boolean containsEffectId(int effectId) {
		for (EffectTemplate template : sucessEffects.values()) {
			if (template.getEffectid() == effectId)
				return true;
		}
		return false;
	}
	
	public TransformType getTransformType()	{
		for (EffectTemplate et : skillTemplate.getEffects().getEffects())	{
			if (et instanceof TransformEffect)
				return ((TransformEffect) et).getTransformType();
		}
		return null;
	}
	
	public void setForcedDuration(boolean forcedDuration) {
		this.forcedDuration = forcedDuration;
	}

	/**
	 * Correct lifecycle of Effect - INITIALIZE - APPLY - START - END
	 */

	/**
	 * Do initialization with proper calculations
	 */
	public void initialize() {
		if (skillTemplate.getEffects() == null)
			return;

		for (EffectTemplate template : getEffectTemplates()) {
			template.calculate(this);

			if (template instanceof DelayedSpellAttackInstantEffect)
				setDelayedDamage(true);
			if (template instanceof DamageEffect)
				setDamageEffect(true);
		}

		for (EffectTemplate template : getEffectTemplates()) {
			template.calculateHate(this);
		}
		if (this.isLaunchSubEffect()) {
			for (EffectTemplate template : sucessEffects.values()) {
				template.calculateSubEffect(this);
			}
		}

		if (sucessEffects.isEmpty()) {
			skillMoveType = SkillMoveType.RESIST;
			if (getSkillType() == SkillType.PHYSICAL) {
				if (getAttackStatus() == AttackStatus.CRITICAL)
					setAttackStatus(AttackStatus.CRITICAL_DODGE);
				else
					setAttackStatus(AttackStatus.DODGE);
			}
			else {
				if (getAttackStatus() == AttackStatus.CRITICAL)
					setAttackStatus(AttackStatus.PHYSICAL_CRITICAL_RESIST);//TODO recheck
				else
					setAttackStatus(AttackStatus.RESIST);
			}
		}
		
		// set spellstatus for sm_castspell_end packet
		switch (AttackStatus.getBaseStatus(getAttackStatus())) {
			case DODGE:
				setSpellStatus(SpellStatus.DODGE);
				break;
			case PARRY:
				if (getSpellStatus() == SpellStatus.NONE)
					setSpellStatus(SpellStatus.PARRY);
				break;
			case BLOCK:
				if (getSpellStatus() == SpellStatus.NONE)
					setSpellStatus(SpellStatus.BLOCK);
				break;
			case RESIST:
				setSpellStatus(SpellStatus.RESIST);
				break;
		}
	}

	/**
	 * Apply all effect templates
	 */
	public void applyEffect() {

		//TODO move it somewhere more appropriate
		// Fear is not applied on players who are gliding
		if (isFearEffect()) {
			if (getEffected().isInState(CreatureState.GLIDING)) {
				// Only if player is not in Flying mode
				// TODO: verify on retail if this check is needed
				if (getEffected() instanceof Player) {
					if (!((Player) getEffected()).isInFlyingMode()) {
						((Player) getEffected()).getFlyController().onStopGliding(true);
						return;
					}
				}
			}
		}

		/**
		 * broadcast final hate to all visible objects
		 */
		//TODO hostile_type?
		if (effectHate != 0) {
			if (getEffected() instanceof Npc && !this.isDelayedDamage)	
				getEffected().getAggroList().addHate(effector, 1);
			
			effector.getController().broadcastHate(effectHate);
		}

		if (skillTemplate.getEffects() == null || sucessEffects.isEmpty())
			return;

		for (EffectTemplate template : sucessEffects.values()) {
			if (getEffected() != null) 
				if (getEffected().getLifeStats().isAlreadyDead() && !skillTemplate.hasResurrectEffect())
					continue;
			template.applyEffect(this);
			template.startSubEffect(this);
		}
	}

	/**
	 * Start effect which includes: - start effect defined in template - start subeffect if possible - activate toogle
	 * skill if needed - schedule end of effect
	 */
	public void startEffect(boolean restored) {
		if (sucessEffects.isEmpty())
			return;

		shedulePeriodicActions();

		for (EffectTemplate template : sucessEffects.values()) {
			template.startEffect(this);
			checkUseEquipmentConditions();
			checkCancelOnDmg();
		}

		if (isToggle() && effector instanceof Player) {
			activateToggleSkill();
		}
		if (!restored && !forcedDuration)
			duration = getEffectsDuration();
		if (duration == 0)
			return;
		endTime = System.currentTimeMillis() + duration;

		task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				endEffect();
			}
		}, duration);
	}

	/**
	 * Will activate toggle skill and start checking task
	 */
	private void activateToggleSkill() {
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), true));
	}

	/**
	 * Will deactivate toggle skill and stop checking task
	 */
	private void deactivateToggleSkill() {
		PacketSendUtility.sendPacket((Player) effector, new SM_SKILL_ACTIVATION(getSkillId(), false));
	}

	/**
	 * End effect and all effect actions This method is synchronized and prevented to be called several times which could
	 * cause unexpected behavior
	 */
	public synchronized void endEffect() {
		if (isStopped)
			return;
		
		for (EffectTemplate template : sucessEffects.values()) {
			template.endEffect(this);
		}
		
		// if effect is a stance, remove stance from player
		if (effector instanceof Player) {
			Player player = (Player)effector;
			if (player.getController().getStanceSkillId() == getSkillId()) {
				PacketSendUtility.sendPacket(player, new SM_PLAYER_STANCE(player, 0));
				player.getController().startStance(0);
			}
		}

		// TODO better way to finish
		if (getSkillTemplate().getTargetSlot() == SkillTargetSlot.SPEC2) {
			getEffected().getLifeStats().increaseHp(TYPE.HP, (int) (getEffected().getLifeStats().getMaxHp() * 0.2));
			getEffected().getLifeStats().increaseMp(TYPE.MP, (int) (getEffected().getLifeStats().getMaxMp() * 0.2));
		}

		if (isToggle() && effector instanceof Player) {
			deactivateToggleSkill();
		}
		stopTasks();
		effected.getEffectController().clearEffect(this);
		this.isStopped = true;
	}

	/**
	 * Stop all scheduled tasks
	 */
	public void stopTasks() {
		if (task != null) {
			task.cancel(false);
			task = null;
		}

		if (checkTask != null) {
			checkTask.cancel(false);
			checkTask = null;
		}

		if (periodicTasks != null) {
			for (Future<?> periodicTask : this.periodicTasks) {
				if (periodicTask != null) {
					periodicTask.cancel(false);
					periodicTask = null;
				}
			}
		}

		stopPeriodicActions();
	}

	/**
	 * Time till the effect end
	 * 
	 * @return
	 */
	public int getRemainingTime() {
		int remainingTime = (int) (endTime - System.currentTimeMillis());
		return remainingTime > 0 ? remainingTime : 0;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * PVP damage ration
	 * 
	 * @return
	 */
	public int getPvpDamage() {
		return skillTemplate.getPvpDamage();
	}

	public ItemTemplate getItemTemplate() {
		return itemTemplate;
	}

	/**
	 * Try to add this effect to effected controller
	 */
	public void addToEffectedController() {
		if ((!addedToController) && (effected.getLifeStats() != null) && (!effected.getLifeStats().isAlreadyDead())) {
			effected.getEffectController().addEffect(this);
			addedToController = true;
		}
	}

	/**
	 * @return the effectHate
	 */
	public int getEffectHate() {
		return effectHate;
	}

	/**
	 * @param effectHate
	 *          the effectHate to set
	 */
	public void setEffectHate(int effectHate) {
		this.effectHate = effectHate;
	}

	/**
	 * @return the tauntHate
	 */
	public int getTauntHate() {
		return tauntHate;
	}

	/**
	 * @param tauntHate
	 *          the tauntHate to set
	 */
	public void setTauntHate(int tauntHate) {
		this.tauntHate = tauntHate;
	}

	/**
	 * @param i
	 * @return actionObserver for this effect template
	 */
	public ActionObserver getActionObserver(int i) {
		return actionObserver[i - 1];
	}

	/**
	 * @param observer
	 *          the observer to set
	 */
	public void setActionObserver(ActionObserver observer, int i) {
		if (actionObserver == null)
			actionObserver = new ActionObserver[4];
		actionObserver[i - 1] = observer;
	}

	public void addSucessEffect(EffectTemplate effect) {
		sucessEffects.put(effect.getPosition(), effect);
	}

	public boolean isInSuccessEffects(int position) {
		if (sucessEffects.get(position) != null)
			return true;

		return false;
	}
	
	/**
	 * @return
	 */
	public Collection<EffectTemplate> getSuccessEffect() {
		return sucessEffects.values();
	}

	public void addAllEffectToSucess() {
		sucessEffects.clear();
		for (EffectTemplate template : getEffectTemplates()) {
			sucessEffects.put(template.getPosition(), template);
		}
	}

	public void clearSucessEffects()	{
		sucessEffects.clear();
	}
	
	private void shedulePeriodicActions() {
		if (periodicActions == null || periodicActions.getPeriodicActions() == null
			|| periodicActions.getPeriodicActions().isEmpty())
			return;
		int checktime = periodicActions.getChecktime();
		periodicActionsTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				for (PeriodicAction action : periodicActions.getPeriodicActions())
					action.act(Effect.this);
			}
		}, 0, checktime);
	}

	private void stopPeriodicActions() {
		if (periodicActionsTask != null) {
			periodicActionsTask.cancel(false);
			periodicActionsTask = null;
		}
	}

	public int getEffectsDuration() {
		int duration = 0;
		for (EffectTemplate template : sucessEffects.values()) {
			int effectDuration = template.getDuration();
			if (template.getRandomTime() > 0)
				effectDuration -= Rnd.get(template.getRandomTime());
			duration = duration > effectDuration ? duration : effectDuration;
		}
		// adjust with pvp duration
		if (effected instanceof Player && skillTemplate.getPvpDuration() != 0)
			duration = duration * skillTemplate.getPvpDuration() / 100;
		
		//special duration for soul sickness
		if (getTargetSlotEnum() == SkillTargetSlot.SPEC2) {
			duration = duration + ((Player) effector).getCommonData().getDeathCount() * duration / 2;
		}
		return duration;
	}

	public boolean isAvatar() {
		return skillTemplate.isAvatar();
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public float getZ() {
		return z;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	/**
	 * @return the skillMoveType
	 */
	public SkillMoveType getSkillMoveType() {
		return skillMoveType;
	}

	/**
	 * @param skillMoveType
	 *          the skillMoveType to set
	 */
	public void setSkillMoveType(SkillMoveType skillMoveType) {
		this.skillMoveType = skillMoveType;
	}

	/**
	 * @return the targetX
	 */
	public float getTargetX() {
		return targetX;
	}

	/**
	 * @return the targetY
	 */
	public float getTargetY() {
		return targetY;
	}

	/**
	 * @return the targetZ
	 */
	public float getTargetZ() {
		return targetZ;
	}

	public void setTragetLoc(float x, float y, float z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}

	public void setSubEffectAborted(boolean value) {
		this.subEffectAbortedBySubConditions = value;
	}

	public boolean isSubEffectAbortedBySubConditions() {
		return this.subEffectAbortedBySubConditions;
	}

	public void setCriticalRatio(float value) {
		this.criticalRatio = value;
		setAttackStatus(AttackStatus.CRITICAL);
	}

	public float getCriticalRatio() {
		return this.criticalRatio;
	}

	public void setXpBoost(boolean value) {
		this.isXpBoost = value;
	}

	public boolean isXpBoost() {
		return this.isXpBoost;
	}

	/**
	 * Check all in use equipment conditions
	 * 
	 * @return true if all conditions have been satisfied
	 */
	private boolean useEquipmentConditionsCheck() {
		Conditions useEquipConditions = skillTemplate.getUseEquipmentconditions();
		return useEquipConditions != null ? useEquipConditions.validate(this) : true;
	}

	/**
	 * Check use equipment conditions by adding Unequip observer if needed
	 */
	private void checkUseEquipmentConditions() {
		// If skill has use equipment conditions
		// Observe for unequip event and remove effect if event occurs
		if ((getSkillTemplate().getUseEquipmentconditions() != null)
			&& (getSkillTemplate().getUseEquipmentconditions().getConditions().size() > 0)) {
			ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP) {

				@Override
				public void unequip(Item item, Player owner) {
					if (!useEquipmentConditionsCheck()) {
						endEffect();
						if (this != null)
							effected.getObserveController().removeObserver(this);
					}
				}
			};
			effected.getObserveController().addObserver(observer);
		}
	}

	/**
	 * Add Attacked/Dot_Attacked observers if this effect needs to be removed on damage received by effected
	 */
	private void checkCancelOnDmg() {
		if (isCancelOnDmg()) {
			effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACKED) {

				@Override
				public void attacked(Creature creature) {
					effected.getEffectController().removeEffect(getSkillId());
				}
			});

			effected.getObserveController().attach(new ActionObserver(ObserverType.DOT_ATTACKED) {

				@Override
				public void dotattacked(Creature creature, Effect dotEffect) {
					effected.getEffectController().removeEffect(getSkillId());
				}
			});
		}
	}

	public void setCancelOnDmg(boolean value) {
		this.isCancelOnDmg = value;
	}

	public boolean isCancelOnDmg() {
		return this.isCancelOnDmg;
	}

	public void endEffects() {
		for (EffectTemplate template : sucessEffects.values()) {
			template.endEffect(this);
		}
	}

	public boolean isFearEffect() {
		for (EffectTemplate template : sucessEffects.values()) {
			if (template instanceof FearEffect)
				return true;
		}
		return false;
	}
	
	public boolean isDelayedDamage() {
		return this.isDelayedDamage;
	}
	
	public void setDelayedDamage(boolean value) {
		this.isDelayedDamage = value;
	}
	
	private int initializePower(int skillId) {
		//TODO debuffs done, Some buffs todo
		switch (skillId) {
			case 1176:// Word of Destruction I
			case 2259:// Word of Destruction II
				return 20;
			case 287:// Unwavering Devotion
			case 426:// Iron Skin
			case 537:// Prayer of Freedom
			case 1794:// Spirit Substitution
			case 671://Shock Arrow
			case 672:
			case 2089:
			case 2090:	
			case 322://Ankle Snare
			case 1560://Curse Of Weakness
			case 1561:
			case 2196:
			case 1040://Chain of Suffering
			case 2129:
			case 2136:
			case 2152:
			case 1343: //Stilling Word
				return 30;
			case 1774://Cursecloud	
			case 2225:
				return 40; //need 2 cleric dispels or potions
		}
		
		return 10;
	}

	
	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	
	/**
	 * @param power the power to set
	 */
	public void setPower(int power) {
		this.power = power;
	}
	
	public int removePower(int power) {
		this.power -= power;
		
		return this.power;
	}

	
	/**
	 * @return the isDamageEffect
	 */
	public boolean isDamageEffect() {
		return isDamageEffect;
	}

	
	/**
	 * @param isDamageEffect the isDamageEffect to set
	 */
	public void setDamageEffect(boolean isDamageEffect) {
		this.isDamageEffect = isDamageEffect;
	}
	
	/**
	 * @return the signetBurstedCount
	 */
	public int getSignetBurstedCount() {
		return signetBurstedCount;
	}

	
	/**
	 * @param signetBurstedCount the signetBurstedCount to set
	 */
	public void setSignetBurstedCount(int signetBurstedCount) {
		this.signetBurstedCount = signetBurstedCount;
	}

	public void setAura(boolean aura) {
		this.isAura = aura;
	}

	public boolean isAura() {
		return this.isAura;
	}
}
