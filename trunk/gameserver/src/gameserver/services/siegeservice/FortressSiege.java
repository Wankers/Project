/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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
package gameserver.services.siegeservice;

import commons.callbacks.util.GlobalCallbackHelper;
import gameserver.configs.main.LoggingConfig;
import commons.database.dao.DAOManager;
import gameserver.configs.main.SiegeConfig;
import gameserver.dao.PlayerDAO;
import gameserver.dao.SiegeDAO;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.siege.*;
import gameserver.model.templates.siegelocation.SiegeLegionReward;
import gameserver.model.templates.siegelocation.SiegeReward;
import gameserver.model.templates.zone.ZoneType;
import gameserver.services.LegionService;
import gameserver.services.SiegeService;
import gameserver.services.SystemMailService;
import gameserver.services.player.PlayerService;
import gameserver.world.knownlist.Visitor;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object that controls siege of certain fortress. Siege object is not reusable.
 * New siege = new instance.
 * <p/>
 * TODO: Implement Balaur siege support
 *
 * @author SoulKeeper
 */
public class FortressSiege extends Siege<FortressLocation> {

	private static final Logger log = LoggerFactory.getLogger(FortressSiege.class);
	/**
	 * AI name of siege boss npc. TODO: It's dirty hack, remove it in the future
	 */
	@Deprecated
	public static final String SIEGE_BOSS_AI_NAME = "siege_protector";
	private final FortressSiegeAbyssPointsAddedListener addAbyssPointsListener = new FortressSiegeAbyssPointsAddedListener(
			this);

	public FortressSiege(FortressLocation fortress) {
		super(fortress);
	}

	public void onSiegeStart() {

		// Mark fortress as vulnerable
		getSiegeLocation().setVulnerable(true);

		// Clear fortress from enemys
		for (Creature creature : getSiegeLocation().getCreatures().values()) {
			if (creature instanceof Player) {
				Player player = (Player) creature;
				if (!getSiegeLocation().isEnemy(player))
					player.setInsideZoneType(ZoneType.SIEGE);
			}
			else if (creature instanceof Kisk) {
				Kisk kisk = (Kisk) creature;
				if (kisk.getRace().getRaceId() != getSiegeLocation().getRace().getRaceId())
					kisk.getController().die();
			}
		}

		// Let the world know where the siege are
		broadcastUpdate(getSiegeLocation());

		// Register abyss points listener
		// We should listen for abyss point callbacks that players are earning
		GlobalCallbackHelper.addCallback(addAbyssPointsListener);

		// Spawn NPCs
		// respawn all NPCs so ppl cannot kill guards before siege
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace());
		SiegeService.getInstance().spawnProtectors(getSiegeLocationId(), getSiegeLocation().getRace());
		initSiegeBoss(Collections.singleton(SIEGE_BOSS_AI_NAME));
	}

	public void onSiegeFinish() {

		// Unregister abyss points listener callback
		// We really don't need to add abyss points anymore
		GlobalCallbackHelper.removeCallback(addAbyssPointsListener);

		// Unregister siege boss listeners
		// cleanup :)
		unregisterSiegeBossListeners();

		// despawn protectors and make fortress invulnerable
		SiegeService.getInstance().deSpawnProtectors(getSiegeLocationId());
		getSiegeLocation().setVulnerable(false);

		// Guardian deity general was not killed, fortress stays with previous
		if (isBossKilled())
			capture();

		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace());

		// Reward players and owning legion
		// If fortress was not captured by balaur
		if (SiegeRace.BALAUR != getSiegeLocation().getRace()) {
			giveRewardsToLegion();
			giveRewardsToPlayers(getSiegeCounter().getRaceCounter(getSiegeLocation().getRace()));
		}

		// Update outpost status
		// Certain fortresses are changing outpost ownership
		updateOutpostStatusAfterFortressSiege();

		// recalculate influence
		Influence.getInstance().recalculateInfluence();

		// Update data in the DB
		DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());

		// And let the world know who is their hero now :)
		broadcastUpdate(getSiegeLocation());

		getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				player.unsetInsideZoneType(ZoneType.SIEGE);
			}

		});
	}

	protected void capture() {

		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();

		// Set new fortress and artifact owner race
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());

		// If new race is balaur
		if (SiegeRace.BALAUR == winner.getSiegeRace()) {
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		}
		else {
			Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
		}
	}

	public void updateOutpostStatusAfterFortressSiege() {
		for (OutpostLocation o : SiegeService.getInstance().getOutposts().values()) {

			if (!o.getFortressDependency().contains(getSiegeLocationId())) {
				continue;
			}

			// Check if all fortresses are captured by the same owner
			// If not - common fortress race is balaur
			SiegeRace fortressRace = getSiegeLocation().getRace();
			for (Integer fortressId : o.getFortressDependency()) {
				SiegeRace sr = SiegeService.getInstance().getFortresses().get(fortressId).getRace();
				if (fortressRace != sr) {
					fortressRace = SiegeRace.BALAUR;
					break;
				}
			}

			// Get the new owner race
			SiegeRace newOwnerRace;
			if (SiegeRace.BALAUR == fortressRace) {
				// In case of balaur fortress ownership
				// oupost also belongs to balaur
				newOwnerRace = SiegeRace.BALAUR;
			}
			else {
				// if fortress owner is non-balaur
				// then outpost owner is opposite to fortress owner
				// Example: if fortresses are captured by Elyos, then outpost should be captured by Asmo
				newOwnerRace = fortressRace == SiegeRace.ELYOS ? SiegeRace.ASMODIANS : SiegeRace.ELYOS;
			}

			// update outpost race status
			if (o.getRace() != newOwnerRace) {
				SiegeService.getInstance().stopSiege(o.getLocationId());
				SiegeService.getInstance().deSpawnNpcs(o.getLocationId());
				SiegeService.getInstance().deSpawnProtectors(o.getLocationId());

				// update outpost race and store in db
				o.setRace(newOwnerRace);
				DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(o);

				// spawn NPC's or sieges
				if (SiegeRace.BALAUR != o.getRace()) {
					if (o.isSiegeAllowed()) {
						SiegeService.getInstance().startSiege(o.getLocationId());
					}
					else {
						SiegeService.getInstance().spawnNpcs(o.getLocationId(), o.getRace());
					}
				}
			}
		}
	}

	/**
	 * Returns 1 hour
	 *
	 * @return 1 hour (3600) seconds
	 */
	@Override
	public int getDurationInSeconds() {
		return 60 * 60;
	}

	@Override
	public boolean isEndless() {
		return false;
	}

	public void addAbyssPoints(Player player, int abysPoints) {
		getSiegeCounter().addAbyssPoints(player, abysPoints);
	}

	protected void giveRewardsToLegion() {
		// We do not give rewards if fortress was captured for first time
		if (isBossKilled()) {
			return;
		}

		// Legion with id 0 = not exists?
		if (getSiegeLocation().getLegionId() == 0) {
			return;
		}

		List<SiegeLegionReward> legionRewards = getSiegeLocation().getLegionReward();
		int legionBGeneral = LegionService.getInstance().getLegionBGeneral(getSiegeLocation().getLegionId());
		if (legionBGeneral != 0) {
			PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
			if (LoggingConfig.LOG_SIEGE) {	
				log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Legion Reward in process... LegionId:" 
				+ getSiegeLocation().getLegionId() + " General Name:" + BGeneral.getName());
			}
			if (legionRewards != null) {
				for (SiegeLegionReward medalsType : legionRewards) {
					SystemMailService.getInstance().sendMail("SiegeService", BGeneral.getName(), "LegionReward",
							"Successful defence", medalsType.getItemId(), medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0,
							false);
				}
			}
		}
	}

	protected void giveRewardsToPlayers(SiegeRaceCounter winnerDamage) {

		// Get the map with playerId to siege reward
		Map<Integer, Long> playerAbyssPoints = winnerDamage.getPlayerAbyssPoints();
		List<Integer> topPlayersIds = Lists.newArrayList(playerAbyssPoints.keySet());
		Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());

		// Black Magic Here :)
		int i = 0;
		List<SiegeReward> playerRewards = getSiegeLocation().getReward();
		for (SiegeReward topGrade : playerRewards) {
			for (int rewardedPC = 0; i < topPlayersIds.size() && rewardedPC < topGrade.getTop(); ++i) {
				Integer playerId = topPlayersIds.get(i);
				++rewardedPC;
				SystemMailService.getInstance().sendMail("SiegeService", playerNames.get(playerId), "SiegeReward", "Medal",
						topGrade.getItemId(), topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0, false);
			}
		}
	}

	protected ArtifactLocation getArtifact() {
		return SiegeService.getInstance().getFortressArtifacts().get(getSiegeLocationId());
	}

	protected boolean hasArtifact() {
		return getArtifact() != null;
	}

}