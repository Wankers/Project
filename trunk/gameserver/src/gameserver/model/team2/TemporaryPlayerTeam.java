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
package gameserver.model.team2;

import java.util.Collection;

import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.group.PlayerFilters;
import gameserver.network.aion.AionServerPacket;
import gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author ATracer
 */
public abstract class TemporaryPlayerTeam<TM extends TeamMember<Player>> extends GeneralTeam<Player, TM> {

	public TemporaryPlayerTeam(Integer objId) {
		super(objId);
	}

	/**
	 * Level of the player with lowest exp
	 */
	public abstract int getMinExpPlayerLevel();

	/**
	 * Level of the player with highest exp
	 */
	public abstract int getMaxExpPlayerLevel();

	@Override
	public Race getRace() {
		return getLeader().getObject().getRace();
	}

	@Override
	public void sendPacket(AionServerPacket packet) {
		applyOnMembers(new TeamMessageSender(packet, Predicates.<Player> alwaysTrue()));
	}

	@Override
	public void sendPacket(AionServerPacket packet, Predicate<Player> predicate) {
		applyOnMembers(new TeamMessageSender(packet, predicate));
	}

	@Override
	public final int onlineMembers() {
		return getOnlineMembers().size();
	}

	@Override
	public final Collection<Player> getOnlineMembers() {
		return filterMembers(PlayerFilters.ONLINE);
	}

	protected final void initializeTeam(TM leader) {
		setLeader(leader);
	}

	public static final class TeamMessageSender implements Predicate<Player> {

		private final AionServerPacket packet;
		private final Predicate<Player> predicate;

		public TeamMessageSender(AionServerPacket packet, Predicate<Player> predicate) {
			this.packet = packet;
			this.predicate = predicate;
		}

		@Override
		public boolean apply(Player player) {
			if (predicate.apply(player)) {
				PacketSendUtility.sendPacket(player, packet);
			}
			return true;
		}
	}

}
