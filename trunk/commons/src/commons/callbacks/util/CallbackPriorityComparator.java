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

package commons.callbacks.util;

import commons.callbacks.Callback;

import java.util.Comparator;

/**
 * Compares priority of two callbacks.<br>
 * It's not necessary for callback to implement {@link com.aionemu.commons.callbacks.CallbackPriority} for callback
 * to has the priority
 *
 * @author SoulKeeper
 */
public class CallbackPriorityComparator implements Comparator<Callback<?>> {

    @Override
    public int compare(Callback<?> o1, Callback<?> o2) {
        int p1 = CallbacksUtil.getCallbackPriority(o1);
        int p2 = CallbacksUtil.getCallbackPriority(o2);

        if (p1 < p2) {
            return -1;
        } else if (p1 == p2) {
            return 0;
        } else {
            return 1;
        }
    }
}
