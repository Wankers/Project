package admincommands;

import java.util.Arrays;

import gameserver.configs.administration.CommandsConfig;
import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import gameserver.services.abyss.AbyssPointsService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Nemiroff, ATracer, IceReaper Date: 11.12.2009
 * @author Sarynth - Added AP
 */
public class Set extends ChatCommand {

	public Set() {
		super("set");
	}

	@Override
	public void execute(Player admin, String... params) {
		Player target = null;
		VisibleObject creature = admin.getTarget();

		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}

		if (params[1] == null) {
			PacketSendUtility.sendMessage(admin, "You should enter second params!");
			return;
		}
		String paramValue = params[1];

		if (params[0].equals("class")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			byte newClass;
			try {
				newClass = Byte.parseByte(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			PlayerClass oldClass = target.getPlayerClass();
			setClass(target, oldClass, newClass);
		}
		else if (params[0].equals("exp")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			long exp;
			try {
				exp = Long.parseLong(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			target.getCommonData().setExp(exp);
			PacketSendUtility.sendMessage(admin, "Set exp of target to " + paramValue);
		}
		else if (params[0].equals("ap")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int ap;
			try {
				ap = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			AbyssPointsService.setAp(target, ap);
			if (target == admin) {
				PacketSendUtility.sendMessage(admin, "Set your Abyss Points to " + ap + ".");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Set " + target.getName() + " Abyss Points to " + ap + ".");
				PacketSendUtility.sendMessage(target, "Admin set your Abyss Points to " + ap + ".");
			}
		}
		else if (params[0].equals("level")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int level;
			try {
				level = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			Player player = target;

			if (level <= 61)

				player.getCommonData().setLevel(level);
			PacketSendUtility.sendMessage(admin, "Set " + player.getCommonData().getName() + " level to " + level);
		}
		else if (params[0].equals("title")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int titleId;
			try {
				titleId = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			Player player = target;
			if (titleId <= 202)
				setTitle(player, titleId);
			PacketSendUtility.sendMessage(admin, "Set " + player.getCommonData().getName() + " title to " + titleId);

		}
	}

	private void setTitle(Player player, int value) {
		PacketSendUtility.sendPacket(player, new SM_TITLE_INFO(value));
		PacketSendUtility.broadcastPacket(player, (new SM_TITLE_INFO(player, value)));
		player.getCommonData().setTitleId(value);
	}

	private void setClass(Player player, PlayerClass oldClass, byte value) {
		PlayerClass playerClass = PlayerClass.getPlayerClassById(value);
		int level = player.getLevel();
		if (level < 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class after reach level 9");
			return;
		}
		if (Arrays.asList(1, 2, 4, 5, 7, 8, 10, 11).contains(oldClass.ordinal())) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return;
		}
		int newClassId = playerClass.ordinal();
		switch (oldClass.ordinal()) {
			case 0:
				if (newClassId == 1 || newClassId == 2)
					break;
			case 3:
				if (newClassId == 4 || newClassId == 5)
					break;
			case 6:
				if (newClassId == 7 || newClassId == 8)
					break;
			case 9:
				if (newClassId == 10 || newClassId == 11)
					break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return;
		}
		player.getCommonData().setPlayerClass(playerClass);
		player.getController().upgradePlayer();
		PacketSendUtility.sendMessage(player, "You have successfuly switched class");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //set <class|exp|ap|level|title>");
	}
}
