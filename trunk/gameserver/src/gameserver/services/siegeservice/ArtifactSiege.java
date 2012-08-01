package gameserver.services.siegeservice;

import commons.database.dao.DAOManager;
import gameserver.dao.SiegeDAO;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.siege.ArtifactLocation;
import gameserver.model.siege.Influence;
import gameserver.model.siege.SiegeRace;
import gameserver.model.team.legion.Legion;
import gameserver.network.aion.AionServerPacket;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.LegionService;
import gameserver.services.SiegeService;
import gameserver.services.player.PlayerService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author SoulKeeper
 */
public class ArtifactSiege extends Siege<ArtifactLocation> {

	private static final Logger log = LoggerFactory.getLogger(ArtifactSiege.class.getName());

	@Deprecated
	public static final String SIEGE_BOSS_AI_NAME = "artifact_protector";

	public ArtifactSiege(ArtifactLocation siegeLocation) {
		super(siegeLocation);
	}

	@Override
	protected void onSiegeStart() {
		initSiegeBoss(Arrays.asList(SIEGE_BOSS_AI_NAME, FortressSiege.SIEGE_BOSS_AI_NAME));
		broadcastUpdate(getSiegeLocation());
	}

	@Override
	protected void onSiegeFinish() {

		// cleanup
		unregisterSiegeBossListeners();

		// for artifact should be always true
		if (isBossKilled()) {

			// despawn npcs
			SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());

			// update winner counter
			SiegeRaceCounter wRaceCounter = getSiegeCounter().getWinnerRaceCounter();
			getSiegeLocation().setRace(wRaceCounter.getSiegeRace());

			// update legion
			Integer wLegionId = wRaceCounter.getWinnerLegionId();
			getSiegeLocation().setLegionId(wLegionId != null ? wLegionId : 0);

			// misc stuff to send player system message
			if (getSiegeLocation().getRace() == SiegeRace.BALAUR) {
				// TODO: Fix message for Balaur Description id
				final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), "Balaur");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player object) {
						PacketSendUtility.sendPacket(object, lRacePacket);
					}
				});
			} else {
				// Prepare packet data
				final Race wRace = wRaceCounter.getSiegeRace() == SiegeRace.ELYOS ? Race.ELYOS : Race.ASMODIANS;
				Legion wLegion = wLegionId != null ? LegionService.getInstance().getLegion(wLegionId) : null;
				Integer wPlayerId = wRaceCounter.getPlayerDamageCounter().keySet().iterator().next();
				String wPlayerName = PlayerService.getPlayerName(wPlayerId);
				final String winnerName = wLegion != null ? wLegion.getLegionName() : wPlayerName;

				// prepare packets, we can use single packet instance
				final AionServerPacket wRacePacket = new SM_SYSTEM_MESSAGE(1320002, wRace.getRaceDescriptionId(), winnerName, getSiegeLocation().getNameAsDescriptionId());
				final AionServerPacket lRacePacket = new SM_SYSTEM_MESSAGE(1320004, getSiegeLocation().getNameAsDescriptionId(), wRace.getRaceDescriptionId());

				// send update to players
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player object) {


						if (object.getRace() == wRace) {
							// send message to winner race
							PacketSendUtility.sendPacket(object, wRacePacket);
						} else {
							// send message to looser race
							PacketSendUtility.sendPacket(object, lRacePacket);
						}
					}
				});
			}

			// add new spawns
			SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace());
		} else {
			log.error("Artifact siege (SiegeLocId = )" + getSiegeLocationId() + " ended without killing a boss.");
		}

		Influence.getInstance().recalculateInfluence();

		// Store siege results in DB
		DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());

		broadcastUpdate(getSiegeLocation());

		SiegeService.getInstance().startSiege(getSiegeLocationId());
	}

	@Override
	public int getDurationInSeconds() {
		return -1;
	}

	@Override
	public boolean isEndless() {
		return true;
	}
}
