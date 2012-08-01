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
package ai.instance.tallocsHollow;

import ai.SummonerAI2;
import gameserver.ai2.AIName;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;

/**
 *
 * @author xTz
 */
@AIName("queenmosqua")
public class QueenMosquaAI2 extends SummonerAI2 {
	private boolean isHome = true;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome) {
			isHome = false;
			getPosition().getWorldMapInstance().getDoors().get(7).setOpen(false);
		}
	}

	@Override
	protected void handleBackHome() {
		isHome = true;
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);
		super.handleBackHome();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);

		Npc npc = instance.getNpc(700738);
		if (npc != null) {
			SpawnTemplate template = npc.getSpawn();
			spawn(700739, template.getX(), template.getY(), template.getZ(), template.getHeading(), 11);
			npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400476));
					Summon summon = player.getSummon();
					if (summon != null) {
						if (summon.getNpcId() == 799500 || summon.getNpcId() == 799501) {
							summon.getController().release(UnsummonType.UNSPECIFIED);
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 435));
						}
					}
				}
			});
		}
		npc.getController().onDelete();
	}

}
