package gameserver.model.ingameshop;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import gameserver.configs.main.InGameShopConfig;
import gameserver.configs.main.LoggingConfig;
import gameserver.dao.InGameShopDAO;
import gameserver.dao.PlayerDAO;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.templates.mail.MailMessage;
import gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import gameserver.network.loginserver.LoginServer;
import gameserver.network.loginserver.serverpackets.SM_PREMIUM_CONTROL;
import gameserver.services.SystemMailService;
import gameserver.services.item.ItemService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * 
 * @author KID
 *
 */
public class InGameShopEn {
	private static InGameShopEn instance = new InGameShopEn();
	private final Logger log = LoggerFactory.getLogger(InGameShopEn.class);
	
	public static InGameShopEn getInstance() {
		return instance;
	}
	
	private FastMap<Integer, IGItem> items;
	private InGameShopDAO dao;
	
	public InGameShopEn() {
		if (!InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			log.info("InGameShop is disabled.");
			return;
		}

		dao = DAOManager.getDAO(InGameShopDAO.class);
		items = FastMap.newInstance();
		activeRequests = FastList.newInstance();
		items = dao.loadInGameShopItems();
		log.info("Loaded with " + items.size() + " items.");
	}

	public void	reload() {
		if (!InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			log.info("InGameShop is disabled.");
			return;
		}

		items = DAOManager.getDAO(InGameShopDAO.class).loadInGameShopItems();
		log.info("Loaded with " + items.size() + " items.");
	}
	
	public IGItem getIGItem(int id) {
		return items.get(id);
	}
	
	public Collection<IGItem> getItems() {
		return this.items.values();
	}
	
	@SuppressWarnings("unchecked")
	public FastList<Integer> getTopSales(int category) {
		byte max = 6;
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>(new DescFilter());
		for(IGItem item : this.items.values()) {
			if(item.getSalesRanking() == 0)
				continue;
			
			if(category != 2 && item.getCategory() != category)
				continue;
			
			map.put(item.getSalesRanking(), item.getObjectId());
		}
		FastList<Integer> top = FastList.newInstance();
		byte cnt = 0;
		for(int objId : map.values()) {
			if(cnt <= max) {
				top.add(objId);
				cnt++;
			}
			else
				break;
		}
		map.clear();
		return top;
	}
	
	@SuppressWarnings("rawtypes")
	class DescFilter implements Comparator {
		public int compare(Object o1, Object o2) {
			Integer i1 = (Integer) o1;
			Integer i2 = (Integer) o2;
			return -i1.compareTo(i2);
		}
	}
	
	public int getMaxList(int categoryId) {
		int id = 0;
		for(IGItem item : items.values()) {
			if (item.getCategory() == categoryId)
				if (item.getList() > id)
					id = item.getList();
		}
		
		return id;
	}
	
	private int lastRequestId = 0;
	private FastList<IGRequest> activeRequests;
	public void acceptRequest(Player player, int itemObjId) {
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
			return;
		}
		
		IGItem item = InGameShopEn.getInstance().getIGItem(itemObjId);
		lastRequestId++;
		IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), itemObjId);
		request.accountId = player.getClientConnection().getAccount().getId();
		if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, item.getItemPrice())))
			activeRequests.add(request);
		if (LoggingConfig.LOG_INGAMESHOP)
			log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " cost " + item.getItemPrice() + " toll.");
	}
	
	public void sendRequest(Player player, String receiver, String message, int itemObjId) {
		if(receiver.equalsIgnoreCase(player.getName())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_CANNOT_GIVE_TO_ME);
			return;
		}
		
		if(!InGameShopConfig.ALLOW_GIFTS) {
			PacketSendUtility.sendMessage(player, "Gifts are disabled.");
			return;
		}
		
		if (!DAOManager.getDAO(PlayerDAO.class).isNameUsed(receiver)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_NO_USER_TO_GIFT);
			return;
		}
		
		PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(receiver);
		if (recipientCommonData.getMailboxLetters() >= 100) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MAIL_MSG_RECIPIENT_MAILBOX_FULL(recipientCommonData.getName()));
			return;
		}
		
		if (!InGameShopConfig.ENABLE_GIFT_OTHER_RACE && !player.isGM())
			if (player.getRace() != recipientCommonData.getRace()) {
				PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(MailMessage.MAIL_IS_ONE_RACE_ONLY));
				return;
			}
		
		IGItem item = getIGItem(itemObjId);
		lastRequestId++;
		IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), receiver, message, itemObjId);
		request.accountId = player.getClientConnection().getAccount().getId();
		if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, item.getItemPrice())))
			activeRequests.add(request);
	}
	
	public void addToll(Player player, int cnt) {
		if (InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			lastRequestId++;
			IGRequest request = new IGRequest(lastRequestId, player.getObjectId(), 0);
			request.accountId = player.getClientConnection().getAccount().getId();
			if (LoginServer.getInstance().sendPacket(new SM_PREMIUM_CONTROL(request, cnt * -1)))
				activeRequests.add(request);
		}
		else {
			PacketSendUtility.sendMessage(player, "You can't add toll if ingameshop is disabled!");
		}
	}
	
	public void finishRequest(int requestId, int result, int toll) {
		for(IGRequest request : activeRequests)
			if (request.requestId == requestId) {
				Player player = World.getInstance().findPlayer(request.playerId);
				if (player != null) {
					if (result == 1) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_ERROR);
					}
					else if (result == 2) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_INGAMESHOP_NOT_ENOUGH_CASH("Toll"));
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					}
					else if (result == 3) {
						IGItem item = getIGItem(request.itemObjId);
						if(item == null) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_ERROR);
							log.error("player "+player.getName()+" requested "+request.itemObjId+" that was not exists in list.");
							return;
						}
						
						if (request.gift) {
							SystemMailService.getInstance().sendMail(player.getName(), request.receiver, "In Game Shop", request.message, item.getItemId(), item.getItemCount(), 0, false);
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INGAMESHOP_GIFT_SUCCESS);
							if (LoggingConfig.LOG_INGAMESHOP)
								log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " BUY ITEM: " + item.getItemId() + " COUNT: " + item.getItemCount() + " FOR PlayerName: " + request.receiver);
						}
						else {
							ItemService.addItem(player, item.getItemId(), item.getItemCount());
							if (LoggingConfig.LOG_INGAMESHOP)
								log.info("[INGAMESHOP] > Account name: " + player.getAcountName() + ", PlayerName: " + player.getName() + " BUY ITEM: " + item.getItemId() + " COUNT: " + item.getItemCount());
						}
						
						item.increaseSales();
						dao.increaseSales(item.getObjectId(), item.getSalesRanking());
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					}
					else if (result == 4) {
						player.getClientConnection().getAccount().setToll(toll);
						PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(toll));
					}
				}
				
				activeRequests.remove(request);
				break;
			}
	}
}
