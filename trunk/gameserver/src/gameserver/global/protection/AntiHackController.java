package gameserver.global.protection;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.utils.audit.AuditLogger;

/**
 * @author Dean
 *
 */
public class AntiHackController {

    public int questCounters;
    public int oldQuest;
    private long questLastSecond;

    public boolean questCheck(Player player, QuestState qs) {
        long currrentSecond = System.currentTimeMillis();
        if ((currrentSecond + 1000) < getQuestLastSecond()) {
            return true;
        }
        if (qs.getQuestId() != getOldQuest()) {
            setQuestCounters(0);
        } else {
            if ((getQuestLastSecond() + 60000) > currrentSecond) {
                return false;
            } else {
                setQuestCounters(0);
            }
        }
        try {
            setQuestCounters(getQuestCounters() + 1);
            if ((getQuestCounters() > 1) && (qs.getQuestId() == getOldQuest())) {
                AuditLogger.info(player, "Player try abuse QuestHandler. QuestId:[" + qs.getQuestId() + "]");
                return false;
            }
        } catch (Throwable e) {
            System.out.print(e);
        }
        setOldQuest(qs.getQuestId());
        setQuestLastSecond(System.currentTimeMillis());
        return true;
    }
    
    public boolean questCanDeleteQuest(Player player, QuestState qs) {
        if (qs.getStatus() == QuestStatus.COMPLETE) {
            AuditLogger.info(player, "Player try abuse CM_DELETE_QUEST. QuestId:[" + qs.getQuestId() + "]");
            return false;
        }
        return true;
    }

    public boolean questCheckRepeatTime(Player player, QuestState qs) {
        return qs.canRepeatByTime();
    }

    private long getQuestLastSecond() {
        return this.questLastSecond;
    }

    private void setQuestLastSecond(long value) {
        this.questLastSecond = value;
    }

    private int getOldQuest() {
        return this.oldQuest;
    }

    private void setOldQuest(int value) {
        this.oldQuest = value;
    }

    private int getQuestCounters() {
        return this.questCounters;
    }

    private void setQuestCounters(int value) {
        this.questCounters = value;
    }
}
