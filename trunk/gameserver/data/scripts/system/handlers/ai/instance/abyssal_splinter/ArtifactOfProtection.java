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
package ai.instance.abyssal_splinter;

import ai.ActionItemNpcAI2;

import gameserver.ai2.AIName;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;

/**
 * @author vlog
 */
@AIName("artifactofprotection")
public class ArtifactOfProtection extends ActionItemNpcAI2 {

	private boolean bossAlreadySpawned;

	@Override
	protected void handleSpawned() {
		bossAlreadySpawned = false;
		super.handleSpawned();
	}

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		QuestEnv env = new QuestEnv(getOwner(), player, 0, 0);
		QuestEngine.getInstance().onDialog(env);
		if (!isSpawned(216960) && !isSpawned(216952) && !bossAlreadySpawned) { // No bosses spawned
			if (!isSpawned(700955)) { // No Huge Aether Fragments spawned (all destroyed)
				spawn(216960, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
			}
			else {
				spawn(216952, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
			}
			bossAlreadySpawned = true;
		}
	}

	private boolean isSpawned(int npcId) {
		return !getPosition().getWorldMapInstance().getNpcs(npcId).isEmpty();
	}
}
