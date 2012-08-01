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
package ai.instance.steelRake;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;
import gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("feedingmantutu")
public class FeedingMantutuAI2 extends ActionItemNpcAI2 {

	private Npc boss;
	private Player frstPlayer;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		boss = instance.getNpc(215079);
	}

	@Override
	protected void handleDialogStart(Player player) {
		if (frstPlayer == null) {
			frstPlayer = player;
		}
		else {
			frstPlayer.getController().cancelActionItemNpc();
			player.getController().cancelActionItemNpc();
			frstPlayer = null;
			handleUseItemFinish(player);
			return;
		}

		player.getActionItemNpc().setCondition(6, 7, 10000);
		getKnownList().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111303));
				}
			});
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (frstPlayer != null) {
			getKnownList().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111305));
				}
			});
			return;
		}
		if (boss == null) {
			return;
		}
		switch (getNpcId()) {
			case 700527:
				boss.getEffectController().removeEffect(18180);
			case 700528:
				boss.getEffectController().removeEffect(18181);
				break;
		}
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleDialogFinish(Player player) {
		if (frstPlayer != null && frstPlayer.getObjectId() == player.getObjectId()) {
			frstPlayer = null;
		}
	}
}
