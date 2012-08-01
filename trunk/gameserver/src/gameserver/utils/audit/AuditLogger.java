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
package gameserver.utils.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.LoggingConfig;
import gameserver.configs.main.PunishmentConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.audit.AutoBan;
import com.google.common.base.Preconditions;

/**
 * @author MrPoke
 */
public class AuditLogger {

	private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);

	public static final void info(Player player, String message) {
		Preconditions.checkNotNull(player, "Player should not be null or use different info method");
		if (LoggingConfig.LOG_AUDIT) {
			info(player.getName(), player.getObjectId(), message);
		}
		if (PunishmentConfig.PUNISHMENT_ENABLE) {
			AutoBan.punishment(player, message);
		}
	}

	public static final void info(String playerName, int objectId, String message) {
			message += " Player name: " + playerName + " objectId: " + objectId;
			log.info(message);

			if (CustomConfig.GM_AUDIT_MESSAGE_BROADCAST)
				GMService.getInstance().broadcastMesage(message);
	}
}
