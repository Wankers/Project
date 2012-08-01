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
package gameserver.controllers.observer;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.shield.Shield;
import gameserver.model.utils3d.Point3D;
import gameserver.services.SiegeService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;

/**
 * @author Wakizashi, Source
 */
public class ShieldObserver extends ActionObserver {

	private Creature creature;
	private Shield shield;
	private Point3D oldPosition;

	public ShieldObserver() {
		super(ObserverType.MOVE);
		this.creature = null;
		this.shield = null;
		this.oldPosition = null;
	}

	public ShieldObserver(Shield shield, Creature creature) {
		super(ObserverType.MOVE);
		this.creature = creature;
		this.shield = shield;
		this.oldPosition = new Point3D(creature.getX(), creature.getY(), creature.getZ());
	}

	@Override
	public void moved() {
		boolean passedThrough = false;
		boolean isGM = false;

		if (SiegeService.getInstance().getFortress(shield.getId()).isUnderShield())
			if (!(creature.getZ() < shield.getZ() && oldPosition.getZ() < shield.getZ()))
				if (MathUtil.isInSphere(shield, (float) oldPosition.getX(), (float) oldPosition.getY(),
						(float) oldPosition.getZ(), shield.getTemplate().getRadius()) != MathUtil.isIn3dRange(shield, creature,
						shield.getTemplate().getRadius()))
					passedThrough = true;

		if (passedThrough) {
			if (creature instanceof Player) {
				PacketSendUtility.sendMessage(((Player) creature), "You passed through shield.");
				isGM = ((Player) creature).isGM();
			}

			if (!isGM) {
				if (!(creature.getLifeStats().isAlreadyDead()))
					creature.getController().die();
				if (creature instanceof Player)
					((Player) creature).getFlyController().endFly();
				creature.getObserveController().removeObserver(this);
			}
		}

		oldPosition.x = creature.getX();
		oldPosition.y = creature.getY();
		oldPosition.z = creature.getZ();
	}

}