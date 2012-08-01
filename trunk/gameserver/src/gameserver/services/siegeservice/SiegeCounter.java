package gameserver.services.siegeservice;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.siege.SiegeNpc;
import gameserver.model.siege.SiegeRace;
import gameserver.model.team.legion.Legion;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SiegeCounter {

	private static final Logger log = LoggerFactory.getLogger(SiegeCounter.class);

	private final Map<SiegeRace, SiegeRaceCounter> siegeRaceCounters = Maps.newHashMap();

	public SiegeCounter() {
		siegeRaceCounters.put(SiegeRace.ELYOS, new SiegeRaceCounter(SiegeRace.ELYOS));
		siegeRaceCounters.put(SiegeRace.ASMODIANS, new SiegeRaceCounter(SiegeRace.ASMODIANS));
		siegeRaceCounters.put(SiegeRace.BALAUR, new SiegeRaceCounter(SiegeRace.BALAUR));
	}

	public void addDamage(Creature creature, int damage) {

		SiegeRace siegeRace;
		if (creature instanceof Player) {
			siegeRace = SiegeRace.getByRace(((Player) creature).getRace());
		} else if (creature instanceof SiegeNpc) {
			siegeRace = ((SiegeNpc) creature).getSiegeRace();
		} else {
			log.warn("Please debug me!", new RuntimeException("Damage to Siege boss done by non-SiegeRace creature" + creature));
			return;
		}

		siegeRaceCounters.get(siegeRace).addPoints(creature, damage);
	}

	public void addAbyssPoints(Player player, int ap) {
		SiegeRace sr = SiegeRace.getByRace(player.getRace());
		siegeRaceCounters.get(sr).addAbyssPoints(player, ap);
	}

	public SiegeRaceCounter getRaceCounter(SiegeRace race) {
		return siegeRaceCounters.get(race);
	}

	public void addLegionDamage(SiegeRace race, Legion legion, int damage){
		getRaceCounter(race).addLegionDamage(legion, damage);
	}

	public void addRaceDamage(SiegeRace race, int damage){
		getRaceCounter(race).addTotalDamage(damage);
	}

	/**
	 * Returns list of siege race counters sorted by total damage done to siege boss.
	 * Sorted in descending order.
	 *
	 * @return all siege race damage counters sorted by descending order
	 */
	public SiegeRaceCounter getWinnerRaceCounter() {
		List<SiegeRaceCounter> list = Lists.newArrayList(siegeRaceCounters.values());
		Collections.sort(list);
		return list.get(0);
	}
}
