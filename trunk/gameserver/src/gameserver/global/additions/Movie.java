/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.global.additions;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.utils.PacketSendUtility;

/**
 *
 * @author Stan
 */
public class Movie {
    public static void useTalocFruit(Player player)
    {
      PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 10021, 438, false));
    }
    public static void die215488(Player player)
    {
      PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 10021, 437, false));
    }
  
}
