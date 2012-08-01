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
package ai.instance.beshmundirTemple;

import java.util.Map;

import ai.AggressiveNpcAI2;

import commons.network.util.ThreadPoolManager;
import commons.utils.Rnd;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.spawnengine.SpawnEngine;


/**
 * @author Luzien
 *
 */
@AIName("isbariya")
public class IsbariyaTheResoluteAI2 extends AggressiveNpcAI2 {
	private int stage = 0;
	private boolean isStart = false;
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isStart) {
			isStart = true;
			useSkill();
			getPosition().getWorldMapInstance().getDoors().get(535).setOpen(false);
		}
		
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isStart = false;
		getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
		stage = 0;
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 75 && stage < 1) {
			stage = 1;
			launchSpecial();
		}
		if (hpPercentage <= 50 && stage < 2) {
			stage = 2;
		}
		if (hpPercentage <= 25 && stage < 3) {
			stage = 3;
		}
	}
	
	private void useSkill() {
		if(!isAlreadyDead() && isStart) {
		AI2Actions.useSkill(this, (18912 + Rnd.get(0, 1))); //randomly uses arcane combustion/seeking explosion
		scheduleSkills();
		}
	}
	
	private void launchSpecial() {
		if(isAlreadyDead() || !isStart || stage == 0)
			return;
		int delay = 10000;
		
		switch(stage) {
			case 1:
				cursePlayer();
				delay = 25000;
				break;
			case 2:
				rndSpawn(281660, 5);
				break;
			case 3:
				rndSpawn(281659, 1); //TODO: find correct use for these servants, maybe create own AI
				AI2Actions.useSkill(this, 18993);
				delay = 20000;
				break;
		}
		scheduleSpecial(delay);
	}
	
	
	private void rndSpawn(int npcId, int count) {
		for(int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
/*	private void spawnSouls() {
*		int count = Rnd.get(3,6);
*		for(int i = 0; i < count; i++) {
*			spawn(281645, (float)1588.527, (float)1574.2,(float) 304.7,(byte) 17);
*		}		
*	}
*/

	
	private void cursePlayer() { //select a player for ghost debuff, which is NOT current target(tank)
		for(int i = 0; i <= 10; i++) {
				Player member = selectRandomPlayer();
					if (member != null && isInRange(member, 40) && !member.getEffectController().hasAbnormalEffect(18959)
						&& !member.getLifeStats().isAlreadyDead() && member != getTarget() && member.isOnline()) {
							AI2Actions.targetCreature(this, member);
							AI2Actions.useSkill(this, 18959);
							 //spawnSouls(); TODO: Use when movement is fixed
							rndSpawn(281645, 3);
							break;
				}
			}
	}

	private Player selectRandomPlayer() {
			Map<Integer, Player> knownPlayers = getKnownList().getKnownPlayers();
			int targetId = 0;
			int currentSum = 0;
			int rnd = Rnd.get(0, (knownPlayers.size()) - 1);
			for(int playerId : knownPlayers.keySet()) { 
					currentSum += 1;
					if (rnd < currentSum) {
							targetId = playerId;
							break;
					}
			}
		return knownPlayers.get(targetId);
	}
	
	
	private void scheduleSpecial(int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					launchSpecial();
				}
		}, delay);	
	}
	
	private void scheduleSkills() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				useSkill();
			}
		}, 24000);
	}


	private SpawnTemplate rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * 5);
		float y1 = (float) (Math.sin(Math.PI * direction) * 5);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
			+ y1, getPosition().getZ(), getPosition().getHeading());
	}
}


