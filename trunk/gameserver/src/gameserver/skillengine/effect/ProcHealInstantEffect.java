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
import javax.xml.bind.annotation.XmlType;

import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.HealType;

/**
 * @author ATracer
 * modified by Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcHealInstantEffect")
public class ProcHealInstantEffect extends AbstractHealEffect {

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, HealType.HP);
	}
	
	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect, HealType.HP);
	}
	
	@Override
	protected int getCurrentStatValue(Effect effect) {
		return effect.getEffected().getLifeStats().getCurrentHp();
	}

	@Override
	protected int getMaxStatValue(Effect effect) {
		return effect.getEffected().getGameStats().getMaxHp().getCurrent();
	}
}
