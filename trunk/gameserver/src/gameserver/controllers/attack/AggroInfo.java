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
package gameserver.controllers.attack;

import gameserver.model.gameobjects.AionObject;

/**
 * AggroInfo: - hate of creature - damage of creature
 * 
 * @author ATracer, Sarynth
 */
public class AggroInfo {

	private AionObject attacker;
	private int hate;
	private int damage;

	/**
	 * @param attacker
	 */
	AggroInfo(AionObject attacker) {
		this.attacker = attacker;
	}

	/**
	 * @return attacker
	 */
	public AionObject getAttacker() {
		return attacker;
	}

	/**
	 * @param damage
	 */
	public void addDamage(int damage) {
		this.damage += damage;
		if (this.damage < 0)
			this.damage = 0;
	}

	/**
	 * @param damage
	 */
	public void addHate(int damage) {
		this.hate += damage;
		if (this.hate < 1)
			this.hate = 1;
	}

	/**
	 * @return hate
	 */
	public int getHate() {
		return this.hate;
	}

	/**
	 * @param hate
	 */
	public void setHate(int hate) {
		this.hate = hate;
	}

	/**
	 * @return damage
	 */
	public int getDamage() {
		return this.damage;
	}

	/**
	 * @param damage
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}
}
