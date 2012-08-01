/*
 * This file is part of aion-unique <www.aion-unique.com>.
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

import java.util.concurrent.atomic.AtomicBoolean;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
public class ActionObserver {

	private AtomicBoolean used;

	private ObserverType observerType;

	public ActionObserver(ObserverType observerType) {
		this.observerType = observerType;
	}

	/**
	 * Make this observer usable exactly one time
	 */
	public void makeOneTimeUse() {
		used = new AtomicBoolean(false);
	}

	/**
	 * Try to use this observer. Will return true only once.
	 * 
	 * @return
	 */
	public boolean tryUse() {
		return used.compareAndSet(false, true);
	}

	/**
	 * @return the observerType
	 */
	public ObserverType getObserverType() {
		return observerType;
	}

	public void moved() {
	};

	/**
	 * @param creature
	 */
	public void attacked(Creature creature) {
	};

	/**
	 * @param creature
	 */
	public void attack(Creature creature) {
	};

	/**
	 * @param item
	 * @param owner
	 */
	public void equip(Item item, Player owner) {
	};

	/**
	 * @param item
	 * @param owner
	 */
	public void unequip(Item item, Player owner) {
	};

	/**
	 * @param skill
	 */
	public void skilluse(Skill skill) {
	};
	
	/**
	 * @param creature
	 */
	public void died(Creature creature) {
	};

	/**
	 * @param creature
	 * @param dotEffect 
	 */
	public void dotattacked(Creature creature, Effect dotEffect) {
	};
	
	/**
	 * 
	 * @param item
	 */
	public void itemused(Item item) {
	};
	
	/**
	 * 
	 * @param npc
	 */
	public void npcdialogrequested(Npc npc) {
	};
}
