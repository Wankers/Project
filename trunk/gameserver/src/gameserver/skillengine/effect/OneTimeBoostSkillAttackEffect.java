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

import gameserver.controllers.observer.AttackCalcObserver;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillAttackEffect")
public class OneTimeBoostSkillAttackEffect extends BufEffect {

	@XmlAttribute
	private int count;

	@XmlAttribute
	private SkillType type;

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		final int stopCount = count;
		final float percent = 1.0f + value / 100.0f;
		AttackCalcObserver observer = null;

		switch (type) {
			case MAGICAL:
				observer = new AttackCalcObserver() {

					private int count = 0;

					@Override
					public float getBaseMagicalDamageMultiplier() {
						if (count++ < stopCount)
							return percent;
						else
							effect.getEffected().getEffectController().removeEffect(effect.getSkillId());

						return 1.0f;
					}
				};
				break;
			case PHYSICAL:
				observer = new AttackCalcObserver() {

					private int count = 0;

					@Override
					public float getBasePhysicalDamageMultiplier(boolean isSkill) {
						if (!isSkill)
							return 1f;

						if (count++ < stopCount) {
							if (count == stopCount)
								effect.getEffected().getEffectController().removeEffect(effect.getSkillId());
							return percent;
						}

						return 1.0f;
					}
				};
				break;
		}

		effect.getEffected().getObserveController().addAttackCalcObserver(observer);
		effect.setAttackStatusObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		AttackCalcObserver observer = effect.getAttackStatusObserver(position);
		effect.getEffected().getObserveController().removeAttackCalcObserver(observer);
	}
}
