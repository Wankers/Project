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

public class CustomConfig {

	@Property(key = "gameserver.phx.speedhack", defaultValue = "false")
	public static boolean PHX_SPEEDHACK;

	@Property(key = "gameserver.phx.speedhack.power", defaultValue = "41")
	public static int PHX_SPEEDHACK_POWER;

	@Property(key = "gameserver.phx.speedhack.punish", defaultValue = "1")
	public static int PHX_SPEEDHACK_PUNISH = 0;

	/**
	 * Show premium account details on login
	 */
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;
        /**
         * All Npcs Despawn with Beam!
         */
    @Property(key = "gameserver.related.beamdespawn", defaultValue = "false")
	public static boolean RELATED_BEAM_DESPAWN;

	/**
	 * Enable announce when a player succes enchant item 15
	 */
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;

	/**
	 * Enable speaking between factions
	 */
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;

	/**
	 * Minimum level to use whisper
	 */
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "10")
	public static int LEVEL_TO_WHISPER;

	/**
	 * Factions search mode
	 */
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;

	/**
	 * list gm when search players
	 */
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;

	/**
	 * Minimum level to use search
	 */
	@Property(key = "gameserver.search.player.level", defaultValue = "10")
	public static int LEVEL_TO_SEARCH;

	/**
	 * Allow opposite factions to bind in enemy territories
	 */
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;

	/**
	 * Enable second class change without quest
	 */
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "true")
	public static boolean ENABLE_SIMPLE_2NDCLASS;

	/**
	 * Disable chain trigger rate (chain skill with 100% success)
	 */
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;

	/**
	 * Unstuck delay
	 */
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;

	/**
	 * The price for using dye command
	 */
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;

	/**
	 * Base Fly Time
	 */
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int BASE_FLYTIME;

	/**
	 * Disable prevention using old names with coupon & command
	 */
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;

	/**
	 * Friendlist size
	 */
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "40")
	public static int BASIC_QUEST_SIZE_LIMIT;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.cubesize.limit", defaultValue = "9")
	public static int BASIC_CUBE_SIZE_LIMIT;
	
	 /**
	 * Npc Cube Expands limit size
	 */
	@Property(key = "gameserver.npcexpands.limit", defaultValue = "5")
	public static int NPC_CUBE_EXPANDS_SIZE_LIMIT;

	/**
	 * Enable instances
	 */
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;

	/**
	 * Enable instances mob always aggro player ignore level
	 */
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300080000,300090000,300060000")
	public static String INSTANCES_MOB_AGGRO;

	/**
	 * Enable instances cooldown filtring
	 */
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;

	/**
	 * Instances formula
	 */
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;

	/**
	 * Enable Kinah cap
	 */
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;

	/**
	 * Kinah cap value
	 */
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "999999999")
	public static long KINAH_CAP_VALUE;

	/**
	 * Enable AP cap
	 */
	@Property(key = "gameserver.enable.ap.cap", defaultValue = "false")
	public static boolean ENABLE_AP_CAP;

	/**
	 * AP cap value
	 */
	@Property(key = "gameserver.ap.cap.value", defaultValue = "1000000")
	public static long AP_CAP_VALUE;

	/**
	 * Enable no AP in mentored group.
	 */
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;

	/**
	 * .faction cfg
	 */
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;

	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;
	
	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;

	/**
	 * Time in milliseconds in which players are limited for killing one player
	 */
	@Property(key = "gameserver.pvp.dayduration", defaultValue = "86400000")
	public static long PVP_DAY_DURATION;
	
	/**
	 * Allowed Kills in configuered time for full AP. Move to separate config when more pvp options.
	 */
	@Property(key = "gameserver.pvp.maxkills", defaultValue = "5")
	public static int MAX_DAILY_PVP_KILLS;

	/**
	 * Add a reward to player for pvp kills
	 */
	@Property(key = "gameserver.kill.reward.enable", defaultValue = "false")
	public static boolean ENABLE_KILL_REWARD;

	/**
	 * Kills needed for item reward
	 */
	@Property(key = "gameserver.kills.needed1", defaultValue = "5")
	public static int KILLS_NEEDED1;
	@Property(key = "gameserver.kills.needed2", defaultValue = "10")
	public static int KILLS_NEEDED2;
	@Property(key = "gameserver.kills.needed3", defaultValue = "15")
	public static int KILLS_NEEDED3;

	/**
	 * Item Rewards
	 */
	@Property(key = "gameserver.item.reward1", defaultValue = "186000031")
	public static int REWARD1;
	@Property(key = "gameserver.item.reward2", defaultValue = "186000030")
	public static int REWARD2;
	@Property(key = "gameserver.item.reward3", defaultValue = "186000096")
	public static int REWARD3;

	/**
	 * Show dialog id and quest id
	 */
	@Property(key = "gameserver.dialog.showid", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOGID;

	/**
	 * Custom RiftLevels for Heiron and Beluslan
	 */
	@Property(key = "gameserver.rift.heiron_fm", defaultValue = "50")
	public static int HEIRON_FM;
	@Property(key = "gameserver.rift.heiron_gm", defaultValue = "50")
	public static int HEIRON_GM;
	@Property(key = "gameserver.rift.beluslan_fm", defaultValue = "50")
	public static int BELUSLAN_FM;
	@Property(key = "gameserver.rift.beluslan_gm", defaultValue = "50")
	public static int BELUSLAN_GM;

	@Property(key = "gameserver.survey.delay.minute", defaultValue = "20")
	public static int SURVEY_DELAY;

	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;
	
	/**
	 * Flood Protection
	 */
	@Property(key = "gameserver.flood.delay", defaultValue = "1")
	public static int FLOOD_DELAY;

	@Property(key = "gameserver.flood.msg", defaultValue = "6")
	public static int FLOOD_MSG;

	/**
	 * Limits Config
	 */
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;

	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 * * ?")
	public static String LIMITS_UPDATE;

	@Property(key = "gameserver.limits.sell", defaultValue = "12900000")
	public static long LIMITS_SELL;

	@Property(key = "gameserver.gmaudit.message.broadcast", defaultValue = "false")
	public static boolean GM_AUDIT_MESSAGE_BROADCAST;

	@Property(key = "gameserver.chat.text.length", defaultValue = "150")
	public static int MAX_CHAT_TEXT_LENGHT;
	
	@Property(key = "gameserver.instance.keycheck", defaultValue = "false")
	public static boolean INSTANCE_KEYCHECK;
	
	@Property(key = "gameserver.pff.enable", defaultValue = "false")
	public static boolean PFF_ENABLE;
	
	@Property(key = "gameserver.pff.level", defaultValue = "1")
	public static int PFF_LEVEL;

	@Property(key = "gameserver.autoassault.enable", defaultValue = "false")
	public static boolean AUTO_ASSAULT;
	
	@Property(key = "gameserver.abyssxform.afterlogout", defaultValue = "false")
	public static boolean ABYSSXFORM_LOGOUT;
	
	/**
	 * Dark Poeta configuration
	 */
	@Property(key = "gameserver.darkpoeta.reward.point.rate", defaultValue = "1.0")
	public static float DARKPOETA_REWARD_POINT_RATE;

	@Property(key = "gameserver.darkpoeta.grade.S.time", defaultValue = "7200")
	public static int DARKPOETA_GRADE_S_TIME;

	@Property(key = "gameserver.darkpoeta.grade.S.points", defaultValue = "20000")
	public static int DARKPOETA_GRADE_S_POINTS;

	@Property(key = "gameserver.darkpoeta.grade.A.time", defaultValue = "5400")
	public static int DARKPOETA_GRADE_A_TIME;

	@Property(key = "gameserver.darkpoeta.grade.A.points", defaultValue = "17100")
	public static int DARKPOETA_GRADE_A_POINTS;

	@Property(key = "gameserver.darkpoeta.grade.B.time", defaultValue = "3600")
	public static int DARKPOETA_GRADE_B_TIME;

	@Property(key = "gameserver.darkpoeta.grade.B.points", defaultValue = "13100")
	public static int DARKPOETA_GRADE_B_POINTS;

	@Property(key = "gameserver.darkpoeta.grade.C.time", defaultValue = "1800")
	public static int DARKPOETA_GRADE_C_TIME;

	@Property(key = "gameserver.darkpoeta.grade.C.points", defaultValue = "11000")
	public static int DARKPOETA_GRADE_C_POINTS;
	
	@Property(key = "gameserver.portal.quests", defaultValue = "true")
	public static boolean PORTAL_QUESTS;

	/**
	* AdvStigma
	*/
	@Property(key = "gameserver.advstigmaslot.onlvlup", defaultValue = "false")
	public static boolean ADVSTIGMA_ONLVLUP;

	/**
	 * Anti channel Spam by Khaos [message]
	 */
	@Property(key = "gameserver.chat.talkdelay", defaultValue = "30")
	public static int TALK_DELAY;
	/*
    * Auto Restart Server Service configs.
    */
	@Property(key = "gameserver.autorestart.enable", defaultValue = "false")
	public static boolean ENABLE_AUTO_RESTART;
    
	@Property(key = "gameserver.autorestart.delay", defaultValue = "7")
	public static int AUTORESTART_DELAY;
}
