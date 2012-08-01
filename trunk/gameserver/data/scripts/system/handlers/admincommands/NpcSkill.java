package admincommands;

import java.util.List;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.npcskill.NpcSkillTemplate;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class NpcSkill extends ChatCommand {

	public NpcSkill() {
		super("npcskill");
	}

	@Override
	public void execute(Player admin, String... params) {
		Npc target = null;
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Npc) {
			target = (Npc) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You should select a valid target first!");
			return;
		}

		StringBuilder strbld = new StringBuilder("-list of skills:\n");

		List<NpcSkillTemplate> list = DataManager.NPC_SKILL_DATA.getNpcSkillList(target.getNpcId()).getNpcSkills();

		for (NpcSkillTemplate skill : list)
			strbld.append("    level " + skill.getSkillLevel() + " of " + skill.getSkillid() + ".\n");
		showAllLines(admin, strbld.toString());
	}

	private void showAllLines(Player admin, String str) {
		int index = 0;
		String[] strarray = str.split("\n");

		while (index < strarray.length - 20) {
			StringBuilder strbld = new StringBuilder();
			for (int i = 0; i < 20; i++, index++) {
				strbld.append(strarray[index]);
				if (i < 20 - 1)
					strbld.append("\n");
			}
			PacketSendUtility.sendMessage(admin, strbld.toString());
		}
		int odd = strarray.length - index;
		StringBuilder strbld = new StringBuilder();
		for (int i = 0; i < odd; i++, index++)
			strbld.append(strarray[index] + "\n");
		PacketSendUtility.sendMessage(admin, strbld.toString());
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
