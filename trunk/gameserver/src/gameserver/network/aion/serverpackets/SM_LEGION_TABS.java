/*
 *  This file is part of Aion Extreme Emulator <aion-core.net>.
 *
 *  Aion Extreme Emulator is a free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Extreme Emulator. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.network.aion.serverpackets;

import commons.database.dao.DAOManager;
import gameserver.dao.LegionDAO;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import gameserver.model.gameobjects.player.Player;

import gameserver.model.team.legion.LegionHistory;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Simple, KID
 */
public class SM_LEGION_TABS extends AionServerPacket {

	private int page;
	private int tab;
	private Collection<LegionHistory> legionHistory;
	private Player player;
	private byte choose;

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory,  int tab) {
		this.legionHistory = legionHistory;
		this.page = 0;
		this.tab = tab;
		this.choose = 0;
	}

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int page, int tab) {
		this.legionHistory = legionHistory;
		this.page = page;
		this.tab = tab;
		this.choose = 0;
	}

    @Override
    protected void writeImpl(AionConnection con) {
        /**
         * If history size is less than page*8 return
         */
        if (legionHistory.size() < (page * 8)) {
            return;
        }

        // TODO: Formula's could use a refactor
        int hisSize = legionHistory.size() - (page * 8);

        if (page == 0 && legionHistory.size() > 8) {
            hisSize = 8;
        }
        if (page == 1 && legionHistory.size() > 16) {
            hisSize = 8;
        }
        if (page == 2 && legionHistory.size() > 24) {
            hisSize = 8;
        }

        writeD(legionHistory.size() > 24 ? 24 : legionHistory.size());
        writeD(page); // current page
        writeD(hisSize);

        int i = 0;
        for (LegionHistory history : legionHistory) {
            if (i >= (page * 8) && i <= (8 + (page * 8))) 	{
                writeD((int) (history.getTime().getTime() / 1000));
                writeH(history.getLegionHistoryType().getHistoryId());
                if (history.getName().length() > 0) {
                    writeS(history.getName());
                    if (history.getItem().length() > 0) {
                        if (history.getTime().getTime() + 20160000 < System.currentTimeMillis()) {
                            DAOManager.getDAO(LegionDAO.class).removeOldLegionWarehouseHistory(con.getActivePlayer().getLegion().getLegionId(), history.getTime());
                        }
                        writeB(new byte[66 - (history.getName().length() * 2 + 2)]);
                        writeS(history.getItem());
                        writeB(new byte[68 - (history.getItem().length() * 2 + 2)]);
                    } else {
                        int size = 134 - (history.getName().length() * 2 + 2);
                        writeB(new byte[size]);
                    }
                } else {
                    writeB(new byte[134]);
                }
            }
            i++;
            if (i >= (8 + (page * 8))) {
                break;
            }
        }
        writeH(tab);
    }
	
	private ByteBuffer render(String name) {
		ByteBuffer bb1 = ByteBuffer.allocate(64);
		for(char ch : name.toCharArray()) {
			bb1.putChar(ch);
		}
		bb1.rewind();
		return Charset.forName("UTF-16LE").encode(bb1.asCharBuffer());
	}
}
