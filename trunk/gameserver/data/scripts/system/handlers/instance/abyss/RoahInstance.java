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
package instance.abyss;

import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;

/**
 * @author bobobear
 */
@InstanceID(300070000)
public class RoahInstance extends GeneralInstanceHandler {

	@Override
	public void onLeaveInstance(Player player) {
		Storage bag = player.getInventory();
		bag.decreaseByItemId(185000036, bag.getItemCountByItemId(185000036));
		bag.decreaseByItemId(185000037, bag.getItemCountByItemId(185000037));
		bag.decreaseByItemId(185000038, bag.getItemCountByItemId(185000038));
	}
}
