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
package gameserver.model.gameobjects.player;

import java.util.HashSet;
import java.util.Set;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerRecipesDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.templates.recipe.RecipeTemplate;
import gameserver.network.aion.serverpackets.SM_LEARN_RECIPE;
import gameserver.network.aion.serverpackets.SM_RECIPE_DELETE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class RecipeList {

	private Set<Integer> recipeList = new HashSet<Integer>();

	public RecipeList(HashSet<Integer> recipeList) {
		this.recipeList = recipeList;
	}
	
	public RecipeList() {}

	public Set<Integer> getRecipeList() {
		return recipeList;
	}

	public void addRecipe(Player player, RecipeTemplate recipeTemplate) {
		int recipeId = recipeTemplate.getId();
		if (!player.getRecipeList().isRecipePresent(recipeId)) {
			if(DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(player.getObjectId(), recipeId)) {
				recipeList.add(recipeId);
				PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(recipeId));
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_LEARN(recipeTemplate.getNameid()));
			}
		}
	}
	
	public void addRecipe(int playerId, int recipeId) {
		if(DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(playerId, recipeId)) {
			recipeList.add(recipeId);
		}
	}

	public void deleteRecipe(Player player, int recipeId) {
		if (recipeList.contains(recipeId)) {
			if(DAOManager.getDAO(PlayerRecipesDAO.class).delRecipe(player.getObjectId(), recipeId)) {
				recipeList.remove(recipeId);
				PacketSendUtility.sendPacket(player, new SM_RECIPE_DELETE(recipeId));
			}
		}
	}

	public void autoLearnRecipe(Player player, int skillId, int skillLvl) {
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getAutolearnRecipes(player.getRace(), skillId, skillLvl)) {
			player.getRecipeList().addRecipe(player, recipe);
		}
	}

	public boolean isRecipePresent(int recipeId) {
		return recipeList.contains(recipeId);
	}

	public int size() {
		return this.recipeList.size();
	}
}