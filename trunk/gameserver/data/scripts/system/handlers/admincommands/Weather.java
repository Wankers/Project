package admincommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.WeatherService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.WorldMapType;

/**
 * Admin command allowing to change weathers of the world.
 * 
 * @author Kwazar
 */
public class Weather extends ChatCommand {

	public Weather() {
		super("weather");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length == 0 || params.length > 2) {
			onFail(admin, null);
			return;
		}

		String regionName = null;
		int weatherType = -1;

		regionName = new String(params[0]);

		if (params.length == 2) {
			try {
				weatherType = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "weather type parameter need to be an integer [0-8].");
				return;
			}
		}

		if (regionName.equals("reset")) {
			WeatherService.getInstance().resetWeather();
			return;
		}

		// Retrieving regionId by name
		WorldMapType region = null;
		for (WorldMapType worldMapType : WorldMapType.values()) {
			if (worldMapType.name().toLowerCase().equals(regionName.toLowerCase())) {
				region = worldMapType;
			}
		}

		if (region != null) {
			if (weatherType > -1 && weatherType < 9) {
				WeatherService.getInstance().changeRegionWeather(region.getId(), new Integer(weatherType));
			}
			else {
				PacketSendUtility.sendMessage(admin, "Weather type must be between 0 and 8");
				return;
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "Region " + regionName + " not found");
			return;
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player,
			"syntax //weather <regionName(poeta, ishalgen, etc ...)> <value(0->8)> OR //weather reset");
	}

}
