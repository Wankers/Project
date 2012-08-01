package admincommands;

import static org.apache.commons.io.filefilter.FileFilterUtils.and;
import static org.apache.commons.io.filefilter.FileFilterUtils.makeSVNAware;
import static org.apache.commons.io.filefilter.FileFilterUtils.notFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.prefixFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.suffixFileFilter;

import gnu.trove.procedure.TObjectProcedure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import gameserver.dataholders.DataManager;
import gameserver.dataholders.EventData;
import gameserver.dataholders.NpcDropData;
import gameserver.dataholders.PortalData;
import gameserver.dataholders.QuestsData;
import gameserver.dataholders.SkillData;
import gameserver.dataholders.StaticData;
import gameserver.dataholders.XMLQuests;
import gameserver.dataholders.loadingutils.XmlValidationHandler;
import gameserver.model.drop.NpcDrop;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.ingameshop.InGameShopEn;
import gameserver.model.templates.npc.NpcTemplate;
import gameserver.model.templates.portal.PortalTemplate;
import gameserver.questEngine.QuestEngine;
import gameserver.services.EventService;
import gameserver.services.drop.DropRegistrationService;
import gameserver.skillengine.model.SkillTemplate;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.utils.chathandlers.ChatProcessor;

/**
 * @author MrPoke
 */
public class Reload extends ChatCommand {

	private static final Logger log = LoggerFactory.getLogger(Reload.class);

	public Reload() {
		super("reload");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(admin,
				"syntax //reload <quest | skill | portal | spawn | commands | drop | gameshop | events>");
			return;
		}
		if (params[0].equals("quest")) {
			File xml = new File("./data/static_data/quest_data/quest_data.xml");
			File dir = new File("./data/static_data/quest_script_data");
			try {
				QuestEngine.getInstance().shutdown();
				JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				QuestsData newQuestData = (QuestsData) un.unmarshal(xml);
				QuestsData questsData = DataManager.QUEST_DATA;
				questsData.setQuestsData(newQuestData.getQuestsData());
				XMLQuests questScriptsData = DataManager.XML_QUESTS;
				questScriptsData.getQuest().clear();
				for (File file : listFiles(dir, true)) {
					XMLQuests data = ((XMLQuests) un.unmarshal(file));
					if (data != null)
						if (data.getQuest() != null)
							questScriptsData.getQuest().addAll(data.getQuest());
				}
				QuestEngine.getInstance().load();
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Quest reload failed!");
				log.error("quest reload fail", e);
			}
			finally {
				PacketSendUtility.sendMessage(admin, "Quest reload Success!");
			}
		}
		else if (params[0].equals("skill")) {
			File dir = new File("./data/static_data/skills");
			try {
				JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				List<SkillTemplate> newTemplates = new ArrayList<SkillTemplate>();
				for (File file : listFiles(dir, true)) {
					SkillData data = (SkillData) un.unmarshal(file);
					if (data != null)
						newTemplates.addAll(data.getSkillTemplates());
				}
				DataManager.SKILL_DATA.setSkillTemplates(newTemplates);
				DataManager.SKILL_DATA.initializeCooldownGroups();
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Skill reload failed!");
				log.error("Skill reload failed!", e);
			}
			finally {
				PacketSendUtility.sendMessage(admin, "Skill reload Success!");
			}
		}
		else if (params[0].equals("portal")) {
			File dir = new File("./data/static_data/portals");
			try {
				JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				List<PortalTemplate> newTemplates = new ArrayList<PortalTemplate>();
				for (File file : listFiles(dir, true)) {
					PortalData data = (PortalData) un.unmarshal(file);
					if (data != null && data.getPortals() != null)
						newTemplates.addAll(data.getPortals());
				}
				DataManager.PORTAL_DATA.setPortals(newTemplates);
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Portal reload failed!");
				log.error("Portal reload failed!", e);
			}
			finally {
				PacketSendUtility.sendMessage(admin, "Portal reload Success!");
			}
		}
		else if (params[0].equals("commands")) {
			ChatProcessor.getInstance().reload();
			PacketSendUtility.sendMessage(admin, "Admin commands successfully reloaded!");
		}
		else if (params[0].equals("drop")) {

			DataManager.NPC_DATA.getNpcData().forEachValue(new TObjectProcedure<NpcTemplate>() {

				@Override
				public boolean execute(NpcTemplate object) {
					object.setNpcDrop(null);
					return true;
				}
			});

			File dir = new File("./data/static_data/npc_drops");
			try {
				JAXBContext jc = JAXBContext.newInstance(StaticData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				List<NpcDrop> npcDrop = new ArrayList<NpcDrop>();
				for (File file : listFiles(dir, true)) {
					NpcDropData data = (NpcDropData) un.unmarshal(file);
					if (data != null && data.getNpcDrop() != null) {
						npcDrop.addAll(data.getNpcDrop());
					}
				}
				DataManager.NPC_DROP_DATA.setNpcDrop(npcDrop);
				DropRegistrationService.getInstance().init();
			}
			catch (Exception e) {
				throw new Error("Drop reload failed!", e);
			}
			finally {
				PacketSendUtility.sendMessage(admin, "Drop reload Success!");
			}
		}
		else if (params[0].equals("gameshop")) {
			InGameShopEn.getInstance().reload();
			PacketSendUtility.sendMessage(admin, "Gameshop successfully reloaded!");
		}
		else if (params[0].equals("events")) {
			File eventXml = new File("./data/static_data/events_config/events_config.xml");
			EventData data = null;
			try {
				JAXBContext jc = JAXBContext.newInstance(EventData.class);
				Unmarshaller un = jc.createUnmarshaller();
				un.setEventHandler(new XmlValidationHandler());
				un.setSchema(getSchema("./data/static_data/static_data.xsd"));
				data = (EventData) un.unmarshal(eventXml);
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Event reload failed! Keeping the last version ...");
				log.error("Event reload failed!", e);
				return;
			}
			if (data != null) {
				EventService.getInstance().stop();
				String text = data.getActiveText();
				if (text == null || text.trim().length() == 0)
					text = "NONE";
				DataManager.EVENT_DATA.setAllEvents(data.getAllEvents(), data.getActiveText());
				PacketSendUtility.sendMessage(admin, "Active events: " + text);
				EventService.getInstance().start();
			}
		}
		else
			PacketSendUtility.sendMessage(admin,
				"syntax //reload <quest | skill | portal | spawn | commands | drop | gameshop | events>");

	}

	private Schema getSchema(String xml_schema) {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			schema = sf.newSchema(new File(xml_schema));
		}
		catch (SAXException saxe) {
			throw new Error("Error while getting schema", saxe);
		}

		return schema;
	}

	private Collection<File> listFiles(File root, boolean recursive) {
		IOFileFilter dirFilter = recursive ? makeSVNAware(HiddenFileFilter.VISIBLE) : null;

		return FileUtils.listFiles(root,
			and(and(notFileFilter(prefixFileFilter("new")), suffixFileFilter(".xml")), HiddenFileFilter.VISIBLE), dirFilter);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player,
			"syntax //reload <quest | skill | portal | commands | drop | gameshop | events>");
	}
}
