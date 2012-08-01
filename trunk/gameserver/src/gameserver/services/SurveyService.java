package gameserver.services;

import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import gameserver.cache.HTMLCache;
import gameserver.configs.main.CustomConfig;
import gameserver.dao.SurveyControllerDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.items.ItemId;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.model.templates.survey.SurveyItem;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;

/**
 * @author KID
 */
public class SurveyService {

	private static final Logger log = LoggerFactory.getLogger(SurveyService.class);
	private FastMap<Integer, SurveyItem> activeItems;
	private final String htmlTemplate;

	public boolean isActive(Player player, int survId) {
		boolean avail = this.activeItems.containsKey(survId);
		if (avail)
			this.requestSurvey(player, survId);

		return avail;
	}

	public SurveyService() {
		activeItems = FastMap.newInstance();
		this.htmlTemplate = HTMLCache.getInstance().getHTML("surveyTemplate.xhtml");
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new TaskUpdate(), 2000, CustomConfig.SURVEY_DELAY * 60000);
	}

	public void requestSurvey(Player player, int survId) {
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			log.warn("[SurveyController] player " + player.getName() + " tried to receive item with full inventory.");
			return;
		}

		SurveyItem item = activeItems.get(survId);
		if (item == null) {
			// There is no survey underway.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300684));
			return;
		}

		if (item.ownerId != player.getObjectId()) {
			// There is no remaining survey to take part in.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300037));
			return;
		}

		if (DAOManager.getDAO(SurveyControllerDAO.class).useItem(item.uniqueId)) {
			ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(item.itemId);
			ItemService.addItem(player, item.itemId, item.count);
			if (item.itemId == ItemId.KINAH.value()) // You received %num0 Kinah as reward for the survey.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300945, item.count));
			else if (item.count == 1) // You received %0 item as reward for the survey.
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300945, new DescriptionId(template.getNameId())));
			else
				// You received %num1 %0 items as reward for the survey.
				PacketSendUtility.sendPacket(player,
					new SM_SYSTEM_MESSAGE(1300946, item.count, new DescriptionId(template.getNameId())));

			template = null;
			activeItems.remove(survId);
		}
	}

	public void taskUpdate() {
		List<SurveyItem> newList = DAOManager.getDAO(SurveyControllerDAO.class).getAllNew();
		if (newList.size() == 0)
			return;

		List<Integer> players = FastList.newInstance();
		int cnt = 0;
		for (SurveyItem item : newList) {
			activeItems.put(item.uniqueId, item);
			cnt++;
			if (!players.contains(item.ownerId))
				players.add(item.ownerId);
		}
		log.info("[SurveyController] found new " + cnt + " items for " + players.size() + " players.");
		for (int ownerId : players) {
			Player player = World.getInstance().findPlayer(ownerId);
			if (player != null) {
				showAvailable(player);
			}
		}
	}

	public void showAvailable(Player player) {
		for (SurveyItem item : this.activeItems.values()) {
			if (item.ownerId != player.getObjectId())
				continue;

			String context = htmlTemplate;
			context = context.replace("%itemid%", item.itemId + "");
			context = context.replace("%itemcount%", item.count + "");
			context = context.replace("%html%", item.html);
			context = context.replace("%radio%", item.radio);

			HTMLService.sendData(player, item.uniqueId, context);
		}
	}

	public class TaskUpdate implements Runnable {

		@Override
		public void run() {
			log.info("[SurveyController] update task start.");
			taskUpdate();
		}
	}

	private static class SingletonHolder {

		protected static final SurveyService instance = new SurveyService();
	}

	public static final SurveyService getInstance() {
		return SingletonHolder.instance;
	}
}
