package admincommands;

import gameserver.model.Gender;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.WeddingService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;



/**
 * @author Made in Russia.
 *
 */
public class Wedding extends ChatCommand {

    /**
     * Constructor.
     */

    public Wedding() {
        super("wedding");
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 2) {
            PacketSendUtility.sendMessage(admin, "syntax //wedding <husb> <wife>");
            return;
        }

        Player playerHusb = World.getInstance().findPlayer(Util.convertName(params[0]));

        if (playerHusb == null) {
            PacketSendUtility.sendMessage(admin, "No such player.");
            return;
        }

        Player playerWife = World.getInstance().findPlayer(Util.convertName(params[1]));

        if (playerWife == null) {
            PacketSendUtility.sendMessage(admin, "No such player.");
            return;
        }
        
        if(playerHusb.getWedding() != null && playerHusb.getWedding().length() > 1){
        	PacketSendUtility.sendMessage(admin, playerHusb.getName() +" already married with "+ playerHusb.getWedding() +".");
        	return;
        }
        
        if(playerWife.getWedding() != null && playerWife.getWedding().length() > 1){
        	PacketSendUtility.sendMessage(admin, playerWife.getName() +" already married with "+ playerWife.getWedding() +"..");
        	return;
        }
        
        if(playerHusb.getGender() == Gender.FEMALE) {
        	PacketSendUtility.sendMessage(admin, "syntax //wedding <husb> <wife>");
            return;
        }
        
        if(playerWife.getGender() == Gender.MALE) {
        	PacketSendUtility.sendMessage(admin, "syntax //wedding <husb> <wife>");
            return;
        }
        WeddingService.start(playerHusb, playerWife);

    }
}