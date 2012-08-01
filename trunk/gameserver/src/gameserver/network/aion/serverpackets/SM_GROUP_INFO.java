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

import gameserver.model.team2.common.legacy.LootGroupRules;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author Lyahim, ATracer, xTz
 */
public class SM_GROUP_INFO extends AionServerPacket {
	private LootGroupRules lootRules;
	private int groupId;
	private int leaderId;
	private int groupType; // deafult 0x3F; autogroup 0x02

	public SM_GROUP_INFO(PlayerGroup group) {
		groupId = group.getObjectId();
		leaderId = group.getLeader().getObjectId();
		lootRules = group.getLootGroupRules();
		groupType = group.getGroupType();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(groupId);
		writeD(leaderId);
		writeD(lootRules.getLootRule().getId());
		writeD(lootRules.getMisc());
		writeD(lootRules.getCommonItemAbove());
		writeD(lootRules.getSuperiorItemAbove());
		writeD(lootRules.getHeroicItemAbove());
		writeD(lootRules.getFabledItemAbove());
		writeD(lootRules.getEthernalItemAbove());
		writeD(lootRules.getAutodistribution().getId());
		writeD(0x02);
		writeC(0x00);
		writeD(groupType);
		writeD(0x00);
		writeH(0x00);
	}
}