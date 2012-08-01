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

import gameserver.controllers.attack.AttackUtil;
import gameserver.model.gameobjects.Creature;
import gameserver.skillengine.effect.DamageEffect;
import gameserver.skillengine.model.DispelCategoryType;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillTargetSlot;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DispelBuffCounterAtkEffect")
public class DispelBuffCounterAtkEffect extends DamageEffect {

	@XmlAttribute
	protected int dpower;
	@XmlAttribute
	protected int power;
	@XmlAttribute
	protected int hitvalue;
	@XmlAttribute
	protected int hitdelta;
	@XmlAttribute(name = "dispel_level")
	protected int dispelLevel;

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, null, null))
			return;
		
		Creature effected = effect.getEffected();
		int count = value + delta * effect.getSkillLevel();
		int finalPower = power + dpower * effect.getSkillLevel();
		
		int i = effected.getEffectController().calculateNumberOfEffects(DispelCategoryType.BUFF, SkillTargetSlot.BUFF, dispelLevel);
		i = (i < count ? i : count);
		
		int newValue = 0;
		if (i == 1)
			newValue = hitvalue;
		else if (i > 1)
			newValue = hitvalue + ((hitvalue / 2) * (i - 1));

		int valueWithDelta = newValue + hitdelta * effect.getSkillLevel();

		int bonus = getActionModifiers(effect);

		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, bonus, getElement());

		// First cancel buffs so to avoid shield effects to calculate damage as 0
		//TOOD move to apply effect, this is not good
		effect.getEffected().getEffectController().removeEffectByDispelCat(DispelCategoryType.BUFF, SkillTargetSlot.BUFF
				, i, dispelLevel, finalPower, false);
	}
}
