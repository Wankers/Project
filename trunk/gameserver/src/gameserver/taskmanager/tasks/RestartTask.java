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
package gameserver.taskmanager.tasks;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import gameserver.ShutdownHook;
import gameserver.ShutdownHook.ShutdownMode;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.tasks.TaskFromDBHandler;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;

/**
 * @author Divinity
 */
public class RestartTask extends TaskFromDBHandler {

	private static final Logger log = LoggerFactory.getLogger(RestartTask.class);

	private int countDown;
	private int announceInterval;
	private int warnCountDown;

	@Override
	public String getTaskName() {
		return "restart";
	}

	@Override
	public boolean isValid() {
		return params.length == 3;
	}

	@Override
	public void run() {
		log.info("Task[" + id + "] launched : restarting the server !");
		setLastActivation();

		countDown = Integer.parseInt(params[0]);
		announceInterval = Integer.parseInt(params[1]);
		warnCountDown = Integer.parseInt(params[2]);

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "Automatic Task: The server will restart in " + warnCountDown
					+ " seconds ! Please find a safe place and disconnect your character.");
			}
		});

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				ShutdownHook.getInstance().doShutdown(countDown, announceInterval, ShutdownMode.RESTART);
			}
		}, warnCountDown * 1000);
	}
}
