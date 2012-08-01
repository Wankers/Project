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
package gameserver.controllers.effect;

import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.configs.main.CustomConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_ABNORMAL_STATE;
import gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillTargetSlot;
import gameserver.skillengine.model.SkillTemplate;
import gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import gameserver.taskmanager.tasks.TeamEffectUpdater;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PlayerEffectController extends EffectController {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(PlayerEffectController.class);
    /**
     * weapon mastery
     */
    private int weaponEffects;
    /**
     * armor mastery
     */
    private int armorEffects;
    /**
     * dual weapon mastery
     */
    private int dualEffects;
    /**
     * holds the value of DualMasteryEffect
     */
    private int dualEffect;
    /**
     * shield mastery
     */
    private int shieldEffects;
    /**
     * current food effect
     */
	public PlayerEffectController(Creature owner) {
		super(owner);
	}

	@Override
	public void addEffect(Effect effect) {
		if (checkDuelCondition(effect))
			return;

		super.addEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}

	@Override
	public void clearEffect(Effect effect) {
		super.clearEffect(effect);
		updatePlayerIconsAndGroup(effect);
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	/**
	 * @param effect
	 */
	private void updatePlayerIconsAndGroup(Effect effect) {
		if (!effect.isPassive()) {
			updatePlayerEffectIcons();
			if (getOwner().isInTeam()) {
				TeamEffectUpdater.getInstance().startTask(getOwner());
			}
		}
	}
	
	@Override
	public void updatePlayerEffectIcons() {
		getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_EFFECT_ICONS);
	}
	
	@Override
	public void updatePlayerEffectIconsImpl() {
		Collection<Effect> effects = getAbnormalEffectsToShow();
		PacketSendUtility.sendPacket((Player) getOwner(), new SM_ABNORMAL_STATE(effects, abnormals));
	}

	/**
	 * Effect of DEBUFF should not be added if duel ended (friendly unit)
	 * 
	 * @param effect
	 * @return
	 */
	private boolean checkDuelCondition(Effect effect) {
		Creature creature = effect.getEffector();
		if (creature instanceof Player) {
			if (!getOwner().isEnemy(creature) && effect.getTargetSlot() == SkillTargetSlot.DEBUFF.ordinal()) {
				return true;
			}
		}

		return false;
	}

    /**
     * Weapon mastery
     */
    public void setWeaponMastery(int skillId) {
        weaponEffects = skillId;
    }

    public void unsetWeaponMastery() {
        weaponEffects = 0;
    }

    public int getWeaponMastery() {
        return weaponEffects;
    }

    public boolean isWeaponMasterySet(int skillId) {
        return weaponEffects == skillId;
    }

    /**
     * Armor mastery
     */
    public void setArmorMastery(int skillId) {
        armorEffects = skillId;
    }

    public void unsetArmorMastery() {
        armorEffects = 0;
    }

    public int getArmorMastery() {
        return armorEffects;
    }

    public boolean isArmorMasterySet(int skillId) {
        return armorEffects == skillId;
    }

    /**
     * Dual Weapon mastery
     */
    public boolean isDualMasterySet(int skillId) {
        return dualEffects == skillId;
    }

    public void setDualMastery(int skillId) {
        dualEffects = skillId;
    }

    public void unsetDualMastery() {
        dualEffects = 0;
    }

    public int getDualMastery() {
        return dualEffects;
    }

    /**
     * Set dualEffect, used in calculation of offhand damage
     * @param dualEffect
     */
    public void setDualEffect(int dualEffect) {
        this.dualEffect = dualEffect;
    }

    public void unsetDualEffect() {
        dualEffect = 0;
    }

    public int getDualEffect() {
        return dualEffect;
    }

    /**
     * Shield mastery
     */
    public void setShieldMastery(int skillId) {
        shieldEffects = skillId;
    }

    public void unsetShieldMastery() {
        shieldEffects = 0;
    }

    public int getShieldMastery() {
        return shieldEffects;
    }

    public boolean isShieldMasterySet(int skillId) {
        return shieldEffects == skillId;
    }
	/**
	 * @param skillId
	 * @param skillLvl
	 * @param currentTime
	 * @param reuseDelay
	 */
	public void addSavedEffect(int skillId, int skillLvl, int remainingTime, long endTime) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		int duration = template.getEffectsDuration();
		if (template.getTargetSlot() == SkillTargetSlot.SPEC2)
			duration = duration + getOwner().getCommonData().getDeathCount() * duration / 2;

		if (remainingTime <= 0)
			return;
		if (CustomConfig.ABYSSXFORM_LOGOUT
			&& ((skillId >= 11885 && skillId <= 11894)
			|| (skillId >= 11907 && skillId <= 11916))) {
			
			if (System.currentTimeMillis() >= endTime)
				return;
			else
				remainingTime = (int)(endTime - System.currentTimeMillis());
		} 
		
		Effect effect = new Effect(getOwner(), getOwner(), template, skillLvl, remainingTime);
		abnormalEffectMap.put(effect.getStack(), effect);
		effect.addAllEffectToSucess();
		effect.startEffect(true);

		if (effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW)
			PacketSendUtility.sendPacket(getOwner(), new SM_ABNORMAL_STATE(Collections.singletonList(effect), abnormals));

	}

	@Override
	public void broadCastEffectsImp() {
		super.broadCastEffectsImp();
		Player player = getOwner();
		if (player.getController().isUnderStance()) {
			PacketSendUtility.sendPacket(player, new SM_PLAYER_STANCE(player, 1));
		}
	}

}
