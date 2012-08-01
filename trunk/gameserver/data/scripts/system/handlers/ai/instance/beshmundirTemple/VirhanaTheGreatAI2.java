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
package ai.instance.beshmundirTemple;

import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;

import ai.AggressiveNpcAI2;


/**
 * @author Luzien
 *
 */
@AIName("virhana")
public class VirhanaTheGreatAI2 extends AggressiveNpcAI2 {

	private boolean isStart;
	private int count;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (!isStart){
			isStart = true;
			scheduleRage();
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isStart = false;
	}

	private void scheduleRage() {
		if (isAlreadyDead() || !isStart) {
			return;
		}
		AI2Actions.useSkill(this, 19121);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				startRage();
			}

		}, 70000);
	}

	private void startRage() {
		if (isAlreadyDead() || !isStart) {
			return;
		}
		if (count < 12) {
			AI2Actions.useSkill(this, 18897);
			count++;

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					startRage();
				}

			}, 10000);
		}
		else { //restart after a douzen casts
			count = 0;
			scheduleRage();
		}
	}
}
