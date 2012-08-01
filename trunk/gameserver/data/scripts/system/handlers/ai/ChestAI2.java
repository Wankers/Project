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
package ai;

import static ch.lambdaj.Lambda.*;

import java.util.Collection;
import java.util.List;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.configs.main.GroupConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.chest.ChestTemplate;
import gameserver.model.templates.chest.KeyItem;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.drop.DropRegistrationService;
import gameserver.services.drop.DropService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import java.util.HashSet;

/**
 * @author ATracer, xTz
 */
@AIName("chest")
public class ChestAI2 extends ActionItemNpcAI2 {

	private ChestTemplate chestTemplate;

	@Override
	protected void handleDialogStart(final Player player) {
		chestTemplate = DataManager.CHEST_DATA.getChestTemplate(getNpcId());

		if (chestTemplate == null) {
			return;
		}
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		if (analyzeOpening(player)) {
			AI2Actions.dieSilently(this, player);
			Collection<Player> players = new HashSet<Player>();
			if (player.isInGroup2()) {
				for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
						players.add(member);
					}
				}
			}
			else if (player.isInAlliance2()) {
				for (Player member : player.getPlayerAlliance2().getOnlineMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
						players.add(member);
					}
				}
			}
			else {
				players.add(player);
			}
			DropRegistrationService.getInstance().registerDrop(getOwner(), player, maxFrom(players).getLevel(), players);
			DropService.getInstance().requestDropList(player, getObjectId());
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1111301));
		}
	}

	private boolean analyzeOpening(final Player player) {
		List<KeyItem> keyItems = chestTemplate.getKeyItem();
		int i = 0;
		for (KeyItem keyItem : keyItems) {
			if (keyItem.getItemId() == 0) {
				return true;
			}
			Item item = player.getInventory().getFirstItemByItemId(keyItem.getItemId());
			if (item != null) {
				if (item.getItemCount() != keyItem.getQuantity()) {
					int _i = 0;
					for (Item findedItem : player.getInventory().getItemsByItemId(keyItem.getItemId())) {
						_i += findedItem.getItemCount();
					}
					if (_i < keyItem.getQuantity()) {
						return false;
					}
				}
				i++;
				continue;
			}
			else {
				return false;
			}
		}
		if (i == keyItems.size()) {
			for (KeyItem keyItem : keyItems) {
				player.getInventory().decreaseByItemId(keyItem.getItemId(), keyItem.getQuantity());
			}
			return true;
		}
		return false;
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}
}
