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

import gameserver.controllers.StaticObjectController;
import gameserver.model.EmotionType;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.model.templates.staticdoor.StaticDoorTemplate;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.utils.PacketSendUtility;


/**
 * @author MrPoke
 *
 */
public class StaticDoor extends StaticObject {

	private boolean open = false;
	/**
	 * @param objectId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public StaticDoor(int objectId, StaticObjectController controller, SpawnTemplate spawnTemplate,
		StaticDoorTemplate objectTemplate) {
		super(objectId, controller, spawnTemplate, objectTemplate);
	}

	
	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	
	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
		PacketSendUtility.broadcastPacket(this, new SM_EMOTION(this.getSpawn().getStaticId(), open ?  EmotionType.OPEN_DOOR : EmotionType.CLOSE_DOOR));
	}
	
	@Override
	public StaticDoorTemplate getObjectTemplate() {
		return (StaticDoorTemplate) super.getObjectTemplate();
	}
}
