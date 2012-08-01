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

import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillAtkDrainInstantEffect")
public class SkillAtkDrainInstantEffect extends DamageEffect {

	@XmlAttribute(name = "hp_percent")
	protected int hp_percent;
	@XmlAttribute(name = "mp_percent")
	protected int mp_percent;

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		if (hp_percent != 0) {
			effect.getEffector().getLifeStats().increaseHp(TYPE.ABSORBED_HP, effect.getReserved1() * hp_percent / 100, effect.getSkillId(), LOG.SKILLLATKDRAININSTANT);
		}
		if (mp_percent != 0) {
			effect.getEffector().getLifeStats().increaseMp(TYPE.MP, effect.getReserved1() * mp_percent / 100, effect.getSkillId(), LOG.SKILLLATKDRAININSTANT);
		}
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, DamageType.PHYSICAL);
	}
}
