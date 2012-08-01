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

import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;


/**
 * @author MrPoke
 *
 */
public abstract class ItemUseObserver extends ActionObserver {
	/**
	 * @param observerType
	 */
	public ItemUseObserver() {
		super(ObserverType.ALL);
	}

	@Override
	public final void attack(Creature creature) {
		abort();
	}
	
	@Override
	public final void attacked(Creature creature) {
		abort();
	}
	
	@Override
	public final void died(Creature creature) {
		abort();
	}

	@Override
	public final void dotattacked(Creature creature, Effect dotEffect) {
		abort();
	}

	@Override
	public final void equip(Item item, Player owner) {
		abort();
	}

	@Override
	public final void moved() {
		abort();
	}

	@Override
	public final void skilluse(Skill skill) {
		abort();
	}

	public abstract void abort();
}
