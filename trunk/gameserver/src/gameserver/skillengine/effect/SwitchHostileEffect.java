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

import gameserver.controllers.attack.AggroInfo;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SwitchHostileEffect")
public class SwitchHostileEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {

		Creature effected = effect.getEffected();
		Creature effector = effect.getEffector();

		if (((Player) effector).getSummon() != null) {
			AggroInfo aggroInfo = effected.getAggroList().getAggroInfo(effector);
			if (aggroInfo != null && aggroInfo.getAttacker() == ((Player) effector).getSummon()) {
				int hate = aggroInfo.getHate();
				effected.getAggroList().stopHating(((Player) effector).getSummon());
				effected.getAggroList().remove(((Player) effector).getSummon());
				effected.getAggroList().addHate(effector, hate);
			}
		}
	}
}
