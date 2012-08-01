package gameserver.services;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.recipe.RecipeTemplate;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class RecipeService {
	public static RecipeTemplate validateNewRecipe(Player player, int recipeId) {
		if (player.getRecipeList().size() >= 1600) {
			PacketSendUtility.sendMessage(player, "You are unable to have more than 1600 recipes at the same time.");
			return null;
		}

		RecipeTemplate template = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		if (template == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_RECIPEITEM_CANT_USE_NO_RECIPE);
			return null;
		}

		if (template.getRace() != player.getRace()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFTRECIPE_RACE_CHECK);
			return null;
		}

		if (player.getRecipeList().isRecipePresent(recipeId)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_LEARNED_ALREADY);
			return null;
		}

		if (!player.getSkillList().isSkillPresent(template.getSkillid())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_CANT_LEARN_SKILL(DataManager.SKILL_DATA.getSkillTemplate(template.getSkillid()).getNameId()));
			return null;
		}

		if (template.getSkillpoint() > player.getSkillList().getSkillLevel(template.getSkillid())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CRAFT_RECIPE_CANT_LEARN_SKILLPOINT);
			return null;
		}

		return template;
	}

	public static boolean addRecipe(Player player, int recipeId, boolean useValidation) {
		RecipeTemplate template = null;
		if(useValidation)
			template = validateNewRecipe(player, recipeId);
		else
			template = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		
		if (template == null)
			return false;

		player.getRecipeList().addRecipe(player, template);
		return true;
	}

}
