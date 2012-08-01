package admincommands;

import commons.network.util.ThreadPoolManager;
import gameserver.global.additions.MessagerAddition;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.services.player.PlayerReviveService;
import gameserver.services.teleport.TeleportService;
import gameserver.taskmanager.tasks.TeamMoveUpdater;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * 
 * 
 * @author Dean
 */
public class Revenge extends ChatCommand {
        public int adminscore;
        public int playerscore;
        public boolean admIsWin1;
        public boolean playerIsWin1;
        public boolean admIsWin2;
        public boolean playerIsWin2;
        public boolean isDraw1;
        public boolean isDraw2;
	public Revenge() {
		super("revenge");
	}

	@Override
	public void execute(final Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //revenge <Character>");
			return;
		}

		final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		if (player == admin) {
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}
                String message = "Player ["+admin.getName()+"] want's to fight with you, do you accept the invitation?";
	        RequestResponseHandler responseHandler = new RequestResponseHandler(player){

	            public void acceptRequest(Creature requester, Player responder)
	            {
	                start(player, admin);
	                return;
	            }

	            public void denyRequest(Creature requester, Player responder){ return; }
	        };
	        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
	        if(requested){PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, message));return;}
	}
        
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //revenge <PlayerName>");
	}
        
        private void start(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: You have 15 seconds to prepare for battle!\nAfter 15 seconds you will be teleported to the enemy!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]The battle has begun!");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);
                      PlayerReviveService.revive(admin, 100, 100, false);
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]You lost the battle! Player" + admin.getName() + "won 1 point");
                      MessagerAddition.announce(admin, "[Revenge]Congratulations! You Won, you got one point into your account");
                      admIsWin1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  else if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]There was an tie, no one gets points");
                      MessagerAddition.announce(admin, "[Revenge]There was an tie, no one gets points");
                      isDraw1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                 else if(admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Congratulations You Won! You got one extra point!");
                      MessagerAddition.announce(admin, "[Revenge]You lose, your opponent gets the extra point!");
                      playerIsWin1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + player.getName() + "is out of battle!\nThe winner is "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + admin.getName() + "is out of battle!\nThe winner is "+ player.getName(), 0);
                  }				  
                  else if (admIsWin1 || playerIsWin1 || isDraw1)
                   {
                      stage2(player, admin);
                   }
                
		}

	}, 18000);  
            
        }
        private void stage2(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: You have 15 seconds to prepare for battle!\nAfter 15 seconds you will be teleported to the enemy!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]The battle has begun! Round 2");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);	  
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]You lost the battle! Player" + admin.getName() + "got one point");
                      MessagerAddition.announce(admin, "[Revenge]Congratulations! You Won, you got one point into your account");
                      admIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]There was an tie, no one gets points");
                      MessagerAddition.announce(admin, "[Revenge]There was an tie, no one gets points");
                      isDraw2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(admin.getLifeStats().getCurrentHp() == 0)
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Congratulations You Won! You got one extra point!");
                      MessagerAddition.announce(admin, "[Revenge]You lose, your opponent gets the extra point!");
                      playerIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
				  if(admIsWin2 || playerIsWin2 || isDraw2)
                  {
                     stage3(player, admin);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + player.getName() + "is out of battle!\nThe winner is "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + admin.getName() + "is out of battle!\nThe winner is "+ player.getName(), 0);
                  }
                  
                   
		}

	}, 18000);  
            
        }
       private void stage3(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: You have 15 seconds to prepare for battle!\nAfter 15 seconds you will be teleported to the enemy!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]The battle has begun! Round 3");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);	  
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]You lost the battle! Player" + admin.getName() + "got one point");
                      MessagerAddition.announce(admin, "[Revenge]Congratulations! You Won, you got one point into your account");
                      admIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]There was an tie, no one gets points");
                      MessagerAddition.announce(admin, "[Revenge]There was an tie, no one gets points");
                      isDraw2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(admin.getLifeStats().getCurrentHp() == 0)
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Congratulations You Won! You got one extra point!");
                      MessagerAddition.announce(admin, "[Revenge]You lose, your opponent gets the extra point!");
                      playerIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 210050000, 2945.6016f, 821.73755f, 359.375f, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
		 if(admIsWin2 || playerIsWin2 || isDraw2)
                  {
                     onEnd(player, admin);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + player.getName() + "is out of battle!\nThe winner is "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Player" + admin.getName() + "is out of battle!\nThe winner is "+ player.getName(), 0);
                  }
                  
                   
		}

	}, 19000);  
            
        }
        private void onEnd(final Player player, final Player admin)
        {
            MessagerAddition.announceAll("[Revenge]:The results of the battle " + admin.getName() + "vs" +player.getName(), 0);
             ThreadPoolManager.getInstance().schedule(new Runnable() {
             
	@Override
	public void run() {
              
		if(adminscore == playerscore)
                {
                    MessagerAddition.announceAll("[Revenge]: The results of the battle " + admin.getName() + "vs" +player.getName() + " <DRAW> !", 0);
                }
                if(adminscore > playerscore)
                {
                    MessagerAddition.announceAll("[Revenge]: The results of the battle " + admin.getName() + "vs" +player.getName() + " - " + admin.getName() + " wins!", 0);
                }
                else
                    MessagerAddition.announceAll("[Revenge]: The results of the battle " + admin.getName() + "vs" +player.getName() + " - " + player.getName() + " wins!", 0);
                TeleportService.moveToBindLocation(player, true);
                TeleportService.moveToBindLocation(admin, true);
                admIsWin1 = false;
                playerIsWin1 = false;
                admIsWin2 = false;
                playerIsWin2 = false;
                isDraw1 = false;
                isDraw2 = false;
	}
	}, 15000);
       
        }
        void restore(Player player)
        {
		player.getLifeStats().restoreMp();
		player.getLifeStats().restoreHp();
                player.getLifeStats().restoreFp();
        }

}