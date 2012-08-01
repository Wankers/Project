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
package ai.instance.esoterrace;

import ai.SummonerAI2;
import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AIName;
import gameserver.model.ai.Percentage;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("wardensurama")
public class WardenSuramaAI2 extends SummonerAI2 {

	@Override
	protected void handleIndividualSpawnedSummons(Percentage percent) {
		spawnGeysers();
	}

	private void spawnGeysers() {
		spawn(282171, 1317.097656f, 1145.419556f, 53.203529f, (byte) 0, 595);
		spawn(282172, 1343.426147f, 1170.675293f, 53.203529f, (byte) 0, 596);
		spawn(282173, 1316.953979f, 1196.861328f, 53.203529f, (byte) 0, 598);
		spawn(282174, 1290.778442f, 1170.730957f, 53.203529f, (byte) 0, 597);
		spawn(282175, 1305.310059f, 1159.337769f, 53.203529f, (byte) 0, 721);
		spawn(282176, 1328.446289f, 1159.062500f, 53.203529f, (byte) 0, 718);
		spawn(282177, 1328.613770f, 1182.369873f, 53.203529f, (byte) 0, 722);
		spawn(282176, 1305.083130f, 1182.424927f, 53.203529f, (byte) 0, 719);

		getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400998));
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400997));
				}
			}
		});
		doSchedule();
	}

	private void deSpawnGeysers() {
		despawnNpc(282171);
		despawnNpc(282172);
		despawnNpc(282173);
		despawnNpc(282174);
		despawnNpc(282175);
		despawnNpc(282176);
		despawnNpc(282177);
		despawnNpc(282178);
	}

	private void despawnNpc(int npcId) {
		Npc npc = getPosition().getWorldMapInstance().getNpc(npcId);
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				deSpawnGeysers();
			}

		}, 13000);
	}

}
