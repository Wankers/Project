/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package instance;

import java.util.Map;

import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticDoor;

/**
 * @author xTz
 */
@InstanceID(300150000)
public class UdasTempleInstance extends GeneralInstanceHandler {
	
	private Map<Integer,StaticDoor> doors;
	@Override
	public void onDie(Npc npc) {
		doors = instance.getDoors();
		switch(npc.getNpcId())
		{
			case 215790:
				openDoor(99);
				break;
			case 215783:
				spawn(730255, 508.361f, 362.717f, 137f, (byte) 31);
				break;
		}
	}
	
	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}
}
