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
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;

/**
 * @author Luzien
 */
@AIName("divineartifact")
public class DivineArtifactAI2 extends AggressiveNpcAI2 {

	private boolean cooldown = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!cooldown) {
			AI2Actions.useSkill(this, 18915);
			setCD();
		}
	}
		
		private void setCD() { //ugly hack to prevent overflow TODO: remove on AI improve
			cooldown = true;
			
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					cooldown = false;
				}
			}, 1000);
	}
}
