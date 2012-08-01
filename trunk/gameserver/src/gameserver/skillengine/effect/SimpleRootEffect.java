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

import gameserver.geoEngine.math.Vector3f;
import gameserver.model.gameobjects.Creature;
import gameserver.model.stats.container.StatEnum;
import gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillMoveType;
import gameserver.skillengine.model.SpellStatus;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.geo.GeoService;

/**
 * @author VladimirZ
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleRootEffect")
public class SimpleRootEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(),
			effected.getHeading());
	}

	@Override
	public void calculate(Effect effect) {
		// Effect is applied only on moving targets, REALLY?
		if (!effect.getEffected().getMoveController().isInMove())
			return;
		
		if (!super.calculate(effect, StatEnum.STAGGER_RESISTANCE, null))
			return;

		final Creature effected = effect.getEffected();

		effect.setSpellStatus(SpellStatus.NONE);
		effect.setSkillMoveType(SkillMoveType.KNOCKBACK);
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effect.getEffector().getHeading()));
		float x1 = (float) (Math.cos(radian) * 1);
		float y1 = (float) (Math.sin(radian) * 1);

		float z = effected.getZ();
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1,
			effected.getY() + y1, effected.getZ(), false);
		x1 = closestCollision.x;
		y1 = closestCollision.y;
		z = closestCollision.z;
		effect.setTragetLoc(x1, y1, z);
	}

	@Override
	public void startEffect(final Effect effect) {
		// effect.getEffected().getController().cancelCurrentSkill(); //TODO: Not sure about this
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.KNOCKBACK.getId());
		effect.setAbnormal(AbnormalState.KNOCKBACK.getId());
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_TARGET_IMMOBILIZE(effect.getEffected()));
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.KNOCKBACK.getId());
	}
}
