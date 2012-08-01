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
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.model.instance.StageType;
import gameserver.model.instance.instancereward.InstanceReward;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.spawnengine.SpawnEngine;
import gameserver.world.WorldMapInstance;
import gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public class GeneralInstanceHandler implements InstanceHandler {

	protected final long creationTime;
	protected WorldMapInstance instance;
	protected int instanceId;
	protected Integer mapId;

	public GeneralInstanceHandler() {
		creationTime = System.currentTimeMillis();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		this.instance = instance;
		this.instanceId = instance.getInstanceId();
		this.mapId = instance.getMapId();
	}

	@Override
	public void onInstanceDestroy() {
	}

	@Override
	public void onPlayerLogin(Player player) {
	}

	@Override
	public void onPlayerLogOut(Player player) {
	}

	@Override
	public void onEnterInstance(Player player) {
	}

	@Override
	public void onLeaveInstance(Player player) {
	}

	@Override
	public void onOpenDoor(int door) {
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
	}

	@Override
	public void onLeaveZone(Player player, ZoneInstance zone) {
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
	}

	@Override
	public boolean onReviveEvent(Player player) {
		return false;
	}

	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading) {
		SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		return SpawnEngine.spawnObject(template, instanceId);
	}

	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading, int staticId) {
		SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(mapId, npcId, x, y, z, heading);
		template.setStaticId(staticId);
		return SpawnEngine.spawnObject(template, instanceId);
	}

	@Override
	public void onExitInstance(Player player) {
	}

	@Override
	public void doReward(Player player) {
	}

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		return false;
	}

	@Override
	public void onStopTraining(Player player) {
	}

	@Override
	public void onDie(Npc npc) {
	}

	@Override
	public void onChangeStage(StageType type) {
	}

	@Override
	public StageType getStage() {
		return StageType.DEFAULT;
	}

	@Override
	public void onDropRegistered(Npc npc) {
	}

	@Override
	public void onGather(Player player) {
	}

	@Override
	public InstanceReward<?> getInstanceReward() {
		return null;
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		return false;
	}
        
        @Override
        public boolean isEnemy(Player effector, Creature effected) {
        return false;
        }  

        @Override
        public boolean isEnemyPlayer(Creature effector, Creature effected) {
        return false;
        }

	@Override
	public void handleUseItemFinish(Player player, int npcId) {
	}
        
        @Override
        public void onStopInstance() {
        }
		
  		public void sendEventPacket(Player paramPlayer){
  		}

  		public void sendActingPacket(Player paramPlayer){
  		}

  		public void onNpcGroupKillEvent(WorldMapInstance paramWorldMapInstance, PlayerGroup paramPlayerGroup, Npc paramNpc){
  		}

  		public void onPvpKillEvent(WorldMapInstance paramWorldMapInstance, Player paramPlayer1, Player paramPlayer2){
  		}

  		public void onGeneralPlayerDeathEvent(WorldMapInstance paramWorldMapInstance, Player paramPlayer, Creature paramCreature){
  		}
}