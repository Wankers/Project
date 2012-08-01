package admincommands;

import java.io.IOException;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.network.aion.serverpackets.SM_DELETE;
import gameserver.network.aion.serverpackets.SM_TELEPORT_LOC;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Luno
 */
public class Delete extends ChatCommand {

	public Delete() {
		super("delete");
	}

	@Override
	public void execute(Player player, String... params) {

		VisibleObject cre = player.getTarget();
		if (!(cre instanceof Npc)) {
			PacketSendUtility.sendMessage(player, "Wrong target");
			return;
		}
		Npc npc = (Npc) cre;
		SpawnTemplate template = npc.getSpawn();
		if (template.hasPool()) {
			PacketSendUtility.sendMessage(player, "Can't delete pooled spawn template");
			return;
		}
		if (template instanceof SiegeSpawnTemplate) {
			PacketSendUtility.sendMessage(player, "Can't delete siege spawn template");
			return;
		}
                
		npc.getController().deleteWithBeam();
		try {
			DataManager.SPAWNS_DATA2.saveSpawn(player, npc, true);
		}
		catch (IOException e) {
			e.printStackTrace();
			PacketSendUtility.sendMessage(player, "Could not remove spawn");
			return;
		}
		PacketSendUtility.sendMessage(player, "Spawn removed");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
