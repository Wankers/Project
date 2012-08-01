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

import javolution.util.FastMap;

import gameserver.controllers.observer.FlyRingObserver;
import gameserver.model.flyring.FlyRing;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;

/**
 * @author xavier
 */
public class FlyRingController extends VisibleObjectController<FlyRing> {

	FastMap<Integer, FlyRingObserver> observed = new FastMap<Integer, FlyRingObserver>().shared();

	@Override
	public void see(VisibleObject object) {
		Player p = (Player) object;
		FlyRingObserver observer = new FlyRingObserver(getOwner(), p);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		Player p = (Player) object;
		FlyRingObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange) {
			observer.moved();
		}
		p.getObserveController().removeObserver(observer);
	}
}
