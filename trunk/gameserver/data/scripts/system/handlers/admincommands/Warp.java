package admincommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gameserver.configs.main.GeoDataConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.geo.GeoService;

/**
 * @author Source
 */
public class Warp extends ChatCommand {

	public Warp() {
		super("warp");
	}

	@Override
	public void execute(Player player, String... params) {
		String locS, first, last;
		float xF, yF, zF;
		locS = "";
		int mapL = 0;
		int layerI = -1;

		first = params[0];
		xF = Float.parseFloat(params[1]);
		yF = Float.parseFloat(params[2]);
		zF = Float.parseFloat(params[3]);
		last = params[4];

		Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{9})");
		Pattern l = Pattern.compile("(\\d)\\]");
		Matcher fm = f.matcher(first);
		Matcher lm = l.matcher(last);

		if (fm.find()) {
			locS = fm.group(1);
			mapL = Integer.parseInt(fm.group(2));
		}
		if (lm.find())
			layerI = Integer.parseInt(lm.group(1));

		zF = GeoService.getInstance().getZ(mapL, xF, yF);
		PacketSendUtility.sendMessage(player, "MapId (" + mapL + ")\n" + "x:" + xF + " y:" + yF + " z:" + zF + " l("
			+ layerI + ")");

		if (mapL == 400010000)
			PacketSendUtility.sendMessage(player, "Sorry you can't warp at abyss");
		else {
			TeleportService.teleportTo(player, mapL, xF, yF, zF, 0);
			PacketSendUtility.sendMessage(player, "You have successfully warp -> " + locS);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		if (!GeoDataConfig.GEO_ENABLE) {
			PacketSendUtility.sendMessage(player, "You must turn on geo in config to use this command!");
			return;
		}
		PacketSendUtility.sendMessage(player, "syntax //warp <@link>");
	}

}
