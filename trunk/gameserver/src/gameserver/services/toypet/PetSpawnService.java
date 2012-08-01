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
package gameserver.services.toypet;

import commons.database.dao.DAOManager;
import gameserver.configs.main.PeriodicSaveConfig;
import gameserver.controllers.PetController;
import gameserver.dao.PlayerPetsDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Pet;
import gameserver.model.gameobjects.player.PetCommonData;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.pet.PetFunction;
import gameserver.model.templates.pet.PetFunctionType;
import gameserver.model.templates.pet.PetTemplate;
import gameserver.network.aion.serverpackets.SM_PET;
import gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import gameserver.spawnengine.VisibleObjectSpawner;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import java.sql.Timestamp;

/**
 * @author ATracer
 */
public class PetSpawnService {

	/**
	 * @param player
	 * @param petId
	 */
	public static void summonPet(Player player, int petId, boolean isManualSpawn) {
		PetCommonData lastPetCommonData;

		if (player.getPet() != null) {
			if (player.getPet().getPetId() == petId) {
				PacketSendUtility.broadcastPacket(player, new SM_PET(3, player.getPet()), true);
				return;
			}

			lastPetCommonData = player.getPet().getCommonData();
			dismissPet(player, isManualSpawn);
		} else {
			lastPetCommonData = player.getPetList().getLastUsedPet();
		}

		if(lastPetCommonData != null) {
			// reset mood if other pet is spawned
			lastPetCommonData.clearMoodStatistics();
		}

		player.getController().addTask(
			TaskId.PET_UPDATE,
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new PetController.PetUpdateTask(player),
				PeriodicSaveConfig.PLAYER_PETS * 1000, PeriodicSaveConfig.PLAYER_PETS * 1000));

		Pet pet = VisibleObjectSpawner.spawnPet(player, petId);
		//It means serious error or cheater - why its just nothing say "null"?
		if (pet != null) {
			sendWhInfo(player, petId);

			if(System.currentTimeMillis() - pet.getCommonData().getDespawnTime().getTime() > 10 * 60 * 1000) {
				// reset mood if pet was despawned for longer than 10 mins.
				player.getPet().getCommonData().clearMoodStatistics();
			}

			player.getPetList().setLastUsedPetId(petId);
		}
	}

	/**
	 * @param player
	 * @param petId
	 */
	private static void sendWhInfo(Player player, int petId) {
		PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		PetFunction pf = petTemplate.getWarehouseFunction();
		if (pf != null) {
			int itemLocation = 0;

			switch (pf.getSlots()) {
				case 6:
					itemLocation = 32;
					break;
				case 12:
					itemLocation = 33;
					break;
				case 18:
					itemLocation = 34;
					break;
				case 24:
					itemLocation = 35;
					break;
			}

			if (itemLocation != 0) {
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(itemLocation).getItemsWithKinah(),
					itemLocation, 0, true, player));

				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, itemLocation, 0, false, player));
			}
		}
	}

	/**
	 * @param player
	 * @param isManualDespawn
	 */
	public static void dismissPet(Player player, boolean isManualDespawn) {
		Pet toyPet = player.getPet();
		if (toyPet != null) {
			toyPet.getCommonData().setCancelFood(true);
			if (toyPet.getPetTemplate().ContainsFunction(PetFunctionType.FOOD)) {
				DAOManager.getDAO(PlayerPetsDAO.class).setHungryLevel(player, toyPet.getPetId(),
					toyPet.getCommonData().getHungryLevel());
			}

			player.getController().cancelTask(TaskId.PET_UPDATE);

			// TODO needs for pet teleportation
			if (isManualDespawn)
				toyPet.getCommonData().setDespawnTime(new Timestamp(System.currentTimeMillis()));

			toyPet.getCommonData().savePetMoodData();

			player.setToyPet(null);
			toyPet.getController().delete();
		}

	}
}
