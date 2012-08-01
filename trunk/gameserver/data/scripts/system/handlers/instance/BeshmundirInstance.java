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
package instance;

import java.util.Map;

import commons.utils.Rnd;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.instance.handlers.GeneralInstanceHandler;
import gameserver.instance.handlers.InstanceID;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticDoor;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;

/**
 * @author Gigi, nrg, oslo0322, xTz
 * TODO: Hard-/normal mode
 * TODO: AI for each boss
 * see http://raouooble.com/Beshmundir_Temple_Guide.html
 * see http://gameguide.na.aiononline.com/aion/Beshmundir+Temple+Walkthrough%3A+Hard+Mode
 *
 */
@InstanceID(300170000)
public class BeshmundirInstance extends GeneralInstanceHandler {

	private int macunbello = 0;
	private int kills;
	private Map<Integer,StaticDoor> doors;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 216175: // Pahraza
				if (Rnd.get(100) > 10) {
					spawn(216764, 1437.2672f, 1579.4656f, 305.82492f, (byte) 97);
					sendMsg("Mystery Box spawned");
				}
				break;
			case 216179:
			case 216181:
			case 216177:
				int chance = Rnd.get(100);

				if (chance > 90) {
					switch (npc.getNpcId()) {
						case 216179:
							spawn(216764, 1625.5829f, 1493.408f, 329.94492f, (byte) 67);
							break;
						case 216181:
							spawn(216764, 1633.7206f, 1429.6768f, 305.83493f, (byte) 59);
							break;
						case 216177:
							spawn(216764, 1500.8236f, 1586.5652f, 329.94492f, (byte) 88);
							break;
					}
					sendMsg("Congratulation: Mystery Box spawned!\nChance: " + chance);
				}
				else {
					sendMsg("Chance: " + chance);
					switch (npc.getObjectTemplate().getTemplateId()) {
						case 216179: // Narma
							spawn(216173, 1546.5916f, 1471.214f, 300.33008f, (byte) 84);
							sendMsg("Gatekeeper Rhapsharr spawned");
							break;
						case 216181: // Kramaka
							spawn(216171, 1403.51f, 1475.79f, 307.793f, (byte) 98);
							sendMsg("Gatekeeper Kutarrun spawned");
							break;
						case 216177: // Dinata
							spawn(216170, 1499.78f, 1507.1f, 300.33f, (byte) 0);
							sendMsg("Gatekeeper Darfall spawned");
					}
				}
				break;
			case 216583:
				spawn(799518, 936.0029f, 441.51712f, 220.5029f, (byte) 28);
				break;
			case 216584:
				spawn(799519, 791.0439f, 439.79608f, 220.3506f, (byte) 28);
				break;
			case 216585:
				spawn(799520, 820.70624f, 278.828f, 220.19385f, (byte) 55);
				break;
			case 216586:
				if (macunbello < 12) {
					spawn(216735, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // strongest macunbello
				}
				else if (macunbello < 14) {
					spawn(216734, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th strongest macunbello
				}
				else if (macunbello < 21) {
					spawn(216737, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th weakest macunbello
				}
				else {
					spawn(216245, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // weakest macunbello
				}
				macunbello = 0;
				sendPacket(new SM_QUEST_ACTION(0, 0));
				openDoor(467);
				break;
			case 799342:
				sendPacket(new SM_PLAY_MOVIE(0, 447));
				break;
			case 216238:
				openDoor(470);
				spawn(216159, 1357.0598f, 388.6637f, 249.26372f, (byte) 90);
				break;
			case 216246:
				openDoor(473);
				break;
			case 216739:
			case 216740:
				kills ++;
				if (kills < 10) {
					sendPacket(new SM_SYSTEM_MESSAGE(1400465));
				}
				else if (kills == 10) {
					sendPacket(new SM_SYSTEM_MESSAGE(1400470));
					spawn(216158, 1356.5719f, 147.76418f, 246.27373f, (byte) 91);
				}
				break;
			case 216158:
				openDoor(471);
				break;
			case 216263:
				sendPacket(new SM_PLAY_MOVIE(0, 443));
				// this is a safety Mechanism
				// super boss
				spawn(216264, 558.306f, 1369.02f, 224.795f, (byte) 70);
				// gate
				sendPacket(new SM_SYSTEM_MESSAGE(1400480));
				spawn(730275, 1611.1266f, 1604.6935f, 310.43274f, (byte) 17);
				break;
			case 216183:
			case 216264:
				spawn(730287, 552.5087f, 1333.2898f, 223.52919f, (byte) 37);
				break;
			case 216250:  // Dorakiki the Bold
				sendPacket(new SM_SYSTEM_MESSAGE(1400471));
				spawn(216527, 1161.859985f, 1213.859985f, 284.057007f, (byte) 110); // Lupukin: cat trader
				break;
			case 216206: 
			case 216207:
			case 216208:
			case 216209:
			case 216210:
			case 216211:
			case 216212: 
			case 216213:
				macunbello ++;
				switch (macunbello) {
					case 12:
						sendPacket(new SM_SYSTEM_MESSAGE(1400466));
						break;
					case 14:
						sendPacket(new SM_SYSTEM_MESSAGE(1400467));
						break;
					case 21:
						sendPacket(new SM_SYSTEM_MESSAGE(1400468));
						break;
				}
				break;
		}
	}

	private void sendMsg(final String str) { // to do system message
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendMessage(player, str);
			}

		});
	}

	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		switch (movieId) {
			case 443:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_BigOrb_Spawn);
				break;
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(535).setOpen(true);
	}

	private void openDoor(int doorId){
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceDestroy() {
		doors.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {

		Summon summon = player.getSummon();
		if (summon != null) {
			summon.getController().release(UnsummonType.UNSPECIFIED);
		}

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}
