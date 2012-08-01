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
package gameserver.configs.administration;

import commons.configuration.Property;

/**
 * @author vlog
 * TEMPORARY fix
 */
public class CommandsConfig {

	@Property(key = "add", defaultValue = "3")
	public static int ADD;
	@Property(key = "adddrop", defaultValue = "3")
	public static int ADDDROP;
	@Property(key = "addexp", defaultValue = "3")
	public static int ADDEXP;
	@Property(key = "addset", defaultValue = "3")
	public static int ADDSET;
	@Property(key = "addskill", defaultValue = "3")
	public static int ADDSKILL;
	@Property(key = "addtitle", defaultValue = "3")
	public static int ADDTITLE;
	@Property(key = "admin", defaultValue = "3")
	public static int ADMIN;
	@Property(key = "ai2", defaultValue = "3")
	public static int AI2;
	@Property(key = "announce", defaultValue = "3")
	public static int ANNONCE;
	@Property(key = "announcements", defaultValue = "3")
	public static int ANNONCEMENTS;
	@Property(key = "announcefaction", defaultValue = "3")
	public static int ANNONCEFACTION;
	@Property(key = "appearance", defaultValue = "3")
	public static int APPEARANCE;
	@Property(key = "ban", defaultValue = "3")
	public static int BAN;
	@Property(key = "banchar", defaultValue = "3")
	public static int BANCHAR;
	@Property(key = "banip", defaultValue = "3")
	public static int BANIP;
	@Property(key = "banmac", defaultValue = "3")
	public static int BANMAC;
	@Property(key = "bk", defaultValue = "3")
	public static int BK;
	@Property(key = "configure", defaultValue = "3")
	public static int CONFIGURE;
	@Property(key = "cooldown", defaultValue = "3")
	public static int COOLDOWN;
	@Property(key = "delete", defaultValue = "3")
	public static int DELETE;
	@Property(key = "delskill", defaultValue = "3")
	public static int DELSKILL;
	@Property(key = "dispel", defaultValue = "3")
	public static int DISPELL;
	@Property(key = "dropinfo", defaultValue = "3")
	public static int DROPINFO;
	@Property(key = "dye", defaultValue = "3")
	public static int DYE;
	@Property(key = "enemy", defaultValue = "3")
	public static int ENEMY;
	@Property(key = "equip", defaultValue = "3")
	public static int EQUIP;	
	@Property(key = "fixpath", defaultValue = "3")
	public static int FIXPATH;
	@Property(key = "fsc", defaultValue = "3")
	public static int FSC;
	@Property(key = "gag", defaultValue = "3")
	public static int GAG;
	@Property(key = "gameshop", defaultValue = "3")
	public static int GAMESHOP;
	@Property(key = "gmlist", defaultValue = "3")
	public static int GMLIST;
	@Property(key = "goto", defaultValue = "3")
	public static int GOTO;
	@Property(key = "grouptome", defaultValue = "3")
	public static int GROUPTOME;
	@Property(key = "heal", defaultValue = "3")
	public static int HEAL;
	@Property(key = "html", defaultValue = "3")
	public static int HTML;
	@Property(key = "info", defaultValue = "3")
	public static int INFO;
	@Property(key = "invis", defaultValue = "3")
	public static int INVIS;
	@Property(key = "invul", defaultValue = "3")
	public static int INVUL;
	@Property(key = "kick", defaultValue = "3")
	public static int KICK;
	@Property(key = "kill", defaultValue = "3")
	public static int KILL;
	@Property(key = "kinah", defaultValue = "3")
	public static int KINAH;
	@Property(key = "legion", defaultValue = "3")
	public static int LEGION;
	@Property(key = "morph", defaultValue = "3")
	public static int MORPH;
	@Property(key = "motion", defaultValue = "3")
	public static int MOTION;
	@Property(key = "moveto", defaultValue = "3")
	public static int MOVETO;
	@Property(key = "moveplayertoplayer", defaultValue = "3")
	public static int MOVEPLAYERTOPLAYER;
	@Property(key = "movetome", defaultValue = "3")
	public static int MOVETOME;
	@Property(key = "movetomeall", defaultValue = "3")
	public static int MOVETOMEALL;
	@Property(key = "movetonpc", defaultValue = "3")
	public static int MOVETONPC;
	@Property(key = "movetoobject", defaultValue = "3")
	public static int MOVETOOBJECT;
	@Property(key = "movetoplayer", defaultValue = "3")
	public static int MOVETOPLAYER;
	@Property(key = "movie", defaultValue = "3")
	public static int MOVIE;
	@Property(key = "neutral", defaultValue = "3")
	public static int NEUTRAL;
	@Property(key = "notice", defaultValue = "3")
	public static int NOTICE;
	@Property(key = "npcskill", defaultValue = "3")
	public static int NPCSKILL;
	@Property(key = "passkeyreset", defaultValue = "3")
	public static int PASSKEYRESET;
	@Property(key = "petition", defaultValue = "3")
	public static int PETITION;
	@Property(key = "playerinfo", defaultValue = "3")
	public static int PLAYERINFO;
	@Property(key = "powerup", defaultValue = "3")
	public static int POWERUP;
	@Property(key = "promote", defaultValue = "3")
	public static int PROMOTE;
	@Property(key = "quest", defaultValue = "3")
	public static int QUEST;
	@Property(key = "raw", defaultValue = "3")
	public static int RAW;
	@Property(key = "reload", defaultValue = "3")
	public static int RELOAD;
	@Property(key = "reload_spawn", defaultValue = "3")
	public static int RELOADSPAWN;
	@Property(key = "remove", defaultValue = "3")
	public static int REMOVE;
	@Property(key = "rename", defaultValue = "3")
	public static int RENAME;
	@Property(key = "res", defaultValue = "3")
	public static int RES;
	@Property(key = "revoke", defaultValue = "3")
	public static int REVOKE;
	@Property(key = "removecd", defaultValue = "3")
	public static int REMOVECD;
	@Property(key = "ring", defaultValue = "3")
	public static int RING;
	@Property(key = "rprison", defaultValue = "3")
	public static int RPRISON;
	@Property(key = "say", defaultValue = "3")
	public static int SAY;
	@Property(key = "send", defaultValue = "3")
	public static int SEND;
	@Property(key = "set", defaultValue = "3")
	public static int SET;
	@Property(key = "setrace", defaultValue = "3")
	public static int SETRACE;
	@Property(key = "siege", defaultValue = "3")
	public static int SIEGE;
	@Property(key = "spawn", defaultValue = "3")
	public static int SPAWN;
	@Property(key = "speed", defaultValue = "3")
	public static int SPEED;
	@Property(key = "state", defaultValue = "3")
	public static int STATE;
	@Property(key = "sprison", defaultValue = "3")
	public static int SPRISON;
	@Property(key = "sys", defaultValue = "3")
	public static int SYS;
	@Property(key = "sysmail", defaultValue = "3")
	public static int SYSMAIL;
	@Property(key = "teleportation", defaultValue = "3")
	public static int TELEPORTATION;
	@Property(key = "time", defaultValue = "3")
	public static int TIME;
	@Property(key = "unban", defaultValue = "3")
	public static int UNBAN;
	@Property(key = "unbanchar", defaultValue = "3")
	public static int UNBANCHAR;
	@Property(key = "unbanip", defaultValue = "3")
	public static int UNBANIP;
	@Property(key = "ungag", defaultValue = "3")
	public static int UNGAG;
	@Property(key = "unstuck", defaultValue = "3")
	public static int UNSTUCK;
	@Property(key = "useskill", defaultValue = "3")
	public static int USESKILL;
	@Property(key = "warp", defaultValue = "3")
	public static int WARP;
	@Property(key = "wc", defaultValue = "3")
	public static int WC;
	@Property(key = "weather", defaultValue = "3")
	public static int WEATHER;
	@Property(key = "whisper", defaultValue = "3")
	public static int WHISPER;
	@Property(key = "zone", defaultValue = "3")
	public static int ZONE;
	@Property(key = "channel", defaultValue = "3")
	public static int CHANNEL;
	@Property(key = "gps", defaultValue = "3")
	public static int GPS;
	@Property(key = "bemypet", defaultValue = "3")
	public static int BEMYPET;
	@Property(key = "door", defaultValue = "3")
	public static int DOOR;
	@Property(key = "tvt2", defaultValue = "3")
	public static int TVT2;
	// Player related
	@Property(key = "faction", defaultValue = "0")
	public static int FACTION;
	@Property(key = "mcheck", defaultValue = "0")
	public static int MCHECK;
	@Property(key = "noexp", defaultValue = "0")
	public static int NOEXP;
	@Property(key = "questauto", defaultValue = "0")
	public static int QUESTAUTO;
	@Property(key = "toll", defaultValue = "0")
	public static int TOLL;
    @Property(key = "tvt", defaultValue = "0")
	public static int TVT;
	@Property(key = "start", defaultValue = "0")
	public static int START;
	@Property(key = "shiva", defaultValue = "0")
	public static int COMMAND_ENCHANT;	
	@Property(key = "shop", defaultValue = "0")
	public static int SHOP;	
	@Property(key = "paint", defaultValue = "0")
	public static int COMMAND_PAINT;	
	@Property(key = "job", defaultValue = "0")
	public static int COMMAND_JOB;
	@Property(key = "cube", defaultValue = "0")
	public static int COMMAND_CUBE;
	@Property(key = "skills", defaultValue = "3")
	public static int SKILLS;
	@Property(key = "go", defaultValue = "3")
	public static int GO;

}
