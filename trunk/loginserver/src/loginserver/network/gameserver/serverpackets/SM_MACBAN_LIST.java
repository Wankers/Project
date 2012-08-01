package loginserver.network.gameserver.serverpackets;

import java.util.Map;

import loginserver.controller.BannedMacManager;
import loginserver.model.base.BannedMacEntry;
import loginserver.network.gameserver.GsConnection;
import loginserver.network.gameserver.GsServerPacket;

/**
 * 
 * @author KID
 *
 */
public class SM_MACBAN_LIST extends GsServerPacket {

	private Map<String, BannedMacEntry> bannedList;
	public SM_MACBAN_LIST()
	{
		this.bannedList = BannedMacManager.getInstance().getMap();
	}
	
	@Override
	protected void writeImpl(GsConnection con) {
		writeC(9);
		writeD(bannedList.size());
		
		for(BannedMacEntry entry : bannedList.values())
		{
			writeS(entry.getMac());
			writeQ(entry.getTime().getTime());
			writeS(entry.getDetails());
		}
	}
}
