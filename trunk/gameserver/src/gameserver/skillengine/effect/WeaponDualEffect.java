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
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.calc.functions.IStatFunction;
import gameserver.model.stats.calc.functions.StatDualWeaponMasteryFunction;
import gameserver.skillengine.model.Effect;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponDualEffect")
public class WeaponDualEffect extends BufEffect {

	@Override
	public void startEffect(Effect effect) {
		if (change == null)
			return;
		
		if (effect.getEffected() instanceof Player)
			((Player)effect.getEffected()).setDualEffectValue(value);
		
		List<IStatFunction> modifiers = getModifiers(effect);
		List<IStatFunction> masteryModifiers = new ArrayList<IStatFunction>(modifiers.size());
		for (IStatFunction modifier : modifiers) {
			masteryModifiers.add(new StatDualWeaponMasteryFunction(effect, modifier));
		}
		if (masteryModifiers.size() > 0) {
			effect.getEffected().getGameStats().addEffect(effect, masteryModifiers);
		}
	}
	
	@Override
	public void endEffect(Effect effect) {
		if (effect.getEffected() instanceof Player)
			((Player)effect.getEffected()).setDualEffectValue(0);
		
		super.endEffect(effect);
	}
	
}
