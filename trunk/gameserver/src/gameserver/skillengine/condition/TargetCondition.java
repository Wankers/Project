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
package gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.Servant;
import gameserver.model.gameobjects.Summon;
import gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetCondition")
public class TargetCondition extends Condition {

	@XmlAttribute(required = true)
	protected TargetAttribute value;

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link TargetAttribute }
	 */
	public TargetAttribute getValue() {
		return value;
	}

	@Override
	public boolean validate(Skill skill) {
		if (value != TargetAttribute.NONE && skill.getFirstTarget() == null) {
			return false;
		}
		skill.setTargetCondition(value);
		switch (value) {
			case NPC:
				return ((skill.getFirstTarget() instanceof Npc) || (skill.checkNonTargetAOE()));
			case PC:
				return ( (skill.getFirstTarget() instanceof Player)
						|| ((skill.getEffector() instanceof Summon) && (skill.isFirstTargetSelf()))
						|| ((skill.getEffector() instanceof Servant) && (skill.isFirstTargetSelf())) );
			default:
				return false;
		}
	}
}
