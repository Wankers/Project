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
package gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.attack.AttackUtil;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.change.Func;
import gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DamageEffect")
public abstract class DamageEffect extends EffectTemplate {

	@XmlAttribute
	protected Func mode = Func.ADD;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.getEffected().getController()
			.onAttack(effect.getEffector(), effect.getSkillId(), effect.getReserved1(), true);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}

	public boolean calculate(Effect effect, DamageType damageType) {
		if (!super.calculate(effect, null, null))
			return false;
		
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		int bonus = getActionModifiers(effect);
		int accMod = this.accMod2 + this.accMod1 * skillLvl;
		
		switch (damageType) {
			case PHYSICAL:
				int rndDmg = (this instanceof SkillAttackInstantEffect ? ((SkillAttackInstantEffect)this).getRnddmg() : 0);
				AttackUtil.calculatePhysicalSkillResult(effect, valueWithDelta, bonus, this.getMode(), rndDmg, accMod);
				break;
			case MAGICAL:
				boolean useKnowledge = true;
				if (this instanceof ProcAtkInstantEffect) {
					useKnowledge = false;
				}
				AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, bonus, getElement(), useKnowledge, false);
				break;
			default:
				AttackUtil.calculatePhysicalSkillResult(effect, 0, 0, this.getMode(), 0, accMod);
		}
		
		return true;
	}

	public Func getMode() {
		return mode;
	}

}
