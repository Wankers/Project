package admincommands;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.SkillEngine;
import gameserver.skillengine.model.SkillTemplate;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Source
 */
public class UseSkill extends ChatCommand {

	public UseSkill() {
		super("useskill");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length > 3) {
			onFail(admin, null);
			return;
		}

		VisibleObject target = admin.getTarget();

		int skillId = 0;
		int skillLevel = 0;

		try {
			skillId = Integer.parseInt(params[0]);
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Parameters need to be an integer.");
			return;
		}

		if (target == null || !(target instanceof Creature)) {
			PacketSendUtility.sendMessage(admin, "You must select a target!");
			return;
		}
		if (target.getTarget() == null || !(target.getTarget() instanceof Creature)) {
			PacketSendUtility.sendMessage(admin, "Target must select some creature!");
			return;
		}

		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (skillTemplate == null) {
			PacketSendUtility.sendMessage(admin, "No skill template id:" + skillId);
			return;
		}
		
		if (params.length == 3 && params[2].equalsIgnoreCase("true")) {
			SkillEngine.getInstance().applyEffectDirectly(skillId, (Creature)target, (Creature)target.getTarget(), 2000);
			PacketSendUtility.sendMessage(admin, "applyingskillid:" + skillId);
		}
		else
			((Creature) target).getController().useSkill(skillId, skillLevel);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //skill <skillId> <skillLevel>");
	}
}
