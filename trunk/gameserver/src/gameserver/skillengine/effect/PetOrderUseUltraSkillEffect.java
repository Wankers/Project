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
package gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SUMMON_USESKILL;
import gameserver.skillengine.model.Effect;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 * modified by Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetOrderUseUltraSkillEffect")
public class PetOrderUseUltraSkillEffect extends EffectTemplate {

	@XmlAttribute
	protected boolean release;

	@Override
	public void applyEffect(Effect effect) {
		Player effector = (Player) effect.getEffector();
		
		if(effector.getSummon() == null) {
			return;
		}
		
		int effectorId = effector.getSummon().getObjectId();

		int npcId = effector.getSummon().getNpcId();
		int orderSkillId = effect.getSkillId();

		int petUseSkillId = DataManager.PET_SKILL_DATA.getPetOrderSkill(orderSkillId, npcId);
		int targetId = effect.getEffected().getObjectId();
		
		// Handle automatic release if skill expects so
		if (release)
			effector.getSummon().getController().setReleaseAfterSkill(petUseSkillId);

		PacketSendUtility.sendPacket(effector, new SM_SUMMON_USESKILL(effectorId, petUseSkillId, 1, targetId));
	}

	@Override
	public void calculate(Effect effect) {
		if (effect.getEffector() instanceof Player && effect.getEffected() != null)
			super.calculate(effect, null, null);
	}
}
