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

import gameserver.controllers.attack.AttackUtil;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.model.DashStatus;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;
import gameserver.utils.MathUtil;
import gameserver.world.World;
import gameserver.world.geo.GeoService;

/**
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MoveBehindEffect")
public class MoveBehindEffect extends DamageEffect {

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		final Player effector = (Player) effect.getEffector();
		// Deselect targets
		AttackUtil.deselectTargettingMe(effector);

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
		
		effect.setDashStatus(DashStatus.MOVEBEHIND);

		final Creature effected = effect.getEffected();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effected.getHeading()));
		float x1 = (float) (Math.cos(Math.PI + radian) * 1.3F);
		float y1 = (float) (Math.sin(Math.PI + radian) * 1.3F);
		float z = GeoService.getInstance().getZAfterMoveBehind(effected.getWorldId(), effected.getX() + x1,
			effected.getY() + y1, effected.getZ(), effected.getInstanceId());
		effect.getSkill().setTargetPosition(effected.getX() + x1, effected.getY() + y1, z, effected.getHeading());
	}
}
