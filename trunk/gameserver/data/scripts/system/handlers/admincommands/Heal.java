package admincommands;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Mrakobes, Loxo
 */
public class Heal extends ChatCommand {

	public Heal() {
		super("heal");
	}

	@Override
	public void execute(Player player, String... params) {
		VisibleObject target = player.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}

		Creature creature = (Creature) target;
		if (params == null || params.length < 1) {
			if (target instanceof Creature) {
				creature.getLifeStats().increaseHp(TYPE.HP, creature.getLifeStats().getMaxHp() + 1);
				creature.getLifeStats().increaseMp(TYPE.MP, creature.getLifeStats().getMaxMp() + 1);
				creature.getEffectController().removeEffect(8291);
				PacketSendUtility.sendMessage(player, creature.getName() + " has been refreshed !");
			}
		}
		else if (params[0].equals("dp") && target instanceof Player) {
			Player targetPlayer = (Player) creature;
			targetPlayer.getCommonData().setDp(targetPlayer.getGameStats().getMaxDp().getCurrent());
			PacketSendUtility.sendMessage(player, creature.getName() + " is now full of DP !");
		}
		else {
			onFail(player, null);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		String syntax = "//heal : Full HP and MP\n" + "//heal dp : Full DP, must be used on a player !";
		PacketSendUtility.sendMessage(player, syntax);
	}
}
