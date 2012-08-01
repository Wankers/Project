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
package gameserver.network.aion.clientpackets;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerAppearanceDAO;
import gameserver.dao.PlayerDAO;
import gameserver.model.Gender;
import gameserver.model.account.Account;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerAppearance;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.player.PlayerEnterWorldService;
import gameserver.services.player.PlayerService;
import gameserver.utils.PacketSendUtility;

/**
 * In this packets aion client is requesting edit of character.
 * 
 * @author IlBuono
 */
public class CM_CHARACTER_EDIT extends AionClientPacket {

	private int objectId;

	private boolean gender_change;

	private boolean check_ticket = true;

	/**
	 * Constructs new instance of <tt>CM_CREATE_CHARACTER </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_CHARACTER_EDIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		AionConnection client = getConnection();
		Account account = client.getAccount();
		objectId = readD();
		readB(52);
		if (account.getPlayerAccountData(objectId) == null) {
			return;
		}
		Player player = PlayerService.getPlayer(objectId, account);
		if (player == null) {
			return;
		}
		PlayerCommonData playerCommonData = player.getCommonData();
		PlayerAppearance playerAppearance = player.getPlayerAppearance();
		// Before modify appearance, we do a check of ticket
		int gender = readD();
		gender_change = playerCommonData.getGender().getGenderId() == gender ? false : true;
		if (!gender_change) {
			if (player.getInventory().getItemCountByItemId(169650000) == 0
				&& player.getInventory().getItemCountByItemId(169650001) == 0) {
				check_ticket = false;
				return;
			}
		}
		else {
			if (player.getInventory().getItemCountByItemId(169660000) == 0
				&& player.getInventory().getItemCountByItemId(169660001) == 0) {
				check_ticket = false;
				return;
			}
		}
		playerCommonData.setGender(gender == 0 ? Gender.MALE : Gender.FEMALE);
		readD(); // race
		readD(); // player class

		playerAppearance.setVoice(readD());
		playerAppearance.setSkinRGB(readD());
		playerAppearance.setHairRGB(readD());
		playerAppearance.setEyeRGB(readD());
		playerAppearance.setLipRGB(readD());
		playerAppearance.setFace(readC());
		playerAppearance.setHair(readC());
		playerAppearance.setDeco(readC());
		playerAppearance.setTattoo(readC());
		playerAppearance.setFaceContour(readC());
		playerAppearance.setExpression(readC());
		readC(); // always 4 o0 // 5 in 1.5.x
		playerAppearance.setJawLine(readC());
		playerAppearance.setForehead(readC());

		playerAppearance.setEyeHeight(readC());
		playerAppearance.setEyeSpace(readC());
		playerAppearance.setEyeWidth(readC());
		playerAppearance.setEyeSize(readC());
		playerAppearance.setEyeShape(readC());
		playerAppearance.setEyeAngle(readC());

		playerAppearance.setBrowHeight(readC());
		playerAppearance.setBrowAngle(readC());
		playerAppearance.setBrowShape(readC());

		playerAppearance.setNose(readC());
		playerAppearance.setNoseBridge(readC());
		playerAppearance.setNoseWidth(readC());
		playerAppearance.setNoseTip(readC());

		playerAppearance.setCheek(readC());
		playerAppearance.setLipHeight(readC());
		playerAppearance.setMouthSize(readC());
		playerAppearance.setLipSize(readC());
		playerAppearance.setSmile(readC());
		playerAppearance.setLipShape(readC());
		playerAppearance.setJawHeigh(readC());
		playerAppearance.setChinJut(readC());
		playerAppearance.setEarShape(readC());
		playerAppearance.setHeadSize(readC());

		playerAppearance.setNeck(readC());
		playerAppearance.setNeckLength(readC());

		playerAppearance.setShoulderSize(readC());

		playerAppearance.setTorso(readC());
		playerAppearance.setChest(readC()); // only woman
		playerAppearance.setWaist(readC());
		playerAppearance.setHips(readC());
		
		playerAppearance.setArmThickness(readC());

		playerAppearance.setHandSize(readC());
		playerAppearance.setLegThicnkess(readC());

		playerAppearance.setFootSize(readC());
		playerAppearance.setFacialRate(readC());

		readC(); // always 0
		playerAppearance.setArmLength(readC());
		playerAppearance.setLegLength(readC()); // wrong??
		playerAppearance.setShoulders(readC()); // 1.5.x May be ShoulderSize
		playerAppearance.setFaceShape(readC());
		readC();
		readC();
		readC();
		playerAppearance.setHeight(readF());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		PlayerEnterWorldService.enterWorld(client, objectId);
		Player player = client.getActivePlayer();
		if (!check_ticket) {
			if(!gender_change)
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EDIT_CHAR_ALL_CANT_NO_ITEM);
			else
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_EDIT_CHAR_GENDER_CANT_NO_ITEM);
		}
		else {
			// Remove ticket and save appearance
			if (!gender_change) {
				if (player.getInventory().getItemCountByItemId(169650000) > 0) // plastic surgery ticket normal
					player.getInventory().decreaseByItemId(169650000, 1);
				else if (player.getInventory().getItemCountByItemId(169650001) > 0) // plastic surgery ticket event
					player.getInventory().decreaseByItemId(169650001, 1);
			}
			else {
				if (player.getInventory().getItemCountByItemId(169660000) > 0) // gender switch ticket normal
					player.getInventory().decreaseByItemId(169660000, 1);
				else if (player.getInventory().getItemCountByItemId(169660001) > 0) // gender switch ticket event
					player.getInventory().decreaseByItemId(169660001, 1);
				DAOManager.getDAO(PlayerDAO.class).storePlayer(player); // save new gender
			}
			DAOManager.getDAO(PlayerAppearanceDAO.class).store(player); // save new appearance
		}
	}
}
