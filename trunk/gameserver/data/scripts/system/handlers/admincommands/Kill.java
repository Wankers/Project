package admincommands;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author ATracer, Wakizashi
 */
public class Kill extends ChatCommand {

	public Kill() {
		super("kill");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length > 1) {
			PacketSendUtility.sendMessage(admin, "syntax //kill <target | all | <range>>");
			return;
		}

		if (params.length == 0) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			if (target instanceof Creature) {
				Creature creature = (Creature) target;
				creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
			}
		}
		else {
			int range = 0;
			if (params[0].equals("all"))
				range = -1;
			else {
				try {
					range = Integer.parseInt(params[0]);
				}
				catch (NumberFormatException ex) {
					PacketSendUtility.sendMessage(admin, "<range> must be a number.");
					return;
				}
			}
			for (VisibleObject obj : admin.getKnownList().getKnownObjects().values()) {
				if (obj instanceof Creature) {
					Creature creature = (Creature) obj;
					if (range < 0 || MathUtil.isIn3dRange(admin, obj, range))
						creature.getController().onAttack(admin, creature.getLifeStats().getMaxHp() + 1, true);
				}
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //kill <target | all | <range>>");
	}
}
