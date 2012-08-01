package admincommands;

import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.controllers.SummonController;
import gameserver.controllers.effect.EffectController;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.stats.SummonStatsTemplate;
import gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import gameserver.network.aion.serverpackets.SM_TRANSFORM_IN_SUMMON;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author GosthMan
 *
 */
 @AIName("tallocssummon")
public class BeMyPet extends ChatCommand {

    public BeMyPet() {
        super("bemypet");
    }

	private boolean isTransformed = false;
		
    @Override
    public void execute(Player player, String... params) {
		String help = "Syntax: //bemypet < enable | cancel >\n"
			+ "Enable - Transform an NPC in your pet.\n"
			+ "Cancel - Cancel . Unsummon NPC pet.";

		if (params.length != 1) {
			onFail(player, null);
			return;
		}

        if (player.getTarget() == null) {
            PacketSendUtility.sendMessage(player, "Please select target");
            return;
        }


        VisibleObject obj = player.getTarget();
		String output = "You have enslaved NPC " + obj + ".";

        if (!(obj instanceof Npc)) {
            PacketSendUtility.sendMessage(player, "Target must be only Npc");
            return;
        }
        Npc npc = (Npc) obj;

        if (npc.isAggressiveTo(player)) {
            PacketSendUtility.sendMessage(player, "Npc must be frendly for you");
            return;
        }

        if (!((NpcAI2) npc.getAi2()).isMoveSupported()) {
            PacketSendUtility.sendMessage(player, "Npc must be Move Supported");
            return;
        }

        if (player.getSummon() != null) {
            player.getSummon().getController().release(SummonController.UnsummonType.COMMAND);
        }
		
		if (params[0].equals("enable")) {
        Summon summon = new Summon(npc.getObjectId(), new SummonController(), npc.getSpawn(), npc.getObjectTemplate(), npc.getObjectTemplate().getLevel());
        player.setSummon(summon);
        summon.setMaster(player);
        summon.setTarget(player.getTarget());
        summon.setKnownlist(npc.getKnownList());
        summon.setEffectController(new EffectController(summon));
        summon.setPosition(npc.getPosition());
        summon.setLifeStats(npc.getLifeStats());
        PacketSendUtility.sendPacket(player, new SM_TRANSFORM_IN_SUMMON(player, npc.getObjectId()));
        PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, 38, 0));
		isTransformed = true;
		}

		else if (params[0].equals("cancel")) {
            player.getSummon().getController().release(SummonController.UnsummonType.COMMAND);
			isTransformed = false;
			output = "You have released NPC " + obj + ".";
		}

		else if (params[0].equals("help")) {
			PacketSendUtility.sendMessage(player, help);
			return;
		}

		else {
			onFail(player, null);
			return;
		}
		
		PacketSendUtility.sendMessage(player, output);
		}
		
		@Override
		public void onFail(Player player, String message) {
		String syntax = "Syntax: //bemypet < enable | cancel >\nIf you're unsure about what you want to do, type //bemypet help";
		PacketSendUtility.sendMessage(player, syntax);
    }
}
