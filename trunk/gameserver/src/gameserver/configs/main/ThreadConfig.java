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
package gameserver.configs.main;

import commons.configuration.Property;

/**
 * @author lord_rex
 */
public class ThreadConfig {

	/**
	 * Thread basepoolsize
	 */
	@Property(key = "gameserver.thread.basepoolsize", defaultValue = "2")
	public static int BASE_THREAD_POOL_SIZE;
	/**
	 * Thread threadpercore
	 */
	@Property(key = "gameserver.thread.threadpercore", defaultValue = "4")
	public static int EXTRA_THREAD_PER_CORE;
	/**
	 * Thread runtime
	 */
	@Property(key = "gameserver.thread.runtime", defaultValue = "5000")
	public static long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;
	public static int THREAD_POOL_SIZE;

	public static void load() {
		final int baseThreadPoolSize = BASE_THREAD_POOL_SIZE;
		final int extraThreadPerCore = EXTRA_THREAD_PER_CORE;

		THREAD_POOL_SIZE = baseThreadPoolSize + Runtime.getRuntime().availableProcessors() * extraThreadPerCore;
	}
}
