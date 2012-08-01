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
package gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAttribute;

import gameserver.skillengine.effect.AbnormalState;
import gameserver.skillengine.model.Effect;


/**
 * @author kecimis
 *
 */
public class AbnormalDamageModifier extends ActionModifier {

	@XmlAttribute(required = true)
	protected AbnormalState state;
	/* (non-Javadoc)
	 * @see gameserver.skillengine.effect.modifier.ActionModifier#analyze(gameserver.skillengine.model.Effect)
	 */
	@Override
	public int analyze(Effect effect) {
		return (value + effect.getSkillLevel() * delta);
	}

	/* (non-Javadoc)
	 * @see gameserver.skillengine.effect.modifier.ActionModifier#check(gameserver.skillengine.model.Effect)
	 */
	@Override
	public boolean check(Effect effect) {
		return effect.getEffected().getEffectController().isAbnormalSet(state);
	}

}
