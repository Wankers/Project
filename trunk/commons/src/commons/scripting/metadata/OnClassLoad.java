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
package commons.scripting.metadata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method marked as {@link OnClassLoad} will be called when class was loaded by script.<br>
 * It's more useful alternative for
 * 
 * <pre>
 * static {
 * 	...
 * }
 * </pre>
 * 
 * block.<br>
 * <br>
 * Only static methods with no arguments can be marked with this annotation.<br>
 * This is only used if {@link commons.scripting.ScriptContext#getClassListener()} returns instance of
 * {@link commons.scripting.classlistener.OnClassLoadUnloadListener} subclass.
 * 
 * @author SoulKeeper
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClassLoad {
}
