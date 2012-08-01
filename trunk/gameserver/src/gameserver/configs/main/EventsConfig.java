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
 * @author Rolandas
 */
public class EventsConfig {

	/**
	 * Event Enabled
	 */
	@Property(key = "gameserver.event.enable", defaultValue = "false")
	public static boolean EVENT_ENABLED;

	/**
	 * Event Rewarding Membership
	 */
	@Property(key = "gameserver.event.membership", defaultValue = "0")
	public static int EVENT_REWARD_MEMBERSHIP;

	@Property(key = "gameserver.event.membership.rate", defaultValue = "false")
	public static boolean EVENT_REWARD_MEMBERSHIP_RATE;

	/**
	 * Event Rewarding Period
	 */
	@Property(key = "gameserver.event.period", defaultValue = "60")
	public static int EVENT_PERIOD;

	/**
	 * Event Boss Invasion by Khaos (www.diamondcore-mmorpgs.com)
	 */
	@Property(key = "gameserver.invasion.enable", defaultValue = "false")
	public static boolean INVASION_ENABLED;

	@Property(key = "gameserver.starter.hour", defaultValue = "17")
	public static int HOUR;

	@Property(key = "gameserver.starter.hour2", defaultValue = "19")
	public static int HOUR2;

	@Property(key = "gameserver.starter.hour3", defaultValue = "21")
	public static int HOUR3;

	@Property(key = "gameserver.starter.hour4", defaultValue = "23")
	public static int HOUR4;

	/**
	 * Event Reward Values
	 */
	@Property(key = "gameserver.event.item", defaultValue = "141000001")
	public static int EVENT_ITEM;
	
	@Property(key = "gameserver.events.givejuice", defaultValue = "160009017")
	public static int EVENT_GIVEJUICE;
	
	@Property(key = "gameserver.events.givecake", defaultValue = "160010073")
	public static int EVENT_GIVECAKE;

	@Property(key = "gameserver.event.count", defaultValue = "1")
	public static int EVENT_ITEM_COUNT;

	@Property(key = "gameserver.event.service.enable", defaultValue = "false")
	public static boolean ENABLE_EVENT_SERVICE;
	/**
	 *TvT Event configuration
	 */
	@Property(key = "gameserver.tvtevent.enable", defaultValue = "true")
	public static boolean TVT_ENABLE;
	@Property(key = "gameserver.tvtevent.min.players", defaultValue = "6")
	public static int TVT_MIN_PLAYERS;
	@Property(key = "gameserver.tvtevent.skill.use", defaultValue = "9833")
	public static int TVT_SKILL_USE;
        
        @Property(key = "gameserver.tvtevent.winner.reward", defaultValue = "188051136")
        public static int TVT_WINNER_REWARD;
        @Property(key = "gameserver.tvtevent.winner.dublereward", defaultValue = "188051135")
        public static int TVT_WINNER_DOUBLEREWARD;
        @Property(key = "gameserver.tvtevent.winner.number", defaultValue = "1")
        public static int TVT_WINNER_NUMBER;

}
