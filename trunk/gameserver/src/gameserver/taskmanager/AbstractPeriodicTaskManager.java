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
package gameserver.taskmanager;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import commons.taskmanager.AbstractLockManager;
import commons.utils.Rnd;
import gameserver.GameServer;
import gameserver.GameServer.StartupHook;
import gameserver.utils.ThreadPoolManager;

/**
 * @author lord_rex and MrPoke based on l2j-free engines. This can be used for periodic calls.
 */
public abstract class AbstractPeriodicTaskManager extends AbstractLockManager implements Runnable, StartupHook {

	protected static final Logger log = LoggerFactory.getLogger(AbstractPeriodicTaskManager.class);

	private final int period;

	public AbstractPeriodicTaskManager(int period) {
		this.period = period;

		GameServer.addStartupHook(this);

		log.info(getClass().getSimpleName() + ": Initialized.");
	}

	@Override
	public final void onStartup() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000 + Rnd.get(period), Rnd.get(period - 5, period + 5));
	}

	@Override
	public abstract void run();
}
