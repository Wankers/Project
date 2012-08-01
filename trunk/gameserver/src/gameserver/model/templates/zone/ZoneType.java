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
package gameserver.model.templates.zone;


/**
 * @author MrPoke
 *
 */
public enum ZoneType {
	
	FLY(0),
	DAMAGE(1),
	WATER(2),
	SIEGE(3),
	PVP(4);
	
	private byte value;

	/**
	 * @param value
	 */
	private ZoneType(int value) {
		this.value = (byte)value;
	}
	
	/**
	 * @return the value
	 */
	public byte getValue() {
		return value;
	}
}
