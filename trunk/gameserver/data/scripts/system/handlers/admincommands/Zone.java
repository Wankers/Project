package admincommands;

import java.util.List;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.zone.ZoneInstance;
import gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class Zone extends ChatCommand {

	public Zone() {
		super("zone");
	}

	@Override
	public void execute(Player admin, String... params) {
		Creature target;
		if (admin.getTarget() == null || !(admin.getTarget() instanceof Creature))
			target = admin;
		else
			target = (Creature) admin.getTarget();
		if (params.length == 0) {
			List<ZoneInstance> zones = target.getPosition().getMapRegion().getZones(target);
			if (zones.isEmpty()) {
				PacketSendUtility.sendMessage(admin, target.getName()+" are out of any zone");
			}
			else {
				PacketSendUtility.sendMessage(admin, target.getName()+" are in zone: ");
				for (ZoneInstance zone : zones){
					PacketSendUtility.sendMessage(admin, zone.getAreaTemplate().getZoneName().name());
				}
			}
		}
		else if ("refresh".equalsIgnoreCase(params[0])) {
			admin.revalidateZones();
		}
		else if ("inside".equalsIgnoreCase(params[0])){
			try {
				ZoneName name = ZoneName.valueOf(params[1]);
				PacketSendUtility.sendMessage(admin, "isInsideZone: "+ admin.isInsideZone(name));
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Zone name missing!");
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
