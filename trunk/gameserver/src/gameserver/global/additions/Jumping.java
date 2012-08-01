/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.global.additions;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;

/**
 *
 * @author Stan
 */
public class Jumping {
    
    public static void crucibleEl(Player player)
    {
       TeleportService.teleportTo(player, 120080000, 542, 202, 93.479576f, 3000, true);
    }
    public static void crucibleAsm(Player player)
    {
        
    }
}
