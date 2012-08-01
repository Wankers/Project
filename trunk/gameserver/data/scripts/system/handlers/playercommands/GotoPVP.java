package playercommands;

import gameserver.model.gameobjects.player.Player;
import gameserver.services.instance.InstanceService;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.WorldMap;
import gameserver.world.WorldMapInstance;
import gameserver.world.WorldMapType;
import gameserver.model.Race;
import gameserver.world.World;
/**
 * Goto command
 * 
 * @author Dwarfpicker
 * @rework Imaginary
 */
public class GotoPVP extends ChatCommand{

	public GotoPVP() {
		super("go");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params == null || params.length < 1) {
			onFail(player, "Fail");
			return;
		}
		StringBuilder sbDestination = new StringBuilder();
		for(String p : params)
			sbDestination.append(p + " ");
		
		String destination = sbDestination.toString().trim();
		    if (player.getRace() == Race.ELYOS)
                    {
                        if (destination.equalsIgnoreCase("pvp"))
                                goTo(player, WorldMapType.SHIVA_PVP.getId(), 110, 2071, 371);              		
                        else
                                PacketSendUtility.sendMessage(player, "Impossible de trouver la destination !");
		    } 			          
		    else
		    {
                        if (destination.equalsIgnoreCase("pvp"))
                                goTo(player, WorldMapType.SHIVA_PVP.getId(), 520, 2661, 328);	
                        else
                                PacketSendUtility.sendMessage(player, "Impossible de trouver la destination !");

                    }
	}
	
	private static void goTo(final Player player, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
                
                if (worldId != player.getWorldId())
                {
                    if (destinationMap.isInstanceType())
                            TeleportService.teleportTo(player, worldId, getInstanceId(worldId, player), x, y, z, 3000 ,true);
                    else
                            TeleportService.teleportTo(player, worldId, x, y, z, 3000, true);
                    
                    PacketSendUtility.sendYellowMessageOnCenter(player, "Bienvenue sur la map PVP d'Aion Shiva ! Bon PVP !");
                }
                else
                {
                    PacketSendUtility.sendMessage(player, "Vous etes deja sur la map PVP ! \n"
                                                        + "Veuillez utiliser votre skill Retour pour partir.");
                } 
	}
	
	private static int getInstanceId(int worldId, Player player) {
		if (player.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, player.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, player);
		return newInstance.getInstanceId();
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntaxe : .go pvp");
	}
}