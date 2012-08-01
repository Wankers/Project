/*
* This file is part of Aion Extreme Emulator <aion-core.net>.
 *
*  Aion-Extreme Emulator is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
*  Aion-Extreme Emulator is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
*  along with Aion-Extreme Emulator.  If not, see <http://www.gnu.org/licenses/>.
 */
package slf4j.filters;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


/**
 * @author zhkchi
 *
 */
public class ChatLogFilter extends Filter<ILoggingEvent> {

	/**
	 * Decides what to do with logging event.<br>
	 * This method accepts only log events that contain exceptions.
	 * 
	 * @param loggingEvent
	 *          log event that is going to be filtred.
	 * @return {@link org.apache.log4j.spi.Filter#ACCEPT} if chatlog, {@link org.apache.log4j.spi.Filter#DENY} otherwise
	 */
	@Override
	public FilterReply decide(ILoggingEvent loggingEvent) {
		Object message = loggingEvent.getMessage();

		if (((String) message).startsWith("[MESSAGE]")) {
			return FilterReply.ACCEPT;
		}

		return FilterReply.DENY;
	}
}
