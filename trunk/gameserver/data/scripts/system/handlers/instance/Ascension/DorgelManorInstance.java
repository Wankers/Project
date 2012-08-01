/*
 * This file is part of Aion Extreme Emulator <Aion Extreme Emulator.org>
 *
 * Aion Extreme Emulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aion Extreme Emulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package instance.Ascension;

import java.util.Map;

import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticDoor;

/**
 * @author GosthMan
 */
@InstanceID(300270000)
public class DorgelManorInstance extends GeneralInstanceHandler {

	private Map<Integer,StaticDoor> doors;
	@Override
	public void onDie(Npc npc) {
		doors = instance.getDoors();
		switch(npc.getNpcId()) {
			//Kill "Cadella" To Appear "Spirit Collector"
			case 217237:
				spawn(217243, 955.5249f, 1218.6611f, 53.671883f, (byte) 90); //Spirit Collector.
				break;
			//Kill "Spirit Collector" To Appear "Ingress Shard"
			case 217243:
				spawn(217239, 902.057f, 1090.7222f, 84.549416f, (byte) 119); //Ingress Shard.
				break;
			//Kill "Ingress Shard" To Appear "Zadra Tissesort"
			case 217239:
				spawn(217241, 1006.20276f, 1267.6141f, 95.93824f, (byte) 91); //Zadra Tissesort.
				break;
			//Kill "Zadra Tissesort" To Appear "Zadra Tissesort (The Betrayer) + Celestial Observation Chamber Entrance"
			case 217241:
				spawn(217253, 864.9385f, 1234.416f, 194.97882f, (byte) 31); //Zadra Tissesort (The Betrayer).
				spawn(730393, 1006.45544f, 1308.1536f, 98.80824f, (byte) 118); //Celestial Observation Chamber Entrance.
				break;
			//Kill "Zadra Tissesort" (The Betrayer) To Appear "Celestial Observation Chamber Exit + Dorgel Manor Exit"
			case 217253:
				spawn(730383, 1005.2376f, 1205.4111f, 71.30918f, (byte) 90); //Dorgel Manor Exit.
				spawn(730394, 864.46356f, 1208.2582f, 199.04294f, (byte) 58); //Celestial Observation Chamber Exit.
				break;
		}
	}

	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}
}