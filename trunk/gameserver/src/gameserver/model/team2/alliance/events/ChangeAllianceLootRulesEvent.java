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
package gameserver.model.team2.alliance.events;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import gameserver.model.team2.common.legacy.LootGroupRules;
import gameserver.network.aion.serverpackets.SM_ALLIANCE_INFO;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class ChangeAllianceLootRulesEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

	private final PlayerAlliance alliance;
	private final LootGroupRules lootGroupRules;

	public ChangeAllianceLootRulesEvent(PlayerAlliance alliance, LootGroupRules lootGroupRules) {
		this.alliance = alliance;
		this.lootGroupRules = lootGroupRules;
	}

	@Override
	public void handleEvent() {
		alliance.setLootGroupRules(lootGroupRules);
		alliance.applyOnMembers(this);
	}

	@Override
	public boolean apply(Player member) {
		PacketSendUtility.sendPacket(member, new SM_ALLIANCE_INFO(alliance));
		return true;
	}

}