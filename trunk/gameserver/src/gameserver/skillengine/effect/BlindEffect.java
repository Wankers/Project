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

import commons.utils.Rnd;
import gameserver.model.stats.container.StatEnum;
import gameserver.controllers.attack.AttackStatus;
import gameserver.controllers.observer.AttackCalcObserver;
import gameserver.controllers.observer.AttackStatusObserver;
import gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlindEffect")
public class BlindEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.BLIND_RESISTANCE, null);
	}

	@Override
	public void startEffect(Effect effect) {
		effect.setAbnormal(AbnormalState.BLIND.getId());
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.BLIND.getId());
		AttackCalcObserver acObserver = new AttackStatusObserver(value, AttackStatus.DODGE) {

			@Override
			public boolean checkAttackerStatus(AttackStatus status) {
				return Rnd.get(0, 100) <= value;
			}

		};
		effect.getEffected().getObserveController().addAttackCalcObserver(acObserver);
		effect.setAttackStatusObserver(acObserver, position);
	}

	@Override
	public void endEffect(Effect effect) {
		AttackCalcObserver acObserver = effect.getAttackStatusObserver(position);
		if (acObserver != null)
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.BLIND.getId());
	}

}