package gameserver.network.aion.clientpackets;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;

/**
 * @author dragoon112
 */
public class CM_REMOVE_ALTERED_STATE extends AionClientPacket {

	private int skillid;

	/**
	 * @param opcode
	 */
	public CM_REMOVE_ALTERED_STATE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/*
	 * (non-Javadoc)
	 * @see commons.network.packet.BaseClientPacket#readImpl()
	 */
	@Override
	protected void readImpl() {
		skillid = readH();

	}

	/*
	 * (non-Javadoc)
	 * @see commons.network.packet.BaseClientPacket#runImpl()
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		player.getEffectController().removeEffect(skillid);
	}

}
