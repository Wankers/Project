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
package gameserver.questEngine.handlers.template;

import gnu.trove.list.array.TIntArrayList;
import javolution.util.FastMap;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnSearchResult;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.handlers.models.Monster;
import gameserver.questEngine.handlers.models.SpawnedMonster;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * @author vlog
 */
public class KillSpawned extends QuestHandler {

	private final int questId;
	private final int startNpc;
	private final int startNpc2;
	private final int endNpc;
	private final int endNpc2;
	private final FastMap<Integer, SpawnedMonster> spawnedMonsters;
	private TIntArrayList spawnerObjects;

	public KillSpawned(int questId, int startNpc, int startNpc2, int endNpc, int endNpc2, FastMap<Integer, SpawnedMonster> spawnedMonsters) {
		super(questId);
		this.questId = questId;
		this.startNpc = startNpc;
		if (startNpc2 != 0) {
			this.startNpc2 = startNpc2;
		}
		else {
			this.startNpc2 = this.startNpc;
		}
		if (endNpc != 0) {
			this.endNpc = endNpc;
		}
		else {
			this.endNpc = startNpc;
		}
		if (endNpc2 != 0) {
			this.endNpc2 = endNpc2;
		}
		else {
			if (startNpc2 != 0) {
				this.endNpc2 = startNpc2;
			}
			else {
				this.endNpc2 = this.endNpc;
			}
		}
		this.spawnedMonsters = spawnedMonsters;
		this.spawnerObjects = new TIntArrayList();
		for (SpawnedMonster m : spawnedMonsters.values()) {
			spawnerObjects.add(m.getSpawnerObject());
		}
	}

	@Override
	public void register() {
		if (startNpc != 0) {
			qe.registerQuestNpc(startNpc).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpc).addOnTalkEvent(questId);
		}
		if (startNpc2 != 0) {
			qe.registerQuestNpc(startNpc2).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpc2).addOnTalkEvent(questId);
		}
		for (int spawnedMonsterId : spawnedMonsters.keySet()) {
			qe.registerQuestNpc(spawnedMonsterId).addOnKillEvent(questId);
		}
		if (endNpc != startNpc) {
			qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		}
		if (endNpc2 != startNpc2) {
			qe.registerQuestNpc(endNpc2).addOnTalkEvent(questId);
		}
		for (int i = 0; i < spawnerObjects.size(); i++) {
			qe.registerQuestNpc(spawnerObjects.get(i)).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpc == 0 || targetId == startNpc || targetId == startNpc2) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			for (Monster mi : spawnedMonsters.values()) {
				if (mi.getEndVar() > qs.getQuestVarById(mi.getVar())) {
					return false;
				}
			}
			if (targetId == endNpc || targetId == endNpc2) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 10002);
				}
				else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
			}
			else if (spawnerObjects.contains(targetId)) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(player.getWorldId(), targetId);
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), targetId, searchResult.getSpot().getX(), searchResult.getSpot().getY(),
						searchResult.getSpot().getZ(), searchResult.getSpot().getHeading());
					return true;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpc || targetId == endNpc2) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			SpawnedMonster m = spawnedMonsters.get(env.getTargetId());
			if (m != null) {
				if (qs.getQuestVarById(m.getVar()) < m.getEndVar()) {
					qs.setQuestVarById(m.getVar(), qs.getQuestVarById(m.getVar()) + 1);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}
