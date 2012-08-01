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
package zone;

import java.io.IOException;

import javolution.util.FastMap;

import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.geoEngine.GeoWorldLoader;
import gameserver.geoEngine.collision.CollisionResults;
import gameserver.geoEngine.math.Ray;
import gameserver.geoEngine.math.Vector3f;
import gameserver.geoEngine.scene.Node;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.world.zone.ZoneInstance;
import gameserver.world.zone.handler.ZoneHandler;
import gameserver.world.zone.handler.ZoneNameAnnotation;



/**
 * @author MrPoke
 *
 */
@ZoneNameAnnotation("CORE_400010000")
public class AbyssCore implements ZoneHandler {

	FastMap<Integer, Observer> observed = new FastMap<Integer, Observer>();

	private Node geometry;
	
	public AbyssCore() {
		try {
			this.geometry =  (Node) GeoWorldLoader.loadMeshs("data/geo/models/na_ab_lmark_col_01a.mesh").values().toArray()[0];
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		geometry.updateModelBound();
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		Creature acting = creature.getActingCreature();
		if (acting instanceof Player && !((Player)acting).isGM()){

			Observer observer = new Observer(creature, geometry);
			creature.getObserveController().addObserver(observer);
			observed.put(creature.getObjectId(), observer);
		}
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		Creature acting = creature.getActingCreature();
		if (acting instanceof Player && !((Player)acting).isGM()){
			Observer observer = observed.get(creature.getObjectId());
			if (observer != null){
				creature.getObserveController().removeObserver(observer);
				observed.remove(creature.getObjectId());
			}
		}
	}

	class Observer extends ActionObserver{

		private Creature creature;
		private Vector3f oldPos;
		private Node geometry;
		/**
		 * @param observerType
		 */
		public Observer(Creature creature, Node geometry) {
			super(ObserverType.MOVE);
			this.creature = creature;
			this.geometry = geometry;
			this.oldPos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
		}

		
		@Override
		public void moved() {
			Vector3f pos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
			Vector3f dir = oldPos.clone();
			Float limit = pos.distance(dir);
			dir.subtractLocal(pos).normalizeLocal();
			Ray r = new Ray(pos, dir);
			r.setLimit(limit);
			CollisionResults results = new CollisionResults();
			results.setOnlyFirst(true);
			geometry.collideWith(r, results, creature.getInstanceId());
			if (results.size() != 0){
				creature.getController().die();
			}
			oldPos = pos;
		}
	}
}
