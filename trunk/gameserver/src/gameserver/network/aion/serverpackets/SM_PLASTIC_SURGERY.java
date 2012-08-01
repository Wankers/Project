package gameserver.network.aion.serverpackets;


import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author IlBuono
 */
public class SM_PLASTIC_SURGERY extends AionServerPacket {

	private int playerObjId;
	private byte check_ticket;
	private byte change_sex;

	public SM_PLASTIC_SURGERY(Player player, byte check_ticket, byte change_sex) {
		this.playerObjId = player.getObjectId();
		this.check_ticket = check_ticket;
		this.change_sex = change_sex;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(playerObjId);
		writeC(check_ticket);
		writeC(change_sex);
	}
}
