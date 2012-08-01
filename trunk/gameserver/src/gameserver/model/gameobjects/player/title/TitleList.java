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
package gameserver.model.gameobjects.player.title;

import java.util.Collection;

import javolution.util.FastMap;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerTitleListDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.listeners.TitleChangeListener;
import gameserver.model.templates.TitleTemplate;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import gameserver.taskmanager.tasks.ExpireTimerTask;
import gameserver.utils.PacketSendUtility;

/**
 * @author xavier, cura, xTz
 */
public class TitleList {

	private final FastMap<Integer, Title> titles;
	private Player owner;

	public TitleList() {
		this.titles = new FastMap<Integer, Title>();
		this.owner = null;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean contains(int titleId) {
		return titles.containsKey(titleId);
	}

	public void addEntry(int titleId, int remaining) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		titles.put(titleId, new Title(tt, titleId, remaining));
	}

	public boolean addTitle(int titleId, boolean questReward, int time) {
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt == null) {
			throw new IllegalArgumentException("Invalid title id " + titleId);
		}
		if (owner != null) {
			if (owner.getRace() != tt.getRace() && tt.getRace() != Race.PC_ALL) {
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RACE);
				return false;
			}
			Title entry = new Title(tt, titleId, time);
			if (!titles.containsKey(titleId)) {
				titles.put(titleId, entry);
				if (time != 0)
					ExpireTimerTask.getInstance().addTask(entry, owner);
				DAOManager.getDAO(PlayerTitleListDAO.class).storeTitles(owner, entry);
			}
			else {
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
				return false;
			}
			if (questReward)
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_QUEST_GET_REWARD_TITLE(tt.getNameId()));
			else
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_GET_CASH_TITLE(tt.getNameId()));

			PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
			return true;
		}
		return false;
	}

	public void setTitle(int titleId) {
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(titleId));
		PacketSendUtility.broadcastPacketAndReceive(owner, (new SM_TITLE_INFO(owner, titleId)));
		if (owner.getCommonData().getTitleId() > 0) {
			if (owner.getGameStats() != null) {
				TitleChangeListener.onTitleChange(owner.getGameStats(), owner.getCommonData().getTitleId(), false);
			}
		}
		owner.getCommonData().setTitleId(titleId);
		if (titleId > 0 && owner.getGameStats() != null) {
			TitleChangeListener.onTitleChange(owner.getGameStats(), titleId, true);
		}
	}

	public void removeTitle(int titleId) {
		if (!titles.containsKey(titleId))
			return;
		if (owner.getCommonData().getTitleId() == titleId)
			setTitle(-1);
		titles.remove(titleId);
		PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(owner));
		DAOManager.getDAO(PlayerTitleListDAO.class).removeTitle(owner.getObjectId(), titleId);
	}

	public int size() {
		return titles.size();
	}

	public Collection<Title> getTitles() {
		return titles.values();
	}
}
