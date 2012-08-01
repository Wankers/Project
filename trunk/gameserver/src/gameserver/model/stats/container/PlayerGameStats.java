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
package gameserver.model.stats.container;

import commons.utils.Rnd;
import gameserver.configs.main.CustomConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Equipment;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.stats.calc.AdditionStat;
import gameserver.model.stats.calc.Stat2;
import gameserver.model.templates.stats.PlayerStatsTemplate;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_STATS_INFO;
import gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class PlayerGameStats extends CreatureGameStats<Player> {

	private int cachedSpeed;
	private int cachedAttackSpeed;

	/**
	 * @param owner
	 */
	public PlayerGameStats(Player owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		int currentAttackSpeed = getAttackSpeed().getCurrent();
		if (current != cachedSpeed || currentAttackSpeed != cachedAttackSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}

	@Override
	public Stat2 getMaxHp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXMP, pst.getMaxMp());
	}

	public Stat2 getMaxDp() {
		return getStat(StatEnum.MAXDP, 4000);
	}

	public Stat2 getFlyTime() {
		return getStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
	}

	@Override
	public Stat2 getAttackSpeed() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();

		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null)
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
		}
		Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}

	@Override
	public Stat2 getMovementSpeed() {
		Stat2 movementSpeed;
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		if (owner.isInFlyingState())
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000));
		else if (owner.isInState(CreatureState.FLIGHT_TELEPORT) && !owner.isInState(CreatureState.RESTING))
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		else if (owner.isInState(CreatureState.WALKING))
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000));
		else
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000));
		return movementSpeed;
	}

	@Override
	public Stat2 getAttackRange() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
		}
		return getStat(StatEnum.ATTACK_RANGE, base);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, 0);
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, 0);
	}

	@Override
	public Stat2 getPower() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}

	@Override
	public Stat2 getHealth() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}

	@Override
	public Stat2 getAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}

	@Override
	public Stat2 getAgility() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}

	@Override
	public Stat2 getKnowledge() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}

	@Override
	public Stat2 getWill() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}

	@Override
	public Stat2 getEvasion() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		return getStat(StatEnum.PARRY, base);
	}

	@Override
	public Stat2 getBlock() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		int base = 18;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical())
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, owner);
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
		return getStat(StatEnum.MAIN_HAND_POWER, stat);
	}

	public Stat2 getOffHandPAttack() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base *= 0.98;
			Stat2 stat = getStat(StatEnum.PHYSICAL_ATTACK, base);
			return getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, owner);
	}

	@Override
	public Stat2 getMainHandPCritical() {
		int base = 2;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
		}
		return getStat(StatEnum.PHYSICAL_CRITICAL, base);
	}

	public Stat2 getOffHandPCritical() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
			return getStat(StatEnum.PHYSICAL_CRITICAL, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, owner);
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMainHandAccuracy();
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
		}
		return getStat(StatEnum.PHYSICAL_ACCURACY, base);
	}

	public Stat2 getOffHandPAccuracy() {
		Equipment equipment = owner.getEquipment();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
			int base = pst.getMainHandAccuracy();
			base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
			return getStat(StatEnum.PHYSICAL_ACCURACY, base);
		}
		return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, owner);
	}

	@Override
	public Stat2 getMAttack() {
		int base = 0;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical())
				return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		else {
			base = Rnd.get(16, 20);// hand attack
		}

		if (owner.isInState(CreatureState.POWERSHARD)) {
			Item powerShard = equipment.getMainHandPowerShard();

			if (powerShard != null) {
				base += powerShard.getItemTemplate().getWeaponBoost();
			}
		}
		return getStat(StatEnum.MAGICAL_ATTACK, base);
	}

	@Override
	public Stat2 getMBoost() {
		int base = 0;
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
		}
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
	}

	@Override
	public Stat2 getMAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getMagicAccuracy();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
		}
		return getStat(StatEnum.MAGICAL_ACCURACY, base);
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}

	@Override
	public Stat2 getHpRegenRate() {
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING))
			base *= 8;
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public Stat2 getMpRegenRate() {
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING))
			base *= 8;
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public void updateStatInfo() {
		PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}
}