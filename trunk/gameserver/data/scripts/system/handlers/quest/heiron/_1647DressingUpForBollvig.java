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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.heiron;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 * @reworked vlog
 */
public class _1647DressingUpForBollvig extends QuestHandler {

	private final static int questId = 1647;

	public _1647DressingUpForBollvig() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(790019).addOnQuestStart(questId);
		qe.registerQuestNpc(790019).addOnTalkEvent(questId);
		qe.registerQuestNpc(700272).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 790019) { // Zetus
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					}
					default: {
						return sendQuestStartDialog(env, 182201783, 1);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 700272) { // Suspicious Stone Statue
				if (dialog == QuestDialog.USE_OBJECT) {
					// Wearing Stenon Blouse and Stenon Skirt
					if (!player.getEquipment().getEquippedItemsByItemId(110100150).isEmpty()
						&& !player.getEquipment().getEquippedItemsByItemId(113100144).isEmpty()) {
						// Having Myanee's Flute
						if (player.getInventory().getItemCountByItemId(182201783) > 0) {
							// TODO: movie
							return useQuestObject(env, 0, 0, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790019) { // Zetus
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 10002);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}
}
