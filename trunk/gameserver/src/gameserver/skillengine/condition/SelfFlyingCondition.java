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
package gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.FlyingRestriction;
import gameserver.skillengine.model.Skill;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelfFlyingCondition")
public class SelfFlyingCondition extends Condition {

	@XmlAttribute(required = true)
	protected FlyingRestriction restriction;
	@Override
	public boolean validate(Skill env) {
		if (env.getEffector() == null)
			return false;
		
		switch (restriction) {
			case FLY:
				return env.getEffector().isFlying();
			case GROUND:
				return !env.getEffector().isFlying();
		}
		
		return true;
	}

	@Override
	public boolean validate(Effect effect) {
		if (effect.getEffector() == null)
			return false;
		
		switch (restriction) {
			case FLY:
				return effect.getEffector().isFlying();
			case GROUND:
				return !effect.getEffector().isFlying();
		}
		
		return true;
	}

}
