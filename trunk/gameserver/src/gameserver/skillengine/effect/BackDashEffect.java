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

import gameserver.geoEngine.math.Vector3f;

import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.model.DashStatus;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;
import gameserver.utils.MathUtil;
import gameserver.world.World;
import gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackDashEffect")
public class BackDashEffect extends DamageEffect {

	@XmlAttribute(name = "distance")
	private float distance;
	// backward = 1, forward = 0
	private float direction = 1;

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect);
		final Player effector = (Player) effect.getEffector();

		Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
	}

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, DamageType.PHYSICAL))
			return;
		
		effect.setDashStatus(DashStatus.BACKDASH);

		final Player effector = (Player) effect.getEffector();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1,
			effector.getY() + y1, effector.getZ(), false);
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(),
			effector.getHeading());
	}
}
