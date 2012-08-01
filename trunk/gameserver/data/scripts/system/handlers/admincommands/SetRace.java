package admincommands;

import gameserver.model.Race;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.WorldMapType;

/**
 * @author Centisgood(Barahime)
 */
public class SetRace extends ChatCommand {

	public SetRace() {
		super("setrace");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax: //setrace <elyos | asmodians>");
			return;
		}

		VisibleObject visibleobject = admin.getTarget();

		if (visibleobject == null || !(visibleobject instanceof Player)) {
			PacketSendUtility.sendMessage(admin, "Wrong select target.");
			return;
		}

		Player target = (Player) visibleobject;
		if (params[0].equalsIgnoreCase("elyos")) {
			target.getCommonData().setRace(Race.ELYOS);
			TeleportService.teleportTo(target, WorldMapType.SANCTUM.getId(), 1322, 1511, 568, 0);
			PacketSendUtility.sendMessage(target, "Has been moved to Sanctum.");
		}
		else if (params[0].equalsIgnoreCase("asmodians")) {
			target.getCommonData().setRace(Race.ASMODIANS);
			TeleportService.teleportTo(target, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195, 0);
			PacketSendUtility.sendMessage(target, "Has been moved to Pandaemonium");
		}
		PacketSendUtility.sendMessage(admin,
			target.getName() + " race has been changed to " + params[0] + ".\n" + target.getName()
				+ " has been moved to town.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax: //setrace <elyos | asmodians>");
	}
}
