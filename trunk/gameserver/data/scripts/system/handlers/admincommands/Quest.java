package admincommands;

import java.util.List;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.QuestStateList;
import gameserver.model.templates.QuestTemplate;
import gameserver.model.templates.quest.FinishedQuestCond;
import gameserver.model.templates.quest.XMLStartCondition;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke
 */
public class Quest extends ChatCommand {

	public Quest() {
		super("quest");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set|show|delete>");
			return;
		}
		Player target = null;
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Incorrect target!");
			return;
		}

		if (params[0].equals("start")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}
			int id;
			try {
				id = Integer.valueOf(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}

			QuestEnv env = new QuestEnv(null, target, id, 0);

			if (QuestService.startQuest(env)) {
				PacketSendUtility.sendMessage(admin, "Quest started.");
			}
			else {
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(id);
				List<XMLStartCondition> preconditions = template.getXMLStartConditions();
				if (preconditions != null && preconditions.size() > 0) {
					for (XMLStartCondition condition : preconditions) {
						List<FinishedQuestCond> finisheds = condition.getFinishedPreconditions();
						if (finisheds != null && finisheds.size() > 0) {
							for (FinishedQuestCond fcondition : finisheds) {
								QuestState qs1 = admin.getQuestStateList().getQuestState(fcondition.getQuestId());
								if (qs1 == null || qs1.getStatus() != QuestStatus.COMPLETE) {
									PacketSendUtility.sendMessage(admin, "You have to finish " + fcondition.getQuestId() + " first!");
								}
							}
						}
					}
				}
				PacketSendUtility.sendMessage(admin, "Quest not started. Some preconditions failed");
			}
		}
		else if (params[0].equals("set")) {
			int questId, var;
			int varNum = 0;
			QuestStatus questStatus;
			try {
				questId = Integer.valueOf(params[1]);
				String statusValue = params[2];
				if ("START".equals(statusValue)) {
					questStatus = QuestStatus.START;
				}
				else if ("NONE".equals(statusValue)) {
					questStatus = QuestStatus.NONE;
				}
				else if ("COMPLETE".equals(statusValue)) {
					questStatus = QuestStatus.COMPLETE;
				}
				else if ("REWARD".equals(statusValue)) {
					questStatus = QuestStatus.REWARD;
				}
				else {
					PacketSendUtility.sendMessage(admin, "<status is one of START, NONE, REWARD, COMPLETE>");
					return;
				}
				var = Integer.valueOf(params[3]);
				if (params.length == 5 && params[4] != null && params[4] != "") {
					varNum = Integer.valueOf(params[4]);
				}
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest set <questId status var [varNum]>");
				return;
			}
			QuestState qs = target.getQuestStateList().getQuestState(questId);
			if (qs == null) {
				PacketSendUtility.sendMessage(admin, "<QuestState wasn't initialized for this quest>");
				return;
			}
			qs.setStatus(questStatus);
			if (varNum != 0) {
				qs.setQuestVarById(varNum, var);
			}
			else {
				qs.setQuestVar(var);
			}
			PacketSendUtility.sendPacket(target, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars()
				.getQuestVars()));
			if (questStatus == QuestStatus.COMPLETE) {
				qs.setCompleteCount(qs.getCompleteCount() + 1);
				target.getController().updateNearbyQuests();
			}
		}
		if (params[0].equals("delete")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest delete <quest id>");
				return;
			}
			int id;
			try {
				id = Integer.valueOf(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "syntax //quest delete <quest id>");
				return;
			}

			QuestStateList list = admin.getQuestStateList();
			if (list == null || list.getQuestState(id) == null) {
				PacketSendUtility.sendMessage(admin, "Quest not deleted.");
			}
			else {
				QuestState qs = list.getQuestState(id);
				qs.setQuestVar(0);
				qs.setCompleteCount(0);
				qs.setStatus(null);
				if (qs.getPersistentState() != PersistentState.NEW)
					qs.setPersistentState(PersistentState.DELETED);
				PacketSendUtility.sendMessage(admin, "Quest deleted. Please logout.");
			}
		}
		else if (params[0].equals("show")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest show <quest id>");
				return;
			}
			ShowQuestInfo(target, admin, params[1]);
		}
		else
			PacketSendUtility.sendMessage(admin, "syntax //quest <start|set|show|delete>");
	}

	private void ShowQuestInfo(Player player, Player admin, String param) {
		int id;
		try {
			id = Integer.valueOf(param);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "syntax //quest show <quest id>");
			return;
		}
		QuestState qs = player.getQuestStateList().getQuestState(id);
		if (qs == null) {
			PacketSendUtility.sendMessage(admin, "Quest state: NULL");
		}
		else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 5; i++)
				sb.append(Integer.toString(qs.getQuestVarById(i)) + " ");
			PacketSendUtility.sendMessage(admin, "Quest state: " + qs.getStatus().toString() + "; vars: " + sb.toString()
				+ qs.getQuestVarById(5));
			sb.setLength(0);
			sb = null;
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //quest <start|set|show|delete>");
	}
}
