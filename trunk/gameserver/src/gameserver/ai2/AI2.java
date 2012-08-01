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
package gameserver.ai2;

import gameserver.ai2.event.AIEventType;
import gameserver.ai2.poll.AIAnswer;
import gameserver.ai2.poll.AIQuestion;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public interface AI2 {

	void onCreatureEvent(AIEventType event, Creature creature);

	void onCustomEvent(int eventId, Object... args);

	void onGeneralEvent(AIEventType event);

	/**
	 *  If already handled dialog return true.
	 */
	boolean onDialogSelect(Player player, int dialogId, int questId);

	void think();

	AIState getState();

	AISubState getSubState();

	String getName();

	boolean poll(AIQuestion question);

	AIAnswer ask(AIQuestion question);

	boolean isLogging();

	long getRemainigTime();

	int modifyDamage(int damage);
}
