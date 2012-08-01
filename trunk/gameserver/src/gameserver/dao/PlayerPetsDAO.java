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
package gameserver.dao;

import java.util.List;

import commons.database.dao.DAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PetCommonData;

/**
 * @author Xitanium, Kamui, Rolandas
 */
public abstract class PlayerPetsDAO implements DAO {

	@Override
	public final String getClassName() {
		return PlayerPetsDAO.class.getName();
	}

	public abstract void insertPlayerPet(PetCommonData petCommonData);

	public abstract void removePlayerPet(Player player, int petId);

	public abstract void updatePetName(PetCommonData petCommonData);

	public abstract List<PetCommonData> getPlayerPets(Player player);

	public abstract void setTime(Player player, int petId, long time);

	public abstract void setHungryLevel(Player player, int petId, int hungryLevel);

	public abstract boolean savePetMoodData(PetCommonData petCommonData);
}
