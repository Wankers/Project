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
package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.database.dao.DAOManager;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AI2Request;
import gameserver.ai2.AIName;
import gameserver.ai2.NpcAI2;
import gameserver.configs.main.CustomConfig;
import gameserver.dao.PlayerBindPointDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.TribeClass;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.BindPointPosition;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.BindPointTemplate;
import gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.teleport.TeleportService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldType;

/**
 * @author ATracer
 */
@AIName("resurrect")
public class ResurrectAI2 extends NpcAI2 {

	private static Logger log = LoggerFactory.getLogger(ResurrectAI2.class);
	private static int bmap;
	private static float bx;
	private static float by;
	private static float bz;

	@Override
	protected void handleDialogStart(Player player) {
		
		if (player.getBindPoint() == null) {
                bmap = 0;
                bx = 0;
                by = 0;
                bz = 0;
		} else {
				bmap = player.getBindPoint().getMapId();
                bx = player.getBindPoint().getX();
                by = player.getBindPoint().getY();
                bz = player.getBindPoint().getZ();
		}
                int mp = getPosition().getMapId();
                float bix = getPosition().getX();
                float biy = getPosition().getY();
                float biz = getPosition().getZ();
               
                
		BindPointTemplate bindPointTemplate = DataManager.BIND_POINT_DATA.getBindPointTemplate(getNpcId());
		Race race = player.getRace();
		if (bindPointTemplate == null) {
			log.info("There is no bind point template for npc: " + getNpcId());
			return;
		}
                
		if(bmap != mp & bx != mp & by != biy & bz != biz & MathUtil.getDistance(player, getOwner()) == 20)
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOTIFY_RESURRECT_POINT);
		}
                
		if (player.getBindPoint() != null && player.getBindPoint().getMapId() == getPosition().getMapId() && MathUtil.getDistance(player.getBindPoint().getX(), player.getBindPoint().getY(), player.getBindPoint().getZ(),getPosition().getX(), getPosition().getY(), getPosition().getZ()) < 20) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ALREADY_REGISTER_THIS_RESURRECT_POINT);
			return;
		}

		WorldType worldType = player.getWorldType();
		if (!CustomConfig.ENABLE_CROSS_FACTION_BINDING) {
			if ((!getRace().equals(Race.NONE) && !getRace().equals(race)) ||
					(race.equals(Race.ASMODIANS) && getTribe().equals(TribeClass.FIELD_OBJECT_LIGHT)) ||
					(race.equals(Race.ELYOS) && getTribe().equals(TribeClass.FIELD_OBJECT_DARK))) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_BINDSTONE_CANNOT_FOR_INVALID_RIGHT(player.getCommonData().
						getOppositeRace().toString()));
				return;
			}
		}
		if (worldType == WorldType.PRISON) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
			return;
		}
		bindHere(player, bindPointTemplate);
	}

	private void bindHere(Player player, final BindPointTemplate bindPointTemplate) {

		String price = Integer.toString(bindPointTemplate.getPrice());
		AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_BIND_TO_LOCATION, 0, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				// check if this both creatures are in same world
				if (responder.getWorldId() == requester.getWorldId()) {
					// check enough kinah
					if (responder.getInventory().getKinah() < bindPointTemplate.getPrice()) {
						PacketSendUtility.sendPacket(responder,
							SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE);
						return;
					}
					else if (MathUtil.getDistance(requester, responder) > 5) {
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
						return;
					}

					BindPointPosition old = responder.getBindPoint();
					BindPointPosition bpp = new BindPointPosition(requester.getWorldId(), responder.getX(), responder.getY(),
						responder.getZ(), responder.getHeading());
					bpp.setPersistentState(old == null ? PersistentState.NEW : PersistentState.UPDATE_REQUIRED);
					responder.setBindPoint(bpp);
					if (DAOManager.getDAO(PlayerBindPointDAO.class).store(responder)) {
						responder.getInventory().decreaseKinah(bindPointTemplate.getPrice());
						TeleportService.sendSetBindPoint(responder);
						PacketSendUtility.broadcastPacket(responder, new SM_LEVEL_UPDATE(responder.getObjectId(), 2, responder.getCommonData().getLevel()), true);
						PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.STR_DEATH_REGISTER_RESURRECT_POINT("")); //TODO
						old = null;
					}
					else
						// if any errors happen, left that player with old bind point
						responder.setBindPoint(old);
				}
			}
		}, price);
	}

}
