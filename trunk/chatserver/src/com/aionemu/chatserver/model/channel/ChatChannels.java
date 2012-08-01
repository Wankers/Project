/**
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.aionemu.chatserver.model.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.chatserver.configs.Config;
import com.aionemu.chatserver.model.Gender;
import com.aionemu.chatserver.model.PlayerClass;
import com.aionemu.chatserver.model.Race;
import com.aionemu.chatserver.model.WorldMapType;
import com.aionemu.chatserver.service.GameServerService;

/**
 * @author ATracer
 */
public class ChatChannels {

	private static final Logger log = LoggerFactory.getLogger(ChatChannels.class);

	private static final List<Channel> channels = new ArrayList<Channel>();

	static {
		// LFG channels
		addGroupChannel("partyFind_PF");
		// TRADE
		addTradeChannel("trade_LF1"); // poeta
		addTradeChannel("trade_LF1A");// verteron
		addTradeChannel("trade_LC1"); // sanctum
		addTradeChannel("trade_LF2");// eltnen
		addTradeChannel("trade_LF2A"); // theobomos
		addTradeChannel("trade_LF3"); // heiron
		addTradeChannel("trade_DF1"); // ishalgen
		addTradeChannel("trade_DF2"); // morheim
		addTradeChannel("trade_DC1"); // pandaemonium
		addTradeChannel("trade_DF3"); // beluslan
		addTradeChannel("trade_DF1A"); // altgard
		addTradeChannel("trade_DF2A"); // brusthonin
		addTradeChannel("trade_Ab1"); // abyss
		addTradeChannel("trade_LF4"); // inggison
		addTradeChannel("trade_DF4"); // gelkmaros
		addTradeChannel("trade_LC2"); // kaisinel
		addTradeChannel("trade_DC2"); // marchutan
		addTradeChannel("trade_IDCatacombs"); // beshmundir
		addTradeChannel("trade_Underpass"); // silentera
		addTradeChannel("trade_IDshulackShip"); // steelrake
		addTradeChannel("trade_IDLF1"); // dark poeta
		addTradeChannel("trade_IDDF3_Dragon"); // Draupnir cave
		addTradeChannel("trade_IDCromede"); // cromede
		addTradeChannel("trade_IDAb1_MiniCastle"); // nochsana training camp
		addTradeChannel("trade_IDNovice"); // haramel
		addTradeChannel("trade_IDDF2_Dflame"); // fire temple
		addTradeChannel("trade_IDDf2a_Adma"); // adma stronghold
		addTradeChannel("trade_IDDF3_LP"); // alquimia research center
		addTradeChannel("trade_IDElim"); // taloc's hollow
		addTradeChannel("trade_IDTemple_Up"); // udas temple
		addTradeChannel("trade_IDTemple_Low"); // lower udas temple
		addTradeChannel("trade_IDAbRe_Low_Eciel"); // right wing chamber
		addTradeChannel("trade_IDAbRe_Low_Wciel"); // left wing chamber
		addTradeChannel("trade_IDAbRe_Low_Divine"); // sulfur tree nest
		addTradeChannel("trade_IDAbRe_Up3_Crotan"); // krotan chamber
		addTradeChannel("trade_IDAbRe_Up3_Lamiren"); // miren chamber
		addTradeChannel("trade_IDAbRe_Up_Rhoo"); // chamber of roah
		addTradeChannel("trade_IDAbRe_Up3_Dkisas"); // kysis chamber
		addTradeChannel("trade_IDAbRe_Up_Asteria"); // asteria chamber
		addTradeChannel("trade_IDAbRe_Core"); // core
		addTradeChannel("trade_IDLF3_Castle_indratoo"); // indratu fortress
		addTradeChannel("trade_IDLF3_Castle_Lehpar"); // azoturan fortress
		addTradeChannel("trade_IDLF3Lp"); // aetherogenetics lab
		addTradeChannel("trade_IDLF2a_Lab"); // theobomos lab
		addTradeChannel("trade_IDDF2Flying"); // arkanis temple
		addTradeChannel("trade_Test_Basic"); // test basic
		addTradeChannel("trade_Test_GiantMonster"); // test giantmonster
		addTradeChannel("trade_Test_Server"); // test server
		//------------------------------------------------------------
		addTradeChannel("trade_Arena_L_Lobby");
		addTradeChannel("trade_Arena_D_Lobby");
		addTradeChannel("trade_IDAbPro");
		addTradeChannel("trade_IDTest_Dungeon");
		addTradeChannel("trade_IDAb1_Dreadgion");
		addTradeChannel("trade_IDDreadgion_02");
		addTradeChannel("trade_IDStation");
		addTradeChannel("trade_IDF4Re_Drana");
		addTradeChannel("trade_IDElemental_1");
		addTradeChannel("trade_IDElemental_2");
		addTradeChannel("trade_IDYun");
		addTradeChannel("trade_Test_MRT_IDZone");
		addTradeChannel("trade_IDArena");
		addTradeChannel("trade_IDRaksha");
		addTradeChannel("trade_IDArena_Solo");
		addTradeChannel("trade_IDArena_pvp01");
		addTradeChannel("trade_IDArena_pvp02");
		addTradeChannel("trade_IDArena_pvp01_T");
		addTradeChannel("trade_IDArena_pvp02_T");
		addTradeChannel("trade_IDAbProL1");
		addTradeChannel("trade_IDAbProL2");
		addTradeChannel("trade_IDAbGateL1");
		addTradeChannel("trade_IDAbGateL2");
		addTradeChannel("trade_IDLF1B");
		addTradeChannel("trade_IDLF1B_Stigma");
		addTradeChannel("trade_IDLC1_arena");
		addTradeChannel("trade_IDAbProL3");
		addTradeChannel("trade_IDAbProD1");
		addTradeChannel("trade_IDAbProD2");
		addTradeChannel("trade_IDAbGateD1");
		addTradeChannel("trade_IDAbGateD2");
		addTradeChannel("trade_IDDF1B");
		addTradeChannel("trade_IDSpace");
		addTradeChannel("trade_IDDC1_arena");
		addTradeChannel("trade_IDDC1_Arena_3F");
		addTradeChannel("trade_IDAbProD3");
		addTradeChannel("trade_IDDramata_01");
		addTradeChannel("trade_LF_Prison");
		addTradeChannel("trade_DF_Prison");
		addTradeChannel("trade_Housing_barrack");
		addTradeChannel("trade_Test_IDArena");
		addTradeChannel("trade_test_legion_housing");
		addTradeChannel("trade_test_personal_housing");

		// REGION
		addRegionChannel(WorldMapType.POETA.getId(), "public_LF1");
		addRegionChannel(WorldMapType.VERTERON.getId(), "public_LF1A");
		addRegionChannel(WorldMapType.SANCTUM.getId(), "public_LC1");
		addRegionChannel(WorldMapType.ELTNEN.getId(), "public_LF2");
		addRegionChannel(WorldMapType.THEOMOBOS.getId(), "public_LF2A");
		addRegionChannel(WorldMapType.HEIRON.getId(), "public_LF3");
		addRegionChannel(WorldMapType.ISHALGEN.getId(), "public_DF1");
		addRegionChannel(WorldMapType.MORHEIM.getId(), "public_DF2");
		addRegionChannel(WorldMapType.PANDAEMONIUM.getId(), "public_DC1");
		addRegionChannel(WorldMapType.BELUSLAN.getId(), "public_DF3");
		addRegionChannel(WorldMapType.ALTGARD.getId(), "public_DF1A");
		addRegionChannel(WorldMapType.BRUSTHONIN.getId(), "public_DF2A");
		addRegionChannel(WorldMapType.RESHANTA.getId(), "public_Ab1");
		addRegionChannel(WorldMapType.INGGISON.getId(), "public_LF4");
		addRegionChannel(WorldMapType.GELKMAROS.getId(), "public_DF4");
		addRegionChannel(WorldMapType.KAISINEL.getId(), "public_LC2");
		addRegionChannel(WorldMapType.MARCHUTAN.getId(), "public_DC2");
		addRegionChannel(WorldMapType.BESHMUNDIR_TEMPLE.getId(), "public_IDCatacombs");
		addRegionChannel(WorldMapType.SILENTERA_CANYON.getId(), "public_Underpass");
		addRegionChannel(WorldMapType.STEEL_RAKE.getId(), "public_IDshulackShip");
		addRegionChannel(WorldMapType.DARK_POETA.getId(), "public_IDLF1");
		addRegionChannel(WorldMapType.DRAUPNIR_CAVE.getId(), "public_IDDF3_Dragon");
		addRegionChannel(WorldMapType.CROMEDE.getId(), "public_IDCromede");
		addRegionChannel(WorldMapType.NOCHSANA_TRAINING_CAMP.getId(), "public_IDAb1_MiniCastle");
		addRegionChannel(WorldMapType.HARAMEL.getId(), "public_IDNovice");
		addRegionChannel(WorldMapType.FIRE_TEMPLE.getId(), "public_IDDF2_Dflame");
		addRegionChannel(WorldMapType.ADMA_STRONGHOLD.getId(), "public_IDDf2a_Adma");
		addRegionChannel(WorldMapType.ALQUIMIA_RESEARCH_CENTER.getId(), "public_IDDF3_LP");
		addRegionChannel(WorldMapType.TALOCS_HOLLOW.getId(), "public_IDElim");
		addRegionChannel(WorldMapType.UDAS_TEMPLE.getId(), "public_IDTemple_Up");
		addRegionChannel(WorldMapType.UDAS_TEMPLE_LOWER.getId(), "public_IDTemple_Low");
		addRegionChannel(WorldMapType.RIGHT_WING_CHAMBER.getId(), "public_IDAbRe_Low_Eciel");
		addRegionChannel(WorldMapType.LEFT_WING_CHAMBER.getId(), "public_IDAbRe_Low_Wciel");
		addRegionChannel(WorldMapType.SULFUR_TREE_NEST.getId(), "public_IDAbRe_Low_Divine");
		addRegionChannel(WorldMapType.KROTAN_CHAMBER.getId(), "public_IDAbRe_Up3_Crotan");
		addRegionChannel(WorldMapType.MIREN_CHAMBER.getId(), "public_IDAbRe_Up3_Lamiren");
		addRegionChannel(WorldMapType.CHAMBER_OF_ROAH.getId(), "public_IDAbRe_Up_Rhoo");
		addRegionChannel(WorldMapType.KYSIS_CHAMBER.getId(), "public_IDAbRe_Up3_Dkisas");
		addRegionChannel(WorldMapType.ASTERIA_CHAMBER.getId(), "public_IDAbRe_Up_Asteria");
		addRegionChannel(WorldMapType.CORE.getId(), "public_IDAbRe_Core");
		addRegionChannel(WorldMapType.INDRATU_FORTRESS.getId(), "public_IDLF3_Castle_indratoo");
		addRegionChannel(WorldMapType.AZOTURAN_FORTRESS.getId(), "public_IDLF3_Castle_Lehpar");
		addRegionChannel(WorldMapType.AETHEROGENETICS_LAB.getId(), "public_IDLF3Lp");
		addRegionChannel(WorldMapType.THEOBOMOS_LAB.getId(), "public_IDLF2a_Lab");
		addRegionChannel(WorldMapType.ARKANIS_TEMPLE.getId(), "public_IDDF2Flying");
		addRegionChannel(WorldMapType.TEST_BASIC.getId(), "public_Test_Basic");
		addRegionChannel(WorldMapType.TEST_GIANTMONSTER.getId(), "public_Test_GiantMonster");
		addRegionChannel(WorldMapType.TEST_SERVER.getId(), "public_Test_Server");
		addRegionChannel(WorldMapType.DE_PRISON.getId(), "public_LF_Prison");
		addRegionChannel(WorldMapType.DF_PRISON.getId(), "public_DF_Prison");
		addRegionChannel(WorldMapType.NO_ZONE_NAME.getId(), "public_IDAbPro");
		addRegionChannel(WorldMapType.ID_TEST_DUNGEON.getId(), "public_IDTest_Dungeon");
		addRegionChannel(WorldMapType.DREDGION.getId(), "public_IDAb1_Dreadgion");
		addRegionChannel(WorldMapType.DREDGION_OF_CHANTRA.getId(), "public_IDDreadgion_02");
		addRegionChannel(WorldMapType.KARAMATIS.getId(), "public_IDAbProL1");
		addRegionChannel(WorldMapType.KARAMATIS_B.getId(), "public_IDAbProL2");
		addRegionChannel(WorldMapType.AERDINA.getId(), "public_IDAbGateL1");
		addRegionChannel(WorldMapType.GERANAIA.getId(), "public_IDAbGateL2");
		addRegionChannel(WorldMapType.FRAGMENT_OF_DARKNESS.getId(), "public_IDLF1B");
		addRegionChannel(WorldMapType.IDLF1B_STIGMA.getId(), "public_IDAbProL3");
		addRegionChannel(WorldMapType.SANCTUM_UNDERGROUND_ARENA.getId(), "public_IDLC1_arena");
		addRegionChannel(WorldMapType.TRINIEL_UNDERGROUND_ARENA.getId(), "public_IDDC1_arena");
		addRegionChannel(WorldMapType.IDAB_PRO_L3.getId(), "public_IDAbProL3");
		addRegionChannel(WorldMapType.ATAXIAR.getId(), "public_IDAbProD1");
		addRegionChannel(WorldMapType.ATAXIAR_B.getId(), "public_IDAbProD1");
		addRegionChannel(WorldMapType.BREGIRUN.getId(), "public_IDAbGateD1");
		addRegionChannel(WorldMapType.NIDALBER.getId(), "public_IDAbGateD2");
		addRegionChannel(WorldMapType.SPACE_OF_OBLIVION.getId(), "public_IDDF1B");
		addRegionChannel(WorldMapType.SPACE_OF_DESTINY.getId(), "public_IDSpace");
		addRegionChannel(WorldMapType.SHADOW_COURT_DUNGEON.getId(), "public_IDDC1_Arena_3F");
		addRegionChannel(WorldMapType.IDAB_PRO_D3.getId(), "public_IDAbProD3");
		addRegionChannel(WorldMapType.KAISINEL_ACADEMY.getId(), "public_Arena_L_Lobby");
		addRegionChannel(WorldMapType.MARCHUTAN_PRIORY.getId(), "public_Arena_D_Lobby");
		addRegionChannel(WorldMapType.ATURAN_SKY_FORTRESS.getId(), "public_IDStation");
		addRegionChannel(WorldMapType.ESOTERRACE.getId(), "public_IDF4Re_Drana");
		addRegionChannel(WorldMapType.LADIS_FOREST.getId(), "public_IDElemental_1");
		addRegionChannel(WorldMapType.DORGEL_MANOR.getId(), "public_IDElemental_2");
		addRegionChannel(WorldMapType.LENTOR_OUTPOST.getId(), "public_IDYun");
		addRegionChannel(WorldMapType.EMPYREAN_CRUCIBLE.getId(), "public_IDArena");
		addRegionChannel(WorldMapType.TAHMES.getId(), "public_IDRaksha");
		addRegionChannel(WorldMapType.CRUCIBLE_CHALLENGE.getId(), "public_IDArena_Solo");
		addRegionChannel(WorldMapType.ARENA_OF_CHAOS.getId(), "public_IDArena_pvp01");
		addRegionChannel(WorldMapType.ARENA_OF_DISCIPLINE.getId(), "public_IDArena_pvp02");
		addRegionChannel(WorldMapType.CHAOS_TRAINING_GROUNDS.getId(), "public_IDArena_pvp01_T");
		addRegionChannel(WorldMapType.DISCIPLINE_TRAINING_GROUNDS.getId(), "public_IDArena_pvp02_T");
		addRegionChannel(WorldMapType.PADMARASHKA_CAVE.getId(), "public_IDDramata_01");
		addRegionChannel(WorldMapType.HOUSING_BARRACK.getId(), "public_Housing_barrack");
		addRegionChannel(WorldMapType.Region_housing.getId(), "public_test_legion_housing");
		addRegionChannel(WorldMapType.Advanced_Personal_Housing.getId(), "public_test_personal_housing");
		addRegionChannel(WorldMapType.Test_MRT_IDZone.getId(), "public_Test_MRT_IDZone");
		addRegionChannel(WorldMapType.IDAbProD2.getId(), "public_IDAbProD2");
		addRegionChannel(WorldMapType.Test_IDArena.getId(), "public_Test_IDArena");

		// LANG & JOB
		// TODO : All other lang
		if(Config.LANG_CHAT == 1) {
			// LANG
			addLangChannel("User_English");
			addLangChannel("User_French");
			addLangChannel("User_German");
			addLangChannel("User_Italian");
			addLangChannel("User_Spanish");
			addLangChannel("User_Danish");
			addLangChannel("User_Swedish");
			addLangChannel("User_Finnish");
			addLangChannel("User_Norwegian");
			addLangChannel("User_Greek");
			
			// JOB
			// Male
			addJobChannel(Gender.MALE, PlayerClass.GLADIATOR, "job_Gladiator");
			addJobChannel(Gender.MALE, PlayerClass.TEMPLAR, "job_Templar");
			addJobChannel(Gender.MALE, PlayerClass.SORCERER, "job_Sorcerer");
			addJobChannel(Gender.MALE, PlayerClass.SPIRIT_MASTER, "job_Spiritmaster");
			addJobChannel(Gender.MALE, PlayerClass.CHANTER, "job_Chanter");
			addJobChannel(Gender.MALE, PlayerClass.RANGER, "job_Ranger");
			addJobChannel(Gender.MALE, PlayerClass.ASSASSIN, "job_Assassin");
			addJobChannel(Gender.MALE, PlayerClass.CLERIC, "job_Cleric");
			// Female
			addJobChannel(Gender.FEMALE, PlayerClass.GLADIATOR, "job_Gladiator");
			addJobChannel(Gender.FEMALE, PlayerClass.TEMPLAR, "job_Templar");
			addJobChannel(Gender.FEMALE, PlayerClass.SORCERER, "job_Sorcerer");
			addJobChannel(Gender.FEMALE, PlayerClass.SPIRIT_MASTER, "job_Spiritmaster");
			addJobChannel(Gender.FEMALE, PlayerClass.CHANTER, "job_Chanter");
			addJobChannel(Gender.FEMALE, PlayerClass.RANGER, "job_Ranger");
			addJobChannel(Gender.FEMALE, PlayerClass.ASSASSIN, "job_Assassin");
			addJobChannel(Gender.FEMALE, PlayerClass.CLERIC, "job_Cleric");
		}
		else if(Config.LANG_CHAT == 2) {
			// LANG
			addLangChannel("User_Anglais");
			addLangChannel("User_Francais");
			addLangChannel("User_Allemand");
			addLangChannel("User_Italien");
			addLangChannel("User_Espagnol");
			addLangChannel("User_Danois");
			addLangChannel("User_Suedois");
			addLangChannel("User_Finnois");
			addLangChannel("User_Norvegien");
			addLangChannel("User_Grec");
			
			// JOB
			// Male
			addJobChannel(Gender.MALE, PlayerClass.GLADIATOR, "job_Gladiateur");
			addJobChannel(Gender.MALE, PlayerClass.TEMPLAR, "job_Templier");
			addJobChannel(Gender.MALE, PlayerClass.SORCERER, "job_Sorcier");
			addJobChannel(Gender.MALE, PlayerClass.SPIRIT_MASTER, "job_Spiritualiste");
			addJobChannel(Gender.MALE, PlayerClass.CHANTER, "job_Aede");
			addJobChannel(Gender.MALE, PlayerClass.RANGER, "job_Rôdeur");
			addJobChannel(Gender.MALE, PlayerClass.ASSASSIN, "job_Assassin");
			addJobChannel(Gender.MALE, PlayerClass.CLERIC, "job_Clerc");
			// Female
			addJobChannel(Gender.FEMALE, PlayerClass.GLADIATOR, "job_Gladiateur[f:" + '"' + "Gladiatrice" + '"' + "]");
			addJobChannel(Gender.FEMALE, PlayerClass.TEMPLAR, "job_Templier[f:" + '"' + "Templière" + '"' + "]");
			addJobChannel(Gender.FEMALE, PlayerClass.SORCERER, "job_Sorcier[f:" + '"' + "Sorcière" + '"' + "]");
			addJobChannel(Gender.FEMALE, PlayerClass.SPIRIT_MASTER, "job_Spiritualiste");
			addJobChannel(Gender.FEMALE, PlayerClass.CHANTER, "job_Aède");
			addJobChannel(Gender.FEMALE, PlayerClass.RANGER, "job_Rôdeur[f:" + '"' + "Rôdeuse" + '"' + "]");
			addJobChannel(Gender.FEMALE, PlayerClass.ASSASSIN, "job_Assassin[f:" + '"' + "Assassine" + '"' + "]");
			addJobChannel(Gender.FEMALE, PlayerClass.CLERIC, "job_Clerc");
		}
	}

	private static void addChannel(Channel Channel) {
		channels.add(Channel);
	}

	/**
	 * @param channelId
	 * @return
	 */
	public static Channel getChannelById(int channelId) {
		for (Channel channel : channels) {
			if (channel.getChannelId() == channelId)
				return channel;
		}
		if (Config.LOG_CHANNEL_INVALID) {
			log.warn("No registered channel with id " + channelId);
		}
		throw new IllegalArgumentException("no channel provided for id " + channelId);
	}

	/**
	 * @param identifier
	 * @return
	 */
	public static Channel getChannelByIdentifier(byte[] identifier) {
		for (Channel channel : channels) {
			if (Arrays.equals(channel.getIdentifierBytes(), identifier))
				return channel;
		}
		if (Config.LOG_CHANNEL_INVALID) {
			log.warn("No registered channel with identifier " + identifier);
		}
		// we can't throw runtime exceptions before support of i18n channel names
		return null;
	}

	private static void addGroupChannel(String channelName) {
		addChannel(new LfgChannel(Race.ELYOS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID
			+ ".0.AION.KOR"));
		addChannel(new LfgChannel(Race.ASMODIANS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID
			+ ".1.AION.KOR"));
	}

	private static void addTradeChannel(String channelName) {
		addChannel(new TradeChannel(Race.ELYOS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID
			+ ".0.AION.KOR"));
		addChannel(new TradeChannel(Race.ASMODIANS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID
			+ ".1.AION.KOR"));
	}

	private static void addRegionChannel(int mapId, String channelName) {
		addChannel(new RegionChannel(mapId, Race.ELYOS, "@\u0001" + channelName + "\u0001"
			+ GameServerService.GAMESERVER_ID + ".0.AION.KOR"));
		addChannel(new RegionChannel(mapId, Race.ASMODIANS, "@\u0001" + channelName + "\u0001"
			+ GameServerService.GAMESERVER_ID + ".1.AION.KOR"));
	}

	private static void addJobChannel(Gender gender, PlayerClass playerClass, String channelName) {
		addChannel(new JobChannel(gender, playerClass, Race.ELYOS, "@\u0001" + channelName + "\u0001"
			+ GameServerService.GAMESERVER_ID + ".0.AION.KOR"));
		addChannel(new JobChannel(gender, playerClass, Race.ASMODIANS, "@\u0001" + channelName + "\u0001"
			+ GameServerService.GAMESERVER_ID + ".1.AION.KOR"));;
	}
	
	private static void addLangChannel(String channelName) {
		addChannel(new LangChannel(Race.ELYOS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"));
		addChannel(new LangChannel(Race.ASMODIANS, "@\u0001" + channelName + "\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"));;
	}
}
