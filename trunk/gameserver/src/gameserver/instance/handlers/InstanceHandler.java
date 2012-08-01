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
package gameserver.instance.handlers;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.instance.StageType;
import gameserver.model.instance.instancereward.InstanceReward;
import gameserver.world.WorldMapInstance;
import gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public interface InstanceHandler {

	/**
	 * Executed during instance creation.<br>
	 * This method will run after spawns are loaded
	 * 
	 * @param instance
	 *          created
	 */
	void onInstanceCreate(WorldMapInstance instance);

	/**
	 * Executed during instance destroy.<br>
	 * This method will run after all spawns unloaded.<br>
	 * All class-shared objects should be cleaned in handler
	 */
	void onInstanceDestroy();

	void onPlayerLogin(Player player);

	void onPlayerLogOut(Player player);

	void onEnterInstance(Player player);

	void onLeaveInstance(Player player);

	void onOpenDoor(int door);

	void onEnterZone(Player player, ZoneInstance zone);

	void onLeaveZone(Player player, ZoneInstance zone);

	void onPlayMovieEnd(Player player, int movieId);

	boolean onReviveEvent(Player player);

	void onExitInstance(Player player);

	void doReward(Player player);

	boolean onDie(Player player, Creature lastAttacker);

	void onStopTraining(Player player);

	void onDie(Npc npc);

	void onChangeStage(StageType type);

	StageType getStage();

	void onDropRegistered(Npc npc);

	void onGather(Player player);
        
        void onStopInstance();
        
        boolean isEnemy(Player effector, Creature effected);
    
        boolean isEnemyPlayer(Creature effector, Creature effected);

	InstanceReward<?> getInstanceReward();

	boolean onPassFlyingRing(Player player, String flyingRing);

	void handleUseItemFinish(Player player, int npcId);
}