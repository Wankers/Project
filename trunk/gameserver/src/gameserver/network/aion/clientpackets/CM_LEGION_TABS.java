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
package gameserver.network.aion.clientpackets;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team.legion.LegionHistory;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_LEGION_TABS;
import gameserver.utils.PacketSendUtility;

/**
 * @author Simple
 */
public class CM_LEGION_TABS extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_LEGION_TABS.class);

	private int page;
	private int tab;

	public CM_LEGION_TABS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		page = readD();
		tab = readC();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		
		if(activePlayer.getLegion() != null) {
			Collection<LegionHistory> history = activePlayer.getLegion().getLegionHistory();
			Collection<LegionHistory> warehouseHistory = activePlayer.getLegion().getLegionWarehouseHistory();

        /**
         * Max page is 3 for legion history
         */
        if (page > 3) {
            return;
        }

        switch (tab) {
            /**
             * History Tab
             */
            case 0:
                /**
                 * If history size is less than page*8 return
                 */
                if (history.size() < page * 8) {
                    return;
                }
                log.debug("Requested History Tab Page: " + page);
                if (!history.isEmpty()) {
                    PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_TABS(history, page, tab));
                }
                break;
            /**
             * Reward Tab
             */
            case 1:
                log.debug("Requested Reward Tab Page: " + page);
                break;
            /**
             * Add Warehouse history
             */
            case 2:
                /**
                 * If warehouse history size is less than page*8 return
                 */
                if (warehouseHistory.size() < page * 8) {
                    return;
                }
                log.debug("Requested Warehouse history Page: " + page);
                if (!warehouseHistory.isEmpty()) {
                    PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_TABS(warehouseHistory, page, tab));
                }
                break;
			}
		}
		else
			log.warn("Player "+activePlayer.getName()+" was requested null legion");
	}
}
