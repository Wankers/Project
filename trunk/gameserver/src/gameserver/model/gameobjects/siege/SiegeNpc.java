/**
 * 
 */
package gameserver.model.gameobjects.siege;

import gameserver.controllers.NpcController;
import gameserver.model.gameobjects.Npc;
import gameserver.model.siege.SiegeRace;
import gameserver.model.templates.npc.NpcTemplate;
import gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;

/**
 * @author ViAl
 */
public class SiegeNpc extends Npc {

	private int siegeId;
	private SiegeRace siegeRace;

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 *          SiegeNpc constructor
	 */
	public SiegeNpc(int objId, NpcController controller, SiegeSpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		super(objId, controller, spawnTemplate, objectTemplate);
		this.siegeId = spawnTemplate.getSiegeId();
		this.siegeRace = spawnTemplate.getSiegeRace();
	}

	public SiegeRace getSiegeRace() {
		return siegeRace;
	}

	public int getSiegeId() {
		return siegeId;
	}

	@Override
	public SiegeSpawnTemplate getSpawn() {
		return (SiegeSpawnTemplate) super.getSpawn();
	}
	
	

}
