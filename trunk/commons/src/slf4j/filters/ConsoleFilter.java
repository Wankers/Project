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
package slf4j.filters;

import ch.qos.logback.classic.spi.ILoggingEvent; 
import ch.qos.logback.core.filter.Filter; 
import ch.qos.logback.core.spi.FilterReply; 

/**
 * @author zhkchi
 *
 */
public class ConsoleFilter extends Filter<ILoggingEvent>{

	@Override 
  public FilterReply decide(ILoggingEvent event) {     
    if (event.getMessage().startsWith("[MESSAGE]") || event.getMessage().startsWith("[ITEM]")
    	|| event.getMessage().startsWith("[ADMIN COMMAND]") || event.getMessage().startsWith("[AUDIT]")) { 
      return FilterReply.DENY;
    } else { 
      return FilterReply.ACCEPT;
    } 
  }
}
