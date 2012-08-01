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
package gameserver.controllers.observer;

import java.util.List;

import gameserver.controllers.attack.AttackResult;
import gameserver.controllers.attack.AttackStatus;
import gameserver.model.gameobjects.Creature;

/**
 * @author ATracer
 */
public class AttackCalcObserver {

	/**
	 * @param status
	 * @return false
	 */
	public boolean checkStatus(AttackStatus status) {
		return false;
	}

	/**
	 * @param attackList
	 * @return value
	 */
	public void checkShield(List<AttackResult> attackList, Creature attacker) {

	}

	/**
	 * @param status
	 * @return
	 */
	public boolean checkAttackerStatus(AttackStatus status) {
		return false;
	}

	/**
	 * @param isSkill
	 * @return physical damage multiplier
	 */
	public float getBasePhysicalDamageMultiplier(boolean isSkill) {
		return 1f;
	}

	/**
	 * @return magic damage multiplier
	 */
	public float getBaseMagicalDamageMultiplier() {
		return 1f;
	}
}
