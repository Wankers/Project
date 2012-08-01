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
package zone.pvpZones;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.player.PlayerReviveService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.knownlist.Visitor;
import gameserver.world.zone.SiegeZoneInstance;
import gameserver.world.zone.ZoneInstance;
import gameserver.world.zone.ZoneName;
import gameserver.world.zone.handler.AdvancedZoneHandler;


/**
 * @author MrPoke
 *
 */
public abstract class PvPZone implements AdvancedZoneHandler {

	@Override
	public void onEnterZone(Creature player, ZoneInstance zone) {
	}

	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone) {
	}

	@Override
	public boolean onDie(final Creature lastAttacker, Creature target, final ZoneInstance zone) {
		if (!(target instanceof Player)) {
			return false;
		}

		final Player player = (Player) target;

		Summon summon = player.getSummon();
		if (summon != null)
			summon.getController().release(UnsummonType.UNSPECIFIED);

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()), true);
		if (zone instanceof SiegeZoneInstance) {
			((SiegeZoneInstance) zone).doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player p) {
					PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_PvPZONE_OUT_MESSAGE(player.getName()));
				}
			});

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					PlayerReviveService.duelRevive(player);
					doTeleport(player, zone.getZoneTemplate().getName());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PvPZONE_MY_DEATH_TO_B(lastAttacker.getName()));
				}
			}, 5000);
		}
		return true;
	}

	protected abstract void doTeleport(Player player, ZoneName zoneName);
}
