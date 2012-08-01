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
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.model.stats.container.StatEnum;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillType;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SilenceEffect")
public class SilenceEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.SILENCE_RESISTANCE, null);
	}

	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effect.setAbnormal(AbnormalState.SILENCE.getId());
		effected.getEffectController().setAbnormal(AbnormalState.SILENCE.getId());
		if (effected.getCastingSkill() != null
			&& effected.getCastingSkill().getSkillTemplate().getType() == SkillType.MAGICAL)
			effected.getController().cancelCurrentSkill();
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.SILENCE.getId());
	}
}