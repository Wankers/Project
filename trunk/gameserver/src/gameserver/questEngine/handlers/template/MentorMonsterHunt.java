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

import javolution.util.FastMap;

import gameserver.configs.main.GroupConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.templates.QuestTemplate;
import gameserver.questEngine.handlers.models.Monster;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.MathUtil;


/**
 * @author MrPoke
 *
 */
public class MentorMonsterHunt extends MonsterHunt {

	private int menteMinLevel;
	private int menteMaxLevel;
	private QuestTemplate qt;
	/**
	 * @param questId
	 * @param startNpc
	 * @param endNpc
	 * @param monsters
	 */
	public MentorMonsterHunt(int questId, int startNpc, int endNpc, FastMap<Integer, Monster> monsters, int menteMinLevel, int menteMaxLevel) {
		super(questId, startNpc, 0, endNpc, 0, monsters);
		this.menteMinLevel = menteMinLevel;
		this.menteMaxLevel = menteMaxLevel;
		this.qt = DataManager.QUEST_DATA.getQuestById(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs != null && qs.getStatus() == QuestStatus.START){
			switch(qt.getMentorType()){
				case MENTOR:
					if (player.isMentor()) {
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()) {
							if (member.getLevel() >= menteMinLevel && member.getLevel() <= menteMaxLevel
								&& MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE) {
								return super.onKillEvent(env);
							}
						}
					}
					break;
				case MENTE:
					if (player.isInGroup2()){
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()){
							if (member.isMentor() && MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE)
								return super.onKillEvent(env);
						}
					}
			}
		}
		return false;
	}
}
