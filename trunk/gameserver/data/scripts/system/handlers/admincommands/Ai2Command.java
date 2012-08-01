package admincommands;

import java.util.Iterator;

import org.slf4j.LoggerFactory;

import gameserver.ai2.AI2Engine;
import gameserver.ai2.AbstractAI;
import gameserver.ai2.event.AIEventLog;
import gameserver.ai2.event.AIEventType;
import gameserver.configs.main.AIConfig;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;

/**
 * @author ATracer
 */
public class Ai2Command extends ChatCommand {

	public Ai2Command() {
		super("ai2");
	}

	@Override
	public void execute(Player player, String... params) {
		/**
		 * Non target commands
		 */
		String param0 = params[0];

		if (param0.equals("createlog")) {
			boolean oldValue = AIConfig.ONCREATE_DEBUG;
			AIConfig.ONCREATE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New createlog value: " + !oldValue);
			return;
		}

		if (param0.equals("eventlog")) {
			boolean oldValue = AIConfig.EVENT_DEBUG;
			AIConfig.EVENT_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New eventlog value: " + !oldValue);
			return;
		}

		if (param0.equals("movelog")) {
			boolean oldValue = AIConfig.MOVE_DEBUG;
			AIConfig.MOVE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(player, "New movelog value: " + !oldValue);
			return;
		}

		if (param0.equals("say")) {
			LoggerFactory.getLogger(Ai2Command.class).info("[AI2] marker: " + params[1]);
		}

		/**
		 * Target commands
		 */
		VisibleObject target = player.getTarget();

		if (target == null || !(target instanceof Npc)) {
			PacketSendUtility.sendMessage(player, "Select target first (Npc only)");
			return;
		}
		Npc npc = (Npc) target;

		if (param0.equals("info")) {
			PacketSendUtility.sendMessage(player, "Ai name: " + npc.getAi2().getName());
			PacketSendUtility.sendMessage(player, "Ai state: " + npc.getAi2().getState());
			PacketSendUtility.sendMessage(player, "Ai substate: " + npc.getAi2().getSubState());
			return;
		}

		if (param0.equals("log")) {
			boolean oldValue = npc.getAi2().isLogging();
			((AbstractAI) npc.getAi2()).setLogging(!oldValue);
			PacketSendUtility.sendMessage(player, "New log value: " + !oldValue);
			return;
		}

		if (param0.equals("print")) {
			AIEventLog eventLog = ((AbstractAI) npc.getAi2()).getEventLog();
			Iterator<AIEventType> iterator = eventLog.iterator();
			while (iterator.hasNext()) {
				PacketSendUtility.sendMessage(player, "EVENT: " + iterator.next().name());
			}
			return;
		}

		String param1 = params[1];
		if (param0.equals("set")) {
			String aiName = param1;
			AI2Engine.getInstance().setupAI(aiName, npc);
		}
		else if (param0.equals("event")) {
			AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
			if (eventType != null) {
				npc.getAi2().onGeneralEvent(eventType);
			}
		}
		else if (param0.equals("event2")) {
			AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
			Creature creature = (Creature) World.getInstance().findVisibleObject(Integer.valueOf(params[2]));
			if (eventType != null) {
				npc.getAi2().onCreatureEvent(eventType, creature);
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //ai2 <set|event|event2|info|log|print|createlog|eventlog|movelog>");
	}

}
