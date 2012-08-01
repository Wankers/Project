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
package gameserver.questEngine.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.templates.QuestTemplate;
import gameserver.model.templates.quest.QuestCategory;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author MrPoke
 * @modified vlog, Rolandas
 */
public class QuestState {

	private final int questId;
	private QuestVars questVars;
	private QuestStatus status;
	private int completeCount;
	private Timestamp completeTime;
	private Timestamp nextRepeatTime;
	private Integer reward;
	private PersistentState persistentState;
        private Calendar currentDay = GregorianCalendar.getInstance();
        private Calendar calendar = GregorianCalendar.getInstance();

	private static final Logger log = LoggerFactory.getLogger(QuestState.class);

	public QuestState(int questId, QuestStatus status, int questVars, int completeCount, Timestamp nextRepeatTime,
		Integer reward, Timestamp completeTime) {
		this.questId = questId;
		this.status = status;
		this.questVars = new QuestVars(questVars);
		this.completeCount = completeCount;
		this.nextRepeatTime = nextRepeatTime;
		this.reward = reward;
		this.completeTime = completeTime;
		this.persistentState = PersistentState.NEW;
	}

	public QuestVars getQuestVars() {
		return questVars;
	}

	/**
	 * @param id
	 * @param var
	 */
	public void setQuestVarById(int id, int var) {
		questVars.setVarById(id, var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param id
	 * @return Quest var by id.
	 */
	public int getQuestVarById(int id) {
		return questVars.getVarById(id);
	}

	public void setQuestVar(int var) {
		questVars.setVar(var);
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public QuestStatus getStatus() {
		return status;
	}

	public void setStatus(QuestStatus status) {
		if (status == QuestStatus.COMPLETE && this.status != QuestStatus.COMPLETE)
			updateCompleteTime();
		this.status = status;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public Timestamp getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Timestamp time) {
		completeTime = time;
	}

	public void updateCompleteTime() {
		completeTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public int getQuestId() {
		return questId;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public int getCompleteCount() {
		return completeCount;
	}

	public void setNextRepeatTime(Timestamp nextRepeatTime) {
		this.nextRepeatTime = nextRepeatTime;
	}

	public Timestamp getNextRepeatTime() {
		return nextRepeatTime;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	public Integer getReward() {
		if (reward == null) {
			log.warn("No reward for the quest " + String.valueOf(questId));
		}
		else {
			return reward;
		}
		return 0;
	}

	public boolean canRepeat() {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (status == QuestStatus.COMPLETE && template.getCategory() == QuestCategory.EVENT)
			return true;
		if (status != QuestStatus.NONE
			&& (status != QuestStatus.COMPLETE || (completeCount >= template.getMaxRepeatCount() && template
				.getMaxRepeatCount() != 255))) {
			return false;
		}
		if (questVars.getQuestVars() != 0) {
			return false;
		}
		if (template.isTimeBased() && nextRepeatTime != null) {
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			if (currentTime.before(nextRepeatTime)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the pState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 *          the pState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		switch (persistentState) {
			case DELETED:
				if (this.persistentState == PersistentState.NEW)
					throw new IllegalArgumentException("Cannot change state to DELETED from NEW");
			case UPDATE_REQUIRED:
				if (this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
	}
        public boolean canRepeatByTime() {
        Date date = new Date();
        Date completeDate = new Date(completeTime.getTime());
        currentDay.setTime(date);
        calendar.setTime(completeDate);
        if ((calendar.get(Calendar.DAY_OF_MONTH) != currentDay.get(Calendar.DAY_OF_MONTH))
                && (currentDay.get(Calendar.HOUR_OF_DAY) >= 9 && currentDay.get(Calendar.HOUR_OF_DAY) <= 23)) {
            return true;
        }
        return false;
    }
}