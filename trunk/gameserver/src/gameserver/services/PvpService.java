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
package gameserver.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import gameserver.configs.main.CustomConfig;
import gameserver.configs.main.GroupConfig;
import gameserver.configs.main.LoggingConfig;
import gameserver.controllers.attack.AggroInfo;
import gameserver.controllers.attack.KillList;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RewardType;
import gameserver.model.team2.alliance.PlayerAlliance;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.services.abyss.AbyssPointsService;
import gameserver.services.item.ItemService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.stats.AbyssRankEnum;
import gameserver.utils.stats.StatFunctions;

/**
 * @author Sarynth
 */
public class PvpService {

	private static Logger log = LoggerFactory.getLogger(PvpService.class);
	public static final PvpService getInstance() {
		return SingletonHolder.instance;
	}

	private FastMap<Integer, KillList> pvpKillLists;

	private PvpService() {
		pvpKillLists = new FastMap<Integer, KillList>();
	}

	public static boolean pvpZoneReward = true;
	public static boolean getPvpZoneReward()
	{
		return pvpZoneReward;
	}
	public static void setPvpZoneReward(boolean reward)
	{
		pvpZoneReward = reward;
	}

	/**
	 * @param winnerId
	 * @param victimId
	 * @return
	 */
	private int getKillsFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);

		if (winnerKillList == null)
			return 0;
		return winnerKillList.getKillsFor(victimId);
	}

	/**
	 * @param winnerId
	 * @param victimId
	 */
	private void addKillFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);
		if (winnerKillList == null) {
			winnerKillList = new KillList();
			pvpKillLists.put(winnerId, winnerKillList);
		}
		winnerKillList.addKillFor(victimId);
	}

	/**
	 * @param victim
	 */
	public void doReward(Player victim) {
		// winner is the player that receives the kill count
		final Player winner = victim.getAggroList().getMostPlayerDamage();

		int totalDamage = victim.getAggroList().getTotalDamage();

		if (totalDamage == 0 || winner == null || winner.getRace() == victim.getRace()) {
			return;
		}

		// Add Player Kill to record.
		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
			winner.getAbyssRank().setAllKill();
			int kills = winner.getAbyssRank().getAllKill();
			// Pvp Kill Reward.
			if (CustomConfig.ENABLE_KILL_REWARD) {
				if (kills % CustomConfig.KILLS_NEEDED1 == 1) {
					ItemService.addItem(winner, CustomConfig.REWARD1, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD1
						+ "] for having killed " + CustomConfig.KILLS_NEEDED1 + " players !");
					log.info("[REWARD] Player [" + winner.getName()+ "] win 2 [" + CustomConfig.REWARD1 + "]");
				}
				if (kills % CustomConfig.KILLS_NEEDED2 == 3) {
					ItemService.addItem(winner, CustomConfig.REWARD2, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD2
						+ "] for having killed " + CustomConfig.KILLS_NEEDED2 + " players !");
					log.info("[REWARD] Player [" + winner.getName()+ "] win 4 [" + CustomConfig.REWARD2 + "]");
				}
				if (kills % CustomConfig.KILLS_NEEDED3 == 5) {
					ItemService.addItem(winner, CustomConfig.REWARD3, 1);
					PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD3
						+ "] for having killed " + CustomConfig.KILLS_NEEDED3 + " players !");
					log.info("[REWARD] Player [" + winner.getName()+ "] win 6 [" + CustomConfig.REWARD3 + "]");
				}
			}
		}

		// Announce that player has died.
		PacketSendUtility.broadcastPacketAndReceive(victim,
			SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));
			
		//Kill-log
		if (LoggingConfig.LOG_KILL)
			log.info("[KILL] Player [" + winner.getName()+ "] killed [" + victim.getName() + "]");

		// Keep track of how much damage was dealt by players
		// so we can remove AP based on player damage...
		int playerDamage = 0;
		boolean success = false;

		// Distribute AP to groups and players that had damage.
		for (AggroInfo aggro : victim.getAggroList().getFinalDamageList(true)) {
			if (aggro.getAttacker() instanceof Player) {
				success = rewardPlayer(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerGroup) {
				success = rewardPlayerGroup(victim, totalDamage, aggro);
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance) {
				success = rewardPlayerAlliance(victim, totalDamage, aggro);
			}

			// Add damage last, so we don't include damage from same race. (Duels, Arena)
			if (success)
				playerDamage += aggro.getDamage();
		}

		// Apply lost AP to defeated player
		final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
		final int apActuallyLost = (int) (apLost * playerDamage / totalDamage);

		if (apActuallyLost > 0)
			AbyssPointsService.addAp(victim, -apActuallyLost);

	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro) {
		// Reward Group
		PlayerGroup group = ((PlayerGroup) aggro.getAttacker());

		// Don't Reward Player of Same Faction.
		if (group.getRace() == victim.getRace())
			return false;

		// Find group members in range
		List<Player> players = new ArrayList<Player>();

		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;

		for (Player member : group.getMembers()) {
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead()) {
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}

		// They are all dead or out of range.
		if (players.size() == 0)
			return false;
		
		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		float groupPercentage = (float) aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
		int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
		int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());
		
		for (Player member : players) {
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
				if (apRewardPerMember > 0)
					memberApGain = Math.round(apRewardPerMember * member.getRates().getApPlayerGainRate());
				if (xpRewardPerMember > 0)
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				if (dpRewardPerMember > 0)
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
				
			}
			AbyssPointsService.addAp(member, memberApGain);
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);
			this.addKillFor(member.getObjectId(), victim.getObjectId());
			//notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
		}

		return true;
	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro) {
		// Reward Alliance
		PlayerAlliance alliance = ((PlayerAlliance) aggro.getAttacker());

		// Don't Reward Player of Same Faction.
		if (alliance.getLeaderObject().getRace() == victim.getRace())
			return false;

		// Find group members in range
		List<Player> players = new ArrayList<Player>();

		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;

		for (Player member : alliance.getMembers()) {
			if (!member.isOnline())
				continue;
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead()) {
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}

		// They are all dead or out of range.
		if (players.size() == 0)
			return false;

		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		float groupPercentage = (float) aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
		int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
		int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());
		
		for (Player member : players) {
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
				if (apRewardPerMember > 0)
					memberApGain = Math.round(apRewardPerMember * member.getRates().getApPlayerGainRate());
				if (xpRewardPerMember > 0)
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				if (dpRewardPerMember > 0)
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
			}
			AbyssPointsService.addAp(member, memberApGain);
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);
			
			this.addKillFor(member.getObjectId(), victim.getObjectId());
			//notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
		}

		return true;
	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if player is not same race
	 */
	private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro) {
		// Reward Player
		Player winner = ((Player) aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		if (winner.getRace() == victim.getRace())
			return false;

		int baseApReward = 1;
		int baseXpReward = 1;
		int baseDpReward = 1;

		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
		{
			baseApReward = StatFunctions.calculatePvpApGained(victim, winner.getAbyssRank().getRank().getId(),
				winner.getLevel());
			baseXpReward = StatFunctions.calculatePvpXpGained(victim, winner.getAbyssRank().getRank().getId(),
				winner.getLevel());
			baseDpReward = StatFunctions.calculatePvpDpGained(victim, winner.getAbyssRank().getRank().getId(), 
				winner.getLevel());
		}

		int apPlayerReward = Math.round(baseApReward * winner.getRates().getApPlayerGainRate() * aggro.getDamage()
			/ totalDamage);
		int xpPlayerReward = Math.round(baseXpReward * winner.getRates().getXpPlayerGainRate() * aggro.getDamage()
			/ totalDamage);
		int dpPlayerReward = Math.round(baseDpReward * winner.getRates().getDpPlayerRate() * aggro.getDamage() 
			/ totalDamage);

		AbyssPointsService.addAp(winner, apPlayerReward);
		winner.getCommonData().addExp(xpPlayerReward, RewardType.PVP_KILL, victim.getName());
		winner.getCommonData().addDp(dpPlayerReward);
		this.addKillFor(winner.getObjectId(), victim.getObjectId());
		//notify Kill-Quests
		int worldId = winner.getWorldId();
		QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, winner, 0, 0), worldId);
		QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, winner, 0, 0), victim.getAbyssRank().getRank());

		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PvpService instance = new PvpService();
	}
}
