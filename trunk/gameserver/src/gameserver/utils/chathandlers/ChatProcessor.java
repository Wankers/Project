package gameserver.utils.chathandlers;

import commons.scripting.classlistener.AggregatedClassListener;
import commons.scripting.classlistener.OnClassLoadUnloadListener;
import commons.scripting.classlistener.ScheduledTaskClassListener;
import commons.scripting.scriptmanager.ScriptManager;
import commons.utils.PropertiesUtils;
import gameserver.GameServerError;
import gameserver.configs.main.LoggingConfig;
import gameserver.configs.main.WeddingsConfig;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author KID
 * @Modified Rolandas
 * 
 */
public class ChatProcessor {
	private static final Logger log = LoggerFactory.getLogger(ChatProcessor.class);
	private static ChatProcessor instance = new ChatProcessor();
	private Map<String, ChatCommand> commands = new FastMap<String, ChatCommand>();
	private Map<String, Byte> accessLevel = new FastMap<String, Byte>();
	private ScriptManager sm = new ScriptManager();
	
	private static final String[] EMPTY_PARAMS = new String[]{};
	
	public static ChatProcessor getInstance() {
		return instance;
	}

	public ChatProcessor() {
		init(sm, this);
	}
	
	private ChatProcessor(ScriptManager scriptManager) {
		init(scriptManager, this);
	}
	
	private void init(ScriptManager scriptManager, ChatProcessor processor) {
		loadLevels();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ChatCommandsLoader(processor));
		scriptManager.setGlobalClassListener(acl);
		
		try {
			scriptManager.load(new File("./data/scripts/system/adminhandlers.xml"));
		} catch (Exception e) {
			throw new GameServerError("Can't initialize admin chat handlers.", e);
		}
		try {
			scriptManager.load(new File("./data/scripts/system/playerhandlers.xml"));
		} catch (Exception e) {
			throw new GameServerError("Can't initialize player chat handlers.", e);
		}
		try {
			scriptManager.load(new File("./data/scripts/system/weddinghandlers.xml"));
		} catch (Exception e) {
			throw new GameServerError("Can't initialize wedding chat handlers.", e);
		}		
	}

	public void registerCommand(ChatCommand cmd) {
		if (commands.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " is already registered. Fail");
			return;
		}

		if (!accessLevel.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " do not have access level. Fail");
			return;
		}

		cmd.setAccessLevel(accessLevel.get(cmd.getAlias()));
		commands.put(cmd.getAlias(), cmd);
	}

	public void reload() {
		ScriptManager tmpSM;
		final ChatProcessor adminCP;
		Map<String, ChatCommand> backupCommands = new FastMap<String, ChatCommand>(commands);
		commands.clear();
		try
		{
			tmpSM = new ScriptManager();
			adminCP = new ChatProcessor(tmpSM);
		}
		catch(Throwable e)
		{
			commands = backupCommands;
			throw new GameServerError("Can't reload chat handlers.", e);
		}
		
		if(tmpSM != null && adminCP != null)
		{
			backupCommands.clear();
			sm.shutdown();
			sm = null;
			sm = tmpSM;
			instance = adminCP;
		}
	}
	
	private void loadLevels()
	{
		accessLevel.clear();
		try {
			java.util.Properties props = PropertiesUtils.load("config/administration/commands.properties");

			for(Object key : props.keySet()){
				String str = (String) key;
				accessLevel.put(str, Byte.valueOf(props.getProperty(str).trim()));
			}
		} catch (IOException e) {
			log.error("Can't read commands.properties", e);
		}
	}

	public boolean handleChatCommand(Player player, String text) {
		if (text.startsWith("//") && player.getAccessLevel() > 0) {
			return processCommand(player, text.substring(2), true);
		} else if (text.startsWith("..") && (player.havePermission(WeddingsConfig.WEDDINGS_COMMAND_MEMBERSHIP)) && player.isMarried()) {
			return processCommand(player, text.substring(2), false);
		} else if (text.startsWith(".")) {
			return processCommand(player, text.substring(1), false);
		} else
			return false;
	}

	private boolean processCommand(Player player, String text, boolean admin) {
		String alias = text.split(" ")[0];

		ChatCommand cmd = this.commands.get(alias);
		if (cmd == null) {
			if (admin && player.getAccessLevel() > 0)
				PacketSendUtility.sendMessage(player, "[WARN] There is no such command as //" + alias);
			return true;
		}

		if (!admin && cmd.getLevel() != 0)
			return false;
		else {
			if (player.getAccessLevel() < cmd.getLevel()) {
				if (LoggingConfig.LOG_GMAUDIT)
					log.info("[ADMIN COMMAND] > [Player: " + player.getName() + "] has tried to use the command //" + alias + " without having the rights");
				if (player.getAccessLevel() > 0)
					PacketSendUtility.sendMessage(player, "[WARN] You need to have access level " + cmd.getLevel() + " or more to use //" + alias);
				return true;
			}
		}
		
		boolean success = false;
		if(text.length() == alias.length())
			success = cmd.run(player, EMPTY_PARAMS);
		else
			success = cmd.run(player, text.substring(alias.length() +1).split(" "));
		
		if (LoggingConfig.LOG_GMAUDIT && admin) {
			if (player.getTarget() != null && player.getTarget() instanceof Creature) {
				Creature target = (Creature) player.getTarget();
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "][Target : " + target.getName() + "]: " + text);
			}
			else
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "]: " + text);
		}
		
		if(!success && admin && player.getAccessLevel() > 0) { //An admin fails an Admin command
			PacketSendUtility.sendMessage(player, "<You have failed to execute //" + text + ">");
			return true;
		}
		else
			return success;
	}

	public void onCompileDone() {
		log.info("Loaded " + commands.size() + " commands.");
	}
}
