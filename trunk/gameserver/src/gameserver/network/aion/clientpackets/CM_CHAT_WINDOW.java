package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_CHAT_WINDOW;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * @author prix
 */
public class CM_CHAT_WINDOW extends AionClientPacket {

	private String playerName;
	@SuppressWarnings("unused")
	private int unk;

	public CM_CHAT_WINDOW(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		playerName = readS();
		unk = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Player target = World.getInstance().findPlayer(playerName);

		if (target == null)
			return;

		PacketSendUtility.sendPacket(player, new SM_CHAT_WINDOW(target));
	}
}
