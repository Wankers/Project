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

import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.flyring.FlyRing;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticDoor;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.storage.Storage;
import gameserver.model.templates.flyring.FlyRingTemplate;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.model.utils3d.Point3D;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xTz
 */
@InstanceID(300190000)
public class TalocsHollowInstance extends GeneralInstanceHandler {

	private List<Integer> movies = new ArrayList<Integer>();
	private Map<Integer, StaticDoor> doors;

	@Override
	public void onEnterInstance(Player player) {
		addItems(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}

	private void addItems(Player player) {
		QuestState qs1 = player.getQuestStateList().getQuestState(10021);
		QuestState qs2 = player.getQuestStateList().getQuestState(20021);
		if ((qs1 != null && qs1.getStatus() == QuestStatus.START) || (qs2 != null && qs2.getStatus() == QuestStatus.START)) {
			return;
		}
		switch (player.getRace()) {
			case ELYOS:
				ItemService.addItem(player, 160001286, 1);
				ItemService.addItem(player, 182206628, 1);
				break;
			case ASMODIANS:
				ItemService.addItem(player, 160001287, 1);
				ItemService.addItem(player, 182207603, 1);
				break;
		}
	}

	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		switch (player.getRace()) {
			case ELYOS:
				storage.decreaseByItemId(160001286, storage.getItemCountByItemId(160001286));
				storage.decreaseByItemId(182206628, storage.getItemCountByItemId(182206628));
				storage.decreaseByItemId(164000137, storage.getItemCountByItemId(164000137));
				if (player.getEffectController().hasAbnormalEffect(10251)) {
					player.getEffectController().removeEffect(10251);
				}
				break;
			case ASMODIANS:
				storage.decreaseByItemId(160001287, storage.getItemCountByItemId(160001287));
				storage.decreaseByItemId(182207603, storage.getItemCountByItemId(182207603));
				storage.decreaseByItemId(164000137, storage.getItemCountByItemId(164000137));
				if (player.getEffectController().hasAbnormalEffect(10252)) {
					player.getEffectController().removeEffect(10252);
				}
				break;
		}
	}

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 215467:
				openDoor(48);
				openDoor(49);
				break;
			case 215457:
				Npc newNpc = instance.getNpc(700633);
				if (newNpc != null) {
					newNpc.getController().onDelete();
				}
				break;
			case 700739:
				npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400477));
					}
				});
				SpawnTemplate template = npc.getSpawn();
				spawn(281817, template.getX(), template.getY(), template.getZ(), template.getHeading(), 9);
				npc.getController().onDelete();
				break;
			case 215488:
				Player player = npc.getAggroList().getMostPlayerDamage();
				if (player != null) {
					PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 10021, 437, false));
				}
				Npc newNpc2 = instance.getNpc(700740);
				if (newNpc2 != null) {
					SpawnTemplate template2 = newNpc2.getSpawn();
					spawn(700741, template2.getX(), template2.getY(), template2.getZ(), template2.getHeading(), 92);
					newNpc2.getController().onDelete();
				}
				spawn(799503, 548f, 811f, 1375f, (byte) 0);
				break;
		}
	}

	@Override
	public void onInstanceDestroy() {
		movies.clear();
		doors.clear();
	}

	private void sendMovie(Player player, int movie) {
		if (!movies.contains(movie)) {
			movies.add(movie);
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
		}
	}

	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}

	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(48).setOpen(true);
		doors.get(7).setOpen(true);
		spawnRings();
	}

	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("TALOCS_1", mapId,
				new Point3D(253.85039, 649.23535, 1171.8772),
				new Point3D(253.85039, 649.23535, 1177.8772),
				new Point3D(262.84872, 649.4091, 1171.8772), 8), instanceId);
		f1.spawn();
		FlyRing f2 = new FlyRing(new FlyRingTemplate("TALOCS_2", mapId,
				new Point3D(592.32275, 844.056, 1295.0966),
				new Point3D(592.32275, 844.056, 1301.0966),
				new Point3D(595.2305, 835.5387, 1295.0966), 8), instanceId);
		f2.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("TALOCS_1")) {
			sendMovie(player, 463);
		}
		else if(flyingRing.equals("TALOCS_2")) {
			sendMovie(player, 464);
		}
		return false;
	}

}