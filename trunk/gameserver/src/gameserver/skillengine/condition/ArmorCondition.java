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

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.calc.Stat2;
import gameserver.model.stats.calc.functions.IStatFunction;
import gameserver.model.templates.item.ArmorType;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArmorCondition")
public class ArmorCondition extends Condition {

	@XmlAttribute(name = "armor")
	private ArmorType armorType;

	@Override
	public boolean validate(Skill env) {
		return isValidArmor(env.getEffector());
	}

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return isValidArmor(stat.getOwner());
	}
	
	@Override
	public boolean validate(Effect effect) {
		return isValidArmor(effect.getEffector());
	}

	/**
	 * @param creature
	 * @return
	 */
	private boolean isValidArmor(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			return player.getEquipment().isArmorTypeEquipped(armorType);
		}
		return false;
	}

}
