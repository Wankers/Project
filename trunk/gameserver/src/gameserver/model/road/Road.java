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
package gameserver.model.road;

import gameserver.controllers.RoadController;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.templates.road.RoadTemplate;
import gameserver.model.utils3d.Plane3D;
import gameserver.model.utils3d.Point3D;
import gameserver.utils.idfactory.IDFactory;
import gameserver.world.World;
import gameserver.world.knownlist.SphereKnownList;

/**
 * @author SheppeR
 */
public class Road extends VisibleObject {

	private RoadTemplate template = null;
	private String name = null;
	private Plane3D plane = null;
	private Point3D center = null;
	private Point3D p1 = null;
	private Point3D p2 = null;

	public Road(RoadTemplate template) {
		super(IDFactory.getInstance().nextId(), new RoadController(), null, null, World.getInstance().createPosition(
			template.getMap(), template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ(),
			(byte) 0, 0));

		((RoadController) getController()).setOwner(this);
		this.template = template;
		this.name = template.getName() == null ? "ROAD" : template.getName();
		this.center = new Point3D(template.getCenter().getX(), template.getCenter().getY(), template.getCenter().getZ());
		this.p1 = new Point3D(template.getP1().getX(), template.getP1().getY(), template.getP1().getZ());
		this.p2 = new Point3D(template.getP2().getX(), template.getP2().getY(), template.getP2().getZ());
		this.plane = new Plane3D(center, p1, p2);
		setKnownlist(new SphereKnownList(this, template.getRadius() * 2));
	}

	public Plane3D getPlane() {
		return plane;
	}

	public RoadTemplate getTemplate() {
		return template;
	}

	@Override
	public String getName() {
		return name;
	}

	public void spawn() {
		World w = World.getInstance();
		w.storeObject(this);
		w.spawn(this);
	}
}
