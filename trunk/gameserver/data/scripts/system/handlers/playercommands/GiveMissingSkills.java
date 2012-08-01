package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.SkillLearnService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class GiveMissingSkills extends ChatCommand {

	public GiveMissingSkills() {
		super("skills");
	}

	@Override
	public void execute(Player player, String... params) {
			SkillLearnService.addMissingSkills(player);
                
                SkillLearnService.addMissingSkills(player);
                
                SkillLearnService.addMissingSkills(player);
                
                SkillLearnService.addMissingSkills(player);
                
                SkillLearnService.addMissingSkills(player);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntaxe : .skills");
	}
}
