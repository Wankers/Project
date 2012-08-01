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
package gameserver.model.templates.zone;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zone")
public class ZoneTemplate {

	protected Points points;
	protected Cylinder cylinder;
	protected Sphere sphere;
	protected List<ZoneName> link;
	@XmlAttribute
	protected int priority;
	@XmlAttribute(name = "breath")
	protected boolean breath;
	@XmlAttribute
	protected ZoneName name;
	@XmlAttribute
	protected int mapid;
	@XmlAttribute(name = "siege_id")
	protected List<Integer> siegeId;
	@XmlAttribute(name = "area_type")
	protected AreaType areaType = AreaType.POLYGON;
	@XmlAttribute(name = "zone_type")
	protected ZoneClassName zoneType = ZoneClassName.SUB;

	/**
	 * Gets the value of the points property.
	 */
	public Points getPoints() {
		return points;
	}

	public Cylinder getCylinder() {
		return cylinder;
	}

	public Sphere getSphare() {
		return sphere;
	}

	public List<ZoneName> getLink()
	{
		if (link == null)
			link = new ArrayList();
		return link;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Gets the value of the name property.
	 */
	public ZoneName getName() {
		return name;
	}

	/**
	 * Gets the value of the mapid property.
	 */
	public int getMapid() {
		return mapid;
	}

	/**
	 * @return the breath
	 */
	public boolean isBreath() {
		return breath;
	}

	/**
	 * @return the type
	 */
	public AreaType getAreaType() {
		return areaType;
	}

	/**
	 * @return the zoneType
	 */
	public ZoneClassName getZoneType() {
		return zoneType;
	}

	public List<Integer> getSiegeId() {
		return siegeId;
	}

}
