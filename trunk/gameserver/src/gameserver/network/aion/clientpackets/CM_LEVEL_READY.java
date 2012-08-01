/**
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.clientpackets;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Pet;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.StorageType;
import gameserver.model.templates.windstreams.Location2D;
import gameserver.model.templates.windstreams.WindstreamTemplate;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.*;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.services.WeatherService;
import gameserver.spawnengine.InstanceRiftSpawnManager;
import gameserver.spawnengine.RiftSpawnManager;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.WorldType;

/**
 * Client is saying that level[map] is ready.
 *
 * @author -Nemesiss-
 * @author Kwazar
 */
public class CM_LEVEL_READY extends AionClientPacket {

	public CM_LEVEL_READY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		sendPacket(new SM_PLAYER_INFO(activePlayer, false));
		sendPacket(new SM_MOTION(activePlayer.getObjectId(), activePlayer.getMotions().getActiveMotions()));
		activePlayer.getController().startProtectionActiveTask();

		WindstreamTemplate template = DataManager.WINDSTREAM_DATA.getStreamTemplate(activePlayer.getPosition().getMapId());
		Location2D location;
		if (template != null)
			for (int i = 0; i < template.getLocations().getLocation().size(); i++) {
				location = template.getLocations().getLocation().get(i);
				sendPacket(new SM_WINDSTREAM_ANNOUNCE(location.getBidirectional(), template.getMapid(), location.getId(),
						location.getBoost()));
			}
		location = null;
		template = null;

		/**
		 * Spawn player into the world.
		 */
		// If already spawned, despawn before spawning into the world
		if (activePlayer.isSpawned())
			World.getInstance().despawn(activePlayer);
		World.getInstance().spawn(activePlayer);

		activePlayer.getController().refreshZoneImpl();

		activePlayer.getController().updateNearbyQuests();

		/**
		 * Loading weather for the player's region
		 */
		WeatherService.getInstance().loadWeather(activePlayer);

		QuestEngine.getInstance().onEnterWorld(new QuestEnv(null, activePlayer, 0, 0));

		activePlayer.getController().onEnterWorld();
		// zone channel message
		sendPacket(new SM_SYSTEM_MESSAGE(1390122, activePlayer.getPosition().getInstanceId()));

		RiftSpawnManager.sendRiftStatus(activePlayer);
		InstanceRiftSpawnManager.sendInstanceRiftStatus(activePlayer);

		activePlayer.getEffectController().updatePlayerEffectIcons();
		sendPacket(SM_CUBE_UPDATE.cubeSize(StorageType.CUBE, activePlayer));

		if (activePlayer.isTeleporting())
			activePlayer.setIsTeleporting(false);

		Pet pet = activePlayer.getPet();
		if (pet != null)
			World.getInstance().spawn(pet);
	}

}