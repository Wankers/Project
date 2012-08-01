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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.configs.main.GSConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.AionConnection.State;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.network.aion.serverpackets.SM_CAPTCHA;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.PunishmentService;
import gameserver.utils.PacketSendUtility;

/**
 * @author Cura
 */
public class CM_CAPTCHA extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_CAPTCHA.class);

	private int type;
	private int count;
	private String word;

	/**
	 * @param opcode
	 */
	public CM_CAPTCHA(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = readC();

		switch (type) {
			case 0x02:
				count = readC();
				word = readS();
				break;
			default:
				log.warn("Unknown CAPTCHA packet type? 0x" + Integer.toHexString(type).toUpperCase());
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		switch (type) {
			case 0x02:
				if (player.getCaptchaWord().equalsIgnoreCase(word)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400270));
					PacketSendUtility.sendPacket(player, new SM_CAPTCHA(true, 0));

					PunishmentService.setIsNotGatherable(player, 0, false, 0);

					// fp bonus (like retail)
					player.getLifeStats().increaseFp(TYPE.FP, GSConfig.CAPTCHA_BONUS_FP_TIME);
				}
				else {
					int banTime = GSConfig.CAPTCHA_EXTRACTION_BAN_TIME + (GSConfig.CAPTCHA_EXTRACTION_BAN_ADD_TIME * count);

					if (count < 3) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400271, 3 - count));
						PacketSendUtility.sendPacket(player, new SM_CAPTCHA(false, banTime));
						PunishmentService.setIsNotGatherable(player, count, true, banTime * 1000L);
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400272));
						PunishmentService.setIsNotGatherable(player, count, true, banTime * 1000L);
					}
				}
				break;
		}
	}
}
