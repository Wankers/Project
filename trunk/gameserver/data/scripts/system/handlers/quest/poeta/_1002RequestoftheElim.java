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
package quest.poeta;

import gameserver.model.EmotionType;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.network.aion.serverpackets.SM_ASCENSION_MORPH;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.instance.InstanceService;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.WorldMapInstance;

/**
 * @author MrPoke
 * @reworked vlog
 */
public class _1002RequestoftheElim extends QuestHandler {

	private final static int questId = 1002;

	public _1002RequestoftheElim() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203076, 730007, 730010, 730008, 205000, 203067 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203076: { // Ampeis
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 730007: { // Forest Protector Noah
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 5) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1353: {
							if (var == 1) {
								playQuestMovie(env, 20);
								return sendQuestDialog(env, 1353);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2, 182200002, 1, 0, 0); // 2
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 5, 6, 0, 0, 182200002, 1); // 6
						}
						case CHECK_COLLECTED_ITEMS: {
							if (var == 6) {
								return checkQuestItems(env, 6, 12, false, 2120, 2205); // 12
							}
							else if (var == 12) {
								return sendQuestDialog(env, 2120);
							}
						}
						case STEP_TO_4: {
							return defaultCloseDialog(env, 12, 13); // 13
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 730010: { // Sleeping Elder
					if (dialog == QuestDialog.USE_OBJECT) {
						if (player.getInventory().getItemCountByItemId(182200002) == 1) {
							if (var == 2) {
								return useQuestObject(env, 2, 4, false, false); // 4
							}
							else if (var == 4) {
								return useQuestObject(env, 4, 5, false, false); // 5
							}
						}
					}
					break;
				}
				case 730008: { // Daminu
					switch (dialog) {
						case START_DIALOG: {
							if (var == 13) {
								return sendQuestDialog(env, 2375);
							}
							else if (var == 14) {
								return sendQuestDialog(env, 2461);
							}
						}
						case STEP_TO_5: {
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310010000);
							TeleportService.teleportTo(player, 310010000, newInstance.getInstanceId(), 52, 174, 229, 3000, true);
							changeQuestStep(env, 13, 20, false); // 20
							return closeDialogWindow(env);
						}
						case STEP_TO_6: {
							return defaultCloseDialog(env, 14, 14, true, false); // reward
						}
					}
					break;
				}
				case 205000: { // Belpartan
					switch (dialog) {
						case START_DIALOG: {
							if (var == 20) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(1001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
								ThreadPoolManager.getInstance().schedule(new Runnable() {

									@Override
									public void run() {
										changeQuestStep(env, 20, 14, false); // 14
										TeleportService.teleportTo(player, 210010000, 1, 603, 1537, 116, (byte) 20, 3000, true);
									}
								}, 43000);
								return true;
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) { // Kalio
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2716);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 310010000) {
				PacketSendUtility.sendPacket(player, new SM_ASCENSION_MORPH(1));
				return true;
			}
			else {
				int var = qs.getQuestVarById(0);
				if (var == 20) {
					changeQuestStep(env, 20, 13, false); // 13
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
}
