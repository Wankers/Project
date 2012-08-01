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
package gameserver.controllers;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerPetsDAO;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Pet;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_PET;
import gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PetController extends VisibleObjectController<Pet> {

	@Override
	public void see(VisibleObject object) {

	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
	}

	public static class PetUpdateTask implements Runnable {

		private final Player player;
		private long startTime = 0;

		public PetUpdateTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (startTime == 0)
				startTime = System.currentTimeMillis();

			try {
				Pet pet = player.getPet();
				if (pet == null)
					throw new IllegalStateException("Pet is null");

				int currentPoints = 0;
				boolean saved = false;

				if (pet.getCommonData().getMoodPoints(false) < 10000) {
					if (System.currentTimeMillis() - startTime >= 60 * 1000) {
						currentPoints = pet.getCommonData().getMoodPoints(false);
						if (currentPoints == 10000) {
							PacketSendUtility.sendPacket(player, new SM_PET(pet, 4, 0, 0));
						}

						DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(pet.getCommonData());
						saved = true;
						startTime = System.currentTimeMillis();
					}
				}

				if (currentPoints < 10000) {
					PacketSendUtility.sendPacket(player, new SM_PET(pet, 4, 0, 0));
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_PET(pet, 3, 0, 0));
					// Save if it reaches 100% after player snuggles the pet, not by the scheduler itself
					if (!saved)
						DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(pet.getCommonData());
				}
			}
			catch (Exception ex) {
				player.getController().cancelTask(TaskId.PET_UPDATE);
			}
		}
	}

}
