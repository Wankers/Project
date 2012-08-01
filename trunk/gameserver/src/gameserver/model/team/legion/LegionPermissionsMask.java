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
package gameserver.model.team.legion;


/**
 * @author MrPoke
 *
 */
public enum LegionPermissionsMask {
	
	EDIT(0x200),
	INVITE(0x8),
	KICK(0x10),
	WH_WITHDRAWAL(0x4),
	WH_DEPOSIT(0x1000),
	ARTIFACT(0x400),
	GUARDIAN_STONE(0x800);

	
	private int rank;

	private LegionPermissionsMask(int rank) {
		this.rank =  rank;
	}
	
	public boolean can(int permission){
		return (rank & permission) != 0;
	}
}
