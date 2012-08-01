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
package gameserver.model.stats.calc.functions;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.calc.Stat2;
import gameserver.model.stats.container.StatEnum;
import gameserver.model.templates.item.WeaponType;

/**
 * @author ATracer (based on Mr.Poke WeaponMasteryModifier)
 */
public class StatWeaponMasteryFunction extends StatRateFunction {

	private final WeaponType weaponType;

	public StatWeaponMasteryFunction(WeaponType weaponType, StatEnum name, int value, boolean bonus) {
		super(name, value, bonus);
		this.weaponType = weaponType;
	}

	@Override
	public void apply(Stat2 stat) {
		Player player = (Player) stat.getOwner();
		switch (this.stat) {
			case MAIN_HAND_POWER:
				if (player.getEquipment().getMainHandWeaponType() == weaponType)
					super.apply(stat);
				break;
			case OFF_HAND_POWER:
				if (player.getEquipment().getOffHandWeaponType() == weaponType)
					super.apply(stat);
				break;
			default:
				if (player.getEquipment().getMainHandWeaponType() == weaponType)
					super.apply(stat);
		}

	}

}