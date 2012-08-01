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
package gameserver.model.gameobjects;

import gameserver.controllers.PetController;
import gameserver.controllers.movement.MoveController;
import gameserver.controllers.movement.PetMoveController;
import gameserver.model.gameobjects.player.PetCommonData;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.pet.PetTemplate;
import gameserver.world.WorldPosition;

/**
 * @author ATracer
 */
public class Pet extends VisibleObject {

	private final Player master;
	private MoveController moveController;
	private final PetTemplate petTemplate;

	/**
	 * @param petTemplate
	 * @param controller
	 * @param commonData
	 * @param master
	 */
	public Pet(PetTemplate petTemplate, PetController controller, PetCommonData commonData, Player master) {
		super(commonData.getObjectId(), controller, null, commonData, new WorldPosition());
		controller.setOwner(this);
		this.master = master;
		this.petTemplate = petTemplate;
		this.moveController = new PetMoveController();
	}

	public Player getMaster() {
		return master;
	}

	public int getPetId() {
		return objectTemplate.getTemplateId();
	}

	@Override
	public String getName() {
		return objectTemplate.getName();
	}

	public final PetCommonData getCommonData() {
		return (PetCommonData) objectTemplate;
	}

	public final MoveController getMoveController() {
		return moveController;
	}

	public final PetTemplate getPetTemplate() {
		return petTemplate;
	}

}
