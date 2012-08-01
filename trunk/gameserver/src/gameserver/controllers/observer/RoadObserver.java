/**
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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

import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.road.Road;
import gameserver.model.templates.road.RoadExit;
import gameserver.model.utils3d.Point3D;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.MathUtil;
import gameserver.world.WorldType;

/**
 * @author SheppeR
 */
public class RoadObserver extends ActionObserver {

	private Player player;
	private Road road;
	private Point3D oldPosition;

	public RoadObserver() {
		super(ObserverType.MOVE);
		this.player = null;
		this.road = null;
		this.oldPosition = null;
	}

	public RoadObserver(Road road, Player player) {
		super(ObserverType.MOVE);
		this.player = player;
		this.road = road;
		this.oldPosition = new Point3D(player.getX(), player.getY(), player.getZ());
	}

	@Override
	public void moved() {
		Point3D newPosition = new Point3D(player.getX(), player.getY(), player.getZ());
		boolean passedThrough = false;

		if (road.getPlane().intersect(oldPosition, newPosition)) {
			Point3D intersectionPoint = road.getPlane().intersection(oldPosition, newPosition);
			if (intersectionPoint != null) {
				double distance = Math.abs(road.getPlane().getCenter().distance(intersectionPoint));

				if (distance < road.getTemplate().getRadius()) {
					passedThrough = true;
				}
			}
			else {
				if (MathUtil.isIn3dRange(road, player, road.getTemplate().getRadius()))
				{
					passedThrough = true;
				}
			}
		}

		if (passedThrough) {
			RoadExit exit = road.getTemplate().getRoadExit();
			
			WorldType type = road.getWorldType();
			if (type == WorldType.ELYSEA) {
				if (player.getRace() == Race.ELYOS) {
					TeleportService.teleportTo(player, exit.getMap(), exit.getX(), exit.getY(), exit.getZ(), 0);
				}
			}
			else if (type == WorldType.ASMODAE) {
				if (player.getRace() == Race.ASMODIANS) {
					TeleportService.teleportTo(player, exit.getMap(), exit.getX(), exit.getY(), exit.getZ(), 0);
				}
			}
		}
		oldPosition = newPosition;
	}
}
