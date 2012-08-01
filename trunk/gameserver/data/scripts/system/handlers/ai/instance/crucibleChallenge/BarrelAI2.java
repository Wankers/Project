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
package ai.instance.crucibleChallenge;

import commons.utils.Rnd;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.poll.AIAnswer;
import gameserver.ai2.poll.AIAnswers;
import gameserver.ai2.poll.AIQuestion;

/**
 * @author xTz
 */
@AIName("barrel")
public class BarrelAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		super.handleDied();
		int npcId = 0;
		switch (getNpcId()) {
			case 218560:
				npcId = 218561;
				break;
			case 217840:
				npcId = 217841;
				break;
		}
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * 4);
		float y1 = (float) (Math.sin(Math.PI * direction) * 4);
		spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
		AI2Actions.deleteOwner(this);
	}

	 @Override
     protected AIAnswer pollInstance(AIQuestion question) {
        	switch (question) {
            	case SHOULD_DECAY:
                	return AIAnswers.NEGATIVE;
           	case SHOULD_RESPAWN:
                	return AIAnswers.NEGATIVE;
            	case SHOULD_REWARD:
                	return AIAnswers.NEGATIVE;
           	default:
                	return null;
        }
    }

}
