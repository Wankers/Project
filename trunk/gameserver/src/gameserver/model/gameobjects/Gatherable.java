/*
 * This file is part of aion-unique <aion-unique.com>.
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
package gameserver.model.gameobjects;

import gameserver.controllers.GatherableController;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.gather.GatherableTemplate;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.world.WorldPosition;

/**
 * @author ATracer
 */
public class Gatherable extends VisibleObject {

	public Gatherable(SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, int objId,
		GatherableController controller) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());
		controller.setOwner(this);
	}

	@Override
	public String getName() {
		return objectTemplate.getName();
	}

	@Override
	public GatherableTemplate getObjectTemplate() {
		return (GatherableTemplate) objectTemplate;
	}

	@Override
	public GatherableController getController() {
		return (GatherableController) super.getController();
	}
}
