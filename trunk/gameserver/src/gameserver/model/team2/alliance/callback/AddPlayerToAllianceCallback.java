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
package gameserver.model.team2.alliance.callback;

import commons.callbacks.Callback;
import commons.callbacks.CallbackResult;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.alliance.PlayerAlliance;

/**
 * @author ATracer
 */
@SuppressWarnings("rawtypes")
public abstract class AddPlayerToAllianceCallback implements Callback {

	@Override
	public CallbackResult beforeCall(Object obj, Object[] args) {
		onBeforePlayerAddToAlliance((PlayerAlliance) args[0], (Player) args[1]);
		return CallbackResult.newContinue();
	}

	@Override
	public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
		onAfterPlayerAddToAlliance((PlayerAlliance) args[0], (Player) args[1]);
		return CallbackResult.newContinue();
	}

	@Override
	public Class<? extends Callback> getBaseClass() {
		return AddPlayerToAllianceCallback.class;
	}

	public abstract void onBeforePlayerAddToAlliance(PlayerAlliance alliance, Player player);
	
	public abstract void onAfterPlayerAddToAlliance(PlayerAlliance alliance, Player player);

}
