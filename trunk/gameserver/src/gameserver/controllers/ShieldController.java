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
package gameserver.controllers;

import gameserver.controllers.observer.ShieldObserver;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.shield.Shield;
import gameserver.model.siege.FortressLocation;
import gameserver.model.siege.SiegeRace;
import gameserver.services.SiegeService;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class ShieldController extends VisibleObjectController<Shield> {

	FastMap<Integer, ShieldObserver> observed = new FastMap<Integer, ShieldObserver>().shared();

	@Override
	public void see(VisibleObject object) {
		FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		Player player = (Player) object;

		if (loc.isUnderShield())
			if (loc.getRace() != SiegeRace.getByRace(player.getRace())) {
				ShieldObserver observer = new ShieldObserver(getOwner(), player);
				player.getObserveController().addObserver(observer);
				observed.put(player.getObjectId(), observer);
			}
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		Player player = (Player) object;

		if (loc.isUnderShield())
			if (loc.getRace() != SiegeRace.getByRace(player.getRace())) {
				ShieldObserver observer = observed.remove(player.getObjectId());
				if (observer != null) {
					if (isOutOfRange)
						observer.moved();

					player.getObserveController().removeObserver(observer);
				}
			}
	}

}