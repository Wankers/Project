/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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
 *  along with Aion Extreme Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.empyreanCrucible;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.item.ItemService;

/**
 *
 * @author xTz
 */
@AIName("empworthinessticketbox")
public class EmpyreanWorthinessTicketBoxAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		ItemService.addItem(player, 186000124, 1);
		AI2Actions.deleteOwner(this);
	}
}
