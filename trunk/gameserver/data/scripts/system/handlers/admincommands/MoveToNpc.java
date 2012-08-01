package admincommands;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.npc.NpcTemplate;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke, lord_rex and ginho1
 */
public class MoveToNpc extends ChatCommand {

	public MoveToNpc() {
		super("movetonpc");
	}

	@Override
	public void execute(Player player, String... params) {
		int npcId = 0;
		String message = "";
		try {
			npcId = Integer.valueOf(params[0]);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			onFail(player, e.getMessage());
		}
		catch (NumberFormatException e) {
			String npcName = "";

			for(int i = 0; i < params.length; i++)
				npcName += params[i]+" ";
			npcName = npcName.substring(0, npcName.length() - 1);

			for(NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection()) {
				if(template.getName().equalsIgnoreCase(npcName)) {
					if(npcId == 0)
						npcId = template.getTemplateId();
					else {
						if(message.equals(""))
							message += "Found others ("+npcName+"): \n";
						message += "Id: "+template.getTemplateId()+"\n";
					}
				}
			}
			if(npcId == 0) {
				PacketSendUtility.sendMessage(player, "NPC " + npcName + " cannot be found");
			}
		}

		if(npcId > 0) {
			message = "Teleporting to Npc: "+npcId+"\n"+message;
			PacketSendUtility.sendMessage(player, message);
			TeleportService.teleportToNpc(player, npcId);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //movetonpc <npc_id|npc name>");
	}
}
