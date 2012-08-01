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

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * @author NB4L1
 */
public final class AionRejectedExecutionHandler implements RejectedExecutionHandler {

	private static final Logger log = LoggerFactory.getLogger(AionRejectedExecutionHandler.class);

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (executor.isShutdown())
			return;

		log.warn(r + " from " + executor, new RejectedExecutionException());

		if (Thread.currentThread().getPriority() > Thread.NORM_PRIORITY)
			new Thread(r).start();
		else
			r.run();
	}
}
