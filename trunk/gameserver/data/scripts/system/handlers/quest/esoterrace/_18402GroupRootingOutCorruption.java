package quest.esoterrace;

import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestDialog;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 * 
 */
public class _18402GroupRootingOutCorruption extends QuestHandler
{
	private final static int	questId	= 18402;

	public _18402GroupRootingOutCorruption()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(799590).addOnQuestStart(questId);
		qe.registerQuestNpc(799590).addOnTalkEvent(questId);
		qe.registerQuestNpc(701015).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(targetId == 799590)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
			{
				if(env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if(env.getDialog() == QuestDialog.SELECT_REWARD)
					return sendQuestDialog(env, 5);
				else 
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = env.getTargetId();

		switch(targetId)
			{				
				case 701015:
					if (qs.getQuestVarById(1) < 5)
					{
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);	
					}	
					else if (qs.getQuestVarById(1) == 5)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);	
					}							
			}		
		return false;
	}
}
