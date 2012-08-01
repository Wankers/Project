/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package instance;

import java.util.ArrayList;
import java.util.List;

import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.Race;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_TRANSFORM;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.zone.ZoneInstance;

/**
 * @author xTz, Gigi
 */
@InstanceID(300230000)
public class KromedesTrialInstance extends GeneralInstanceHandler {

	private int transformId;
	private List<Integer> movies = new ArrayList<Integer>();
	private boolean isSpawned = false;

	@Override
	public void onEnterInstance(Player player) {
		transform(player);
		if (movies.contains(453)) {
			return;
		}
		transformId = player.getRace() == Race.ASMODIANS ? 202546 : 202545;
		sendMovie(player, 453);
		transform(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.setTransformedModelId(0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	private void transform(Player player) {
		player.setTransformedModelId(transformId);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
	}

	@Override
	public void onPlayerLogOut(Player player) {
		player.setTransformedModelId(0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		Storage storage = player.getInventory();
		switch (movieId) {
			case 454:
				Npc npc1 = player.getPosition().getWorldMapInstance().getNpc(730308);
				if (npc1 != null && MathUtil.isIn3dRange(player, npc1, 20)) {
					storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109));
					TeleportService.teleportTo(player, mapId, 687.56116f, 681.68225f, 200.28648f, 30);
				}
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		switch (zone.getAreaTemplate().getZoneName()) {
			case MANOR_ENTRANCE_300230000:
				sendMovie(player, 462);
				break;
			case KALIGA_TREASURY_300230000:
				if (!isSpawned) {
					isSpawned = true;
					Npc npc1 = instance.getNpc(217002);
					Npc npc2 = instance.getNpc(217000);
					Npc npc3 = instance.getNpc(216982);
					if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
						spawn(217005, 669.214f, 774.387f, 216.88f, (byte) 60);
						spawn(217001, 663.8805f, 779.1967f, 216.26213f, (byte) 60);
						spawn(217003, 663.0468f, 774.6116f, 216.26215f, (byte) 60);
						spawn(217004, 663.0468f, 770.03815f, 216.26212f, (byte) 60);
					}
					else {
						spawn(217006, 669.214f, 774.387f, 216.88f, (byte) 60);
					}
				}
				break;
		}
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead()); 
	}

	private void sendMovie(Player player, int movie) {
		if (!movies.contains(movie)) {
			movies.add(movie);
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
		}
	}

	@Override
	public void onInstanceDestroy() {
		movies.clear();
	}

}
