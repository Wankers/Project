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

import ai.AggressiveNpcAI2;
import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;

/**
 *
 * @author xTz
 */
@AIName("tameranikiki")
public class TamerAnikikiAI2 extends AggressiveNpcAI2{
	@Override
	protected void handleSpawned() {
		super.handleSpawned();

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.targetSelf(TamerAnikikiAI2.this);
				AI2Actions.useSkill(TamerAnikikiAI2.this, 18189);
			}

		}, 5000);
	}
}
