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
package gameserver.model.gameobjects;

import org.apache.commons.lang.StringUtils;

import gameserver.controllers.NpcController;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.container.NpcLifeStats;
import gameserver.model.stats.container.TrapGameStats;
import gameserver.model.templates.npc.NpcTemplate;
import gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author ATracer
 */
public class Trap extends SummonedObject {

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public Trap(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		super(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}

	@Override
	protected void setupStatContainers(byte level) {
		setGameStats(new TrapGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	@Override
	public byte getLevel() {
		return (getCreator() == null ? 1 : getCreator().getLevel());
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return getCreator().isEnemy(creature);
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return getCreator() != null ? getCreator().isEnemyFrom(player) : false;
	}

	/**
	 * @return NpcObjectType.TRAP
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.TRAP;
	}

	@Override
	public String getCreatorName() {
		return StringUtils.EMPTY;
	}
}
