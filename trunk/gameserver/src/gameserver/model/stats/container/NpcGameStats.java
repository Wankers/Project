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
package gameserver.model.stats.container;

import commons.utils.Rnd;
import gameserver.ai2.AI2Logger;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.SummonedObject;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.stats.calc.Stat2;
import gameserver.model.templates.stats.NpcStatsTemplate;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class NpcGameStats extends CreatureGameStats<Npc> {

	int currentRunSpeed = 0;
	private long lastAttackTime = 0;
	private long lastAttackedTime = 0;
	private long nextAttackTime = 0;
	private long lastSkillTime = 0;
	private long lastSkilledTime = 0;
	private int cachedState;
	private Stat2 cachedSpeedStat;
	private long lastGeoZUpdate;
	private long lastChangeTarget = 0;
	private int pAccuracy = 0;
	private int mRes = 0;
	public NpcGameStats(Npc owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		checkSpeedStats();
	}
	
	private void checkSpeedStats() {
		Stat2 oldSpeed = cachedSpeedStat;
		cachedSpeedStat = null;
		Stat2 newSpeed = getMovementSpeed();
		cachedSpeedStat = newSpeed;
		if (oldSpeed == null || oldSpeed.getCurrent() != newSpeed.getCurrent()) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
	}

	@Override
	public Stat2 getMaxHp() {
		return getStat(StatEnum.MAXHP, owner.getObjectTemplate().getStatsTemplate().getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		return getStat(StatEnum.MAXMP, owner.getObjectTemplate().getStatsTemplate().getMaxMp());
	}

	@Override
	public Stat2 getAttackSpeed() {
		return getStat(StatEnum.ATTACK_SPEED, Math.round(owner.getObjectTemplate().getAttackDelay()));
	}

	@Override
	public Stat2 getMovementSpeed() {
		int currentState = owner.getState();
		Stat2 cachedSpeed = cachedSpeedStat;
		if (cachedSpeed != null && cachedState == currentState) {
			return cachedSpeed;
		}
		Stat2 newSpeedStat = null;
		if (owner.isInFlyingState() || owner.isInState(CreatureState.GLIDING)) {
			newSpeedStat = getStat(StatEnum.FLY_SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1.3f * 1000));
		}
		else if (owner.isInState(CreatureState.WEAPON_EQUIPPED)) {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeedFight() * 1000));
		}
		else if (owner.isInState(CreatureState.WALKING)) {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed() * 1000));
		}
		else {
			newSpeedStat = getStat(StatEnum.SPEED,
				Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1000));
		}
		cachedState = currentState;
		cachedSpeedStat = newSpeedStat;
		return newSpeedStat;
	}

	@Override
	public Stat2 getAttackRange() {
		return getStat(StatEnum.ATTACK_RANGE, Math.round(owner.getObjectTemplate().getAttackRange() * 1000));
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, owner.getObjectTemplate().getStatsTemplate().getPdef());
	}

	@Override
	public Stat2 getMResist() {
		if (mRes == 0){
			mRes = Math.round(owner.getLevel()*17.5f+75);
		}
		return getStat(StatEnum.MAGICAL_RESIST, mRes);
	}

	@Override
	public Stat2 getPower() {
		return getStat(StatEnum.POWER, 100);
	}

	@Override
	public Stat2 getHealth() {
		return getStat(StatEnum.HEALTH, 100);
	}

	@Override
	public Stat2 getAccuracy() {
		return getStat(StatEnum.ACCURACY, 100);
	}

	@Override
	public Stat2 getAgility() {
		return getStat(StatEnum.AGILITY, 100);
	}

	@Override
	public Stat2 getKnowledge() {
		return getStat(StatEnum.KNOWLEDGE, 100);
	}

	@Override
	public Stat2 getWill() {
		return getStat(StatEnum.WILL, 100);
	}

	@Override
	public Stat2 getEvasion() {
		if (pAccuracy == 0)
			calcStats();
		return getStat(StatEnum.EVASION, pAccuracy);
	}

	@Override
	public Stat2 getParry() {
		return getStat(StatEnum.PARRY, 100);
	}

	@Override
	public Stat2 getBlock() {
		return getStat(StatEnum.BLOCK, 0);
	}

	@Override
	public Stat2 getMainHandPAttack() {
		return getStat(StatEnum.PHYSICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMainHandAttack());
	}

	@Override
	public Stat2 getMainHandPCritical() {
		return getStat(StatEnum.PHYSICAL_CRITICAL, 10);
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		if (pAccuracy == 0)
			calcStats();
		return getStat(StatEnum.PHYSICAL_ACCURACY, pAccuracy);
	}

	@Override
	public Stat2 getMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, 100);
	}

	@Override
	public Stat2 getMBoost() {
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 100);
	}

	@Override
	public Stat2 getMAccuracy() {
		if (pAccuracy == 0)
			calcStats();
		// Trap's MAccuracy is being calculated into TrapGameStats and is related to master's MAccuracy
		if (owner instanceof SummonedObject)
			return getStat(StatEnum.MAGICAL_ACCURACY, pAccuracy);
		return getMainHandPAccuracy();
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}

	@Override
	public Stat2 getHpRegenRate() {
		NpcStatsTemplate nst = owner.getObjectTemplate().getStatsTemplate();
		return getStat(StatEnum.REGEN_HP, nst.getMaxHp() / 4);
	}

	@Override
	public Stat2 getMpRegenRate() {
		throw new IllegalStateException("No mp regen for NPC");
	}

	public int getLastAttackTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackTime) / 1000f);
	}

	public int getLastAttackedTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackedTime) / 1000f);
	}

	public void renewLastAttackTime() {
		this.lastAttackTime = System.currentTimeMillis();
	}

	public void renewLastAttackedTime() {
		this.lastAttackedTime = System.currentTimeMillis();
	}

	public boolean isNextAttackScheduled() {
		return nextAttackTime - System.currentTimeMillis() > 50;
	}

	public void setNextAttackTime(long nextAttackTime) {
		this.nextAttackTime = nextAttackTime;
	}

	/**
	 * @return next possible attack time depending on stats
	 */
	public int getNextAttackInterval() {
		long attackDelay = System.currentTimeMillis() - lastAttackTime;
		int attackSpeed = getAttackSpeed().getCurrent();
		if (attackSpeed == 0) {
			attackSpeed = 2000;
		}
		if (owner.getAi2().isLogging()) {
			AI2Logger.info(owner.getAi2(), "adelay = " + attackDelay + " aspeed = " + attackSpeed);
		}
		int nextAttack = 0;
		if (attackDelay < attackSpeed) {
			nextAttack = (int) (attackSpeed - attackDelay);
		}
		return nextAttack;
	}
	
	/**
	 * @return next possible skill time depending on time
	 */
	
	public void renewLastSkillTime() {
		this.lastSkillTime = System.currentTimeMillis();
	}

	public void renewLastSkilledTime() {
		this.lastSkilledTime = System.currentTimeMillis();
	}
	
	public void renewLastChangeTargetTime() {
		this.lastChangeTarget = System.currentTimeMillis();
	}

	public int getLastSkillTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastSkillTime) / 1000f);
	}

	public int getLastSkilledTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastSkilledTime) / 1000f);
	}
	
	public int getLastChangeTargetTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastChangeTarget) / 1000f);
	}

	public boolean canUseNextSkill() {
		if (getLastSkilledTimeDelta() >= 6 + Rnd.get(-3,3))
			return true;
		else
			return false;
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}

	public final long getLastGeoZUpdate() {
		return lastGeoZUpdate;
	}
	
	/**
	 * @param lastGeoZUpdate the lastGeoZUpdate to set
	 */
	public void setLastGeoZUpdate(long lastGeoZUpdate) {
		this.lastGeoZUpdate = lastGeoZUpdate;
	}

	private void calcStats(){
		int lvl = owner.getLevel();
		double accuracy = lvl*(33.6f-(0.16*lvl))+5;
		switch (owner.getObjectTemplate().getRank()){
			case NOVICE:
			case DISCIPLINED:
				break;
			case SEASONED:
				accuracy *= 1.05f;
				break;
			case EXPERT:
				accuracy *= 1.15f;
				break;
			case MASTER:
				accuracy *= 1.25f;
				break;
			case VETERAN:
				accuracy *= 1.35f;
				break;
		}
		this.pAccuracy = (int) Math.round(accuracy);
	}
}
