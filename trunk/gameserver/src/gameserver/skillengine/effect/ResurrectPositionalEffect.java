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

import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;
import gameserver.network.aion.serverpackets.SM_RESURRECT;
import gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectPositionalEffect")
public class ResurrectPositionalEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		Player effector = (Player) effect.getEffector();
		Player effected = (Player) effect.getEffected();

		effected.setPlayerResActivate(true);
		PacketSendUtility.sendPacket(effected, new SM_RESURRECT(effect.getEffector(), effect.getSkillId()));
		effected.setResPosState(true);
		effected.setResPosX(effector.getX());
		effected.setResPosY(effector.getY());
		effected.setResPosZ(effector.getZ());
	}

	@Override
	public void calculate(Effect effect) {
		if ((effect.getEffector() instanceof Player) && (effect.getEffected() instanceof Player)
			&& (effect.getEffected().getLifeStats().isAlreadyDead()))
			super.calculate(effect, null, null);
	}
}
