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

import java.sql.Timestamp;
import java.util.List;

import commons.database.dao.DAOManager;
import gameserver.configs.main.GSConfig;
import gameserver.configs.main.MembershipConfig;
import gameserver.dao.InventoryDAO;
import gameserver.model.Gender;
import gameserver.model.PlayerClass;
import gameserver.model.Race;
import gameserver.model.account.Account;
import gameserver.model.account.PlayerAccountData;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerAppearance;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import gameserver.services.player.PlayerService;
import gameserver.utils.Util;
import gameserver.utils.idfactory.IDFactory;

/**
 * In this packets aion client is requesting creation of character.
 * 
 * @author -Nemesiss-
 * @modified cura
 */
public class CM_CREATE_CHARACTER extends AionClientPacket {

	/** Character appearance */
	private PlayerAppearance playerAppearance;
	/** Player base data */
	private PlayerCommonData playerCommonData;

	/**
	 * Constructs new instance of <tt>CM_CREATE_CHARACTER </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_CREATE_CHARACTER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readD(); // ignored for now
		readS(); // something + accointId

		playerCommonData = new PlayerCommonData(IDFactory.getInstance().nextId());
		String name = Util.convertName(readS());

		playerCommonData.setName(name);

		readB(50 - (name.length() * 2)); // some shit? 2.5.x

		playerCommonData.setLevel(GSConfig.LOGIN_LEVEL);
		playerCommonData.setGender(readD() == 0 ? Gender.MALE : Gender.FEMALE);
		playerCommonData.setRace(readD() == 0 ? Race.ELYOS : Race.ASMODIANS);
		playerCommonData.setPlayerClass(PlayerClass.getPlayerClassById((byte) readD()));

		if (getConnection().getAccount().getMembership() >= MembershipConfig.STIGMA_SLOT_QUEST) {
			playerCommonData.setAdvancedStigmaSlotSize(11);
		}

		playerAppearance = new PlayerAppearance();

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
	 * Actually does the dirty job
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();

		Account account = client.getAccount();

		/* Some reasons why player can' be created */
		if (client.getActivePlayer() != null) {
			return;
		}
		if (account.getMembership() >= MembershipConfig.CHARACTER_ADDITIONAL_ENABLE) {
			if (MembershipConfig.CHARACTER_ADDITIONAL_COUNT <= account.size()) {
				client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
				IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
				return;
			}
		}
		else if (GSConfig.CHARACTER_LIMIT_COUNT <= account.size()) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_SERVER_LIMIT_EXCEEDED));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if (!PlayerService.isFreeName(playerCommonData.getName())) {
			if (GSConfig.CHARACTER_FACTIONS_MODE == 2)
				client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_RESERVED));
			else
				client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if (PlayerService.isOldName(playerCommonData.getName())) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if (!PlayerService.isValidName(playerCommonData.getName())) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if (!playerCommonData.getPlayerClass().isStartingClass()) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if (GSConfig.CHARACTER_FACTIONS_MODE == 0) {
			for (PlayerAccountData data : account.getSortedAccountsList()) {
				if (data.getPlayerCommonData().getRace() != playerCommonData.getRace()) {
					client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
					IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
					return;
				}
			}
		}
		Player player = PlayerService.newPlayer(playerCommonData, playerAppearance, account);

		if (!PlayerService.storeNewPlayer(player, account.getName(), account.getId())) {
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
			IDFactory.getInstance().releaseId(playerCommonData.getPlayerObjId());
		}
		else {
			List<Item> equipment = DAOManager.getDAO(InventoryDAO.class).loadEquipment(player.getObjectId());
			PlayerAccountData accPlData = new PlayerAccountData(playerCommonData, null, playerAppearance, equipment, null);

			accPlData.setCreationDate(new Timestamp(System.currentTimeMillis()));
			PlayerService.storeCreationTime(player.getObjectId(), accPlData.getCreationDate());

			account.addPlayerAccountData(accPlData);
			client.sendPacket(new SM_CREATE_CHARACTER(accPlData, SM_CREATE_CHARACTER.RESPONSE_OK));
		}
	}
}
