package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;


public class Job extends ChatCommand {

	public Job() {
		super("job");
	}

        @Override
	public void execute(Player player, String... params) {
            player.getSkillList().addSkill(player, 30002, 499); // Vita
            player.getSkillList().addSkill(player, 30003, 499); // Ether
            player.getSkillList().addSkill(player, 40001, 499); // Cuisine
            player.getSkillList().addSkill(player, 40002, 499); // Armes
            player.getSkillList().addSkill(player, 40003, 499); // Armure
            player.getSkillList().addSkill(player, 40004, 499); // Couture
            player.getSkillList().addSkill(player, 40007, 499); // Alchimie
            player.getSkillList().addSkill(player, 40008, 499); // Artisanat
	}
        
        @Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: .job ");
	}
}