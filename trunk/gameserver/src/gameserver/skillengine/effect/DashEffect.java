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
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.model.DashStatus;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;
import gameserver.world.World;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DashEffect")
public class DashEffect extends DamageEffect {

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		final Player effector = (Player) effect.getEffector();

		// Move Effector to Effected
		Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
	}

	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected() == null)
			return;
		if (!(effect.getEffector() instanceof Player))
			return;
		
		if (!super.calculate(effect, DamageType.PHYSICAL))
			return;
		
		Creature effected = effect.getEffected();
		effect.setDashStatus(DashStatus.DASH);
		effect.getSkill().setTargetPosition(effected.getX(), effected.getY(), effected.getZ(), effected.getHeading());
	}
}
