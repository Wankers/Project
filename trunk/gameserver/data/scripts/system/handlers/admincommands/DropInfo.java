package admincommands;

import gameserver.dataholders.DataManager;
import gameserver.model.drop.Drop;
import gameserver.model.drop.DropGroup;
import gameserver.model.drop.NpcDrop;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.npc.NpcTemplate;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Oliver
 */
public class DropInfo extends ChatCommand {

	public DropInfo() {
		super("dropinfo");
	}

	@Override
	public void execute(Player player, String... params) {
		NpcDrop npcDrop = null;
		if (params.length > 0) {
			int npcId = Integer.parseInt(params[0]);
			NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
			if (npcTemplate == null){
				PacketSendUtility.sendMessage(player, "Incorrect npcId: "+ npcId);
				return;
			}
			npcDrop = npcTemplate.getNpcDrop();
		}
		else {
			VisibleObject visibleObject = player.getTarget();

			if (visibleObject == null) {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}

			if (visibleObject instanceof Npc) {
				npcDrop = ((Npc)visibleObject).getNpcDrop();
			}
		}
		if (npcDrop == null){
			PacketSendUtility.sendMessage(player, "No drops for the selected NPC");
			return;
		}
		
		int count = 0;
		PacketSendUtility.sendMessage(player, "[Drop Info for the specified NPC]\n");
		for (DropGroup dropGroup: npcDrop.getDropGroup()){
			PacketSendUtility.sendMessage(player, "DropGroup: "+ dropGroup.getGroupName());
			for (Drop drop : dropGroup.getDrop()){
				PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + "	Rate: " + drop.getChance());
				count ++;
			}
		}
		PacketSendUtility.sendMessage(player, count + " drops available for the selected NPC");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
