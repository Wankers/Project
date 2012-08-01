/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import commons.configuration.Property;

public class TaskManagerConfig {

	/**
	 * Enable deadlock detector
	 */
	@Property(key = "gameserver.deadlock.enable", defaultValue = "false")
	public static boolean DEADLOCK_DETECTOR_ENABLED;

	/**
	 * Interval for deadlock detector run schedule
	 */
	@Property(key = "gameserver.deadlock.interval", defaultValue = "300")
	public static int DEADLOCK_DETECTOR_INTERVAL;
}
