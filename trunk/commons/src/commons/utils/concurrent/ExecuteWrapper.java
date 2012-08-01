/*
 * This file is part of Aion Extreme  Emulator <aion-core.net>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package commons.utils.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javolution.text.TextBuilder;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import commons.configs.CommonsConfig;

/**
 * @author NB4L1
 */
public class ExecuteWrapper implements Executor{

	private static final Logger log = LoggerFactory.getLogger(ExecuteWrapper.class);

	@Override
	public void execute(Runnable runnable) {
		execute(runnable, Long.MAX_VALUE);
	}

	public static void execute(Runnable runnable, long maximumRuntimeInMillisecWithoutWarning) {
		long begin = System.nanoTime();

		try {
			runnable.run();
		}
		catch (Throwable t) {
			log.warn("Exception in a Runnable execution:", t);
		}
		finally {

			long runtimeInNanosec = System.nanoTime() - begin;
			Class<? extends Runnable> clazz = runnable.getClass();

			if (CommonsConfig.RUNNABLESTATS_ENABLE) {
				RunnableStatsManager.handleStats(clazz, runtimeInNanosec);
			}

			long runtimeInMillisec = TimeUnit.NANOSECONDS.toMillis(runtimeInNanosec);
			if (runtimeInMillisec > maximumRuntimeInMillisecWithoutWarning) {
				TextBuilder tb = TextBuilder.newInstance();
				tb.append(clazz);
				tb.append(" - execution time: ");
				tb.append(runtimeInMillisec);
				tb.append("msec");
				log.warn(tb.toString());
			}
		}
	}
}
