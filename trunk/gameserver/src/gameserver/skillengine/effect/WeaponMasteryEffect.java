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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.stats.calc.functions.IStatFunction;
import gameserver.model.stats.calc.functions.StatWeaponMasteryFunction;
import gameserver.model.stats.container.StatEnum;
import gameserver.model.templates.item.WeaponType;
import gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponMasteryEffect")
public class WeaponMasteryEffect extends BufEffect {

	@XmlAttribute(name = "weapon")
	private WeaponType weaponType;

	@Override
	public void startEffect(Effect effect) {
		if (change == null)
			return;

		List<IStatFunction> modifiers = getModifiers(effect);
		List<IStatFunction> masteryModifiers = new ArrayList<IStatFunction>(modifiers.size());
		for (IStatFunction modifier : modifiers) {
			if (weaponType.getRequiredSlots() == 2) {
				masteryModifiers.add(new StatWeaponMasteryFunction(weaponType, modifier.getName(), modifier.getValue(),
					modifier.isBonus()));
			}
			else if (modifier.getName() == StatEnum.PHYSICAL_ATTACK) {
				masteryModifiers.add(new StatWeaponMasteryFunction(weaponType, StatEnum.MAIN_HAND_POWER, modifier.getValue(),
					modifier.isBonus()));
				masteryModifiers.add(new StatWeaponMasteryFunction(weaponType, StatEnum.OFF_HAND_POWER, modifier.getValue(),
					modifier.isBonus()));
			}
		}
		effect.getEffected().getGameStats().addEffect(effect, masteryModifiers);
	}

}
