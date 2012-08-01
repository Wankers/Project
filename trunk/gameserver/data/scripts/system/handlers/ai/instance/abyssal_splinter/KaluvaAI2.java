package ai.instance.abyssal_splinter;

import ai.AggressiveNpcAI2;

import commons.network.util.ThreadPoolManager;
import commons.utils.Rnd;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillTemplate;

/**
 * @author Luzien
 */
@AIName("kaluva")
public class KaluvaAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleAttack(Creature creature) {

		super.handleAttack(creature);

		if (Rnd.get(1, 100) < 3) {
			moveToSpawner();
			AI2Actions.applyEffectSelf(KaluvaAI2.this, 19152);
		}
	}

	private void move(int npcId){
		AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(npcId));
		getMoveController().moveToTargetObject();
	}
	
	private void applyEffect(Creature effector, Creature effected ,int skillId) {
			SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			Effect e = new Effect(effector, effected, st, 1, st.getEffectsDuration());
			e.initialize();
			e.applyEffect();
	}
	
	private void spawned(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				hatchAdds(npc);
				npc.getController().die();
			}

		}, 25000);
}
	private void hatchAdds(Npc npc) { // 4 different spawn-formations; See Powerwiki for more information
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(281911, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				spawn(281911, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				break;
			case 2:
				for (int i = 0; i < 12; i++) {
					spawn(281912, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				}
				break;
			case 3:
				spawn(282057, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				break;
			case 4:
				spawn(281911, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				spawn(281912, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				spawn(281912, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				spawn(281912, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				break;
		}
	}
	
	private void moveToSpawner() {
		Npc SpawnerA = getPosition().getWorldMapInstance().getNpc(281902);
		Npc SpawnerB = getPosition().getWorldMapInstance().getNpc(282056);
		Npc SpawnerC = getPosition().getWorldMapInstance().getNpc(282055);
		Npc SpawnerD = getPosition().getWorldMapInstance().getNpc(282054);
		switch (Rnd.get(1, 4)) {
			case 1:
				if(SpawnerA != null && !SpawnerA.getLifeStats().isAlreadyDead()){
				move(281902);
				SpawnerA.getEffectController().removeEffect(19222);
				applyEffect(SpawnerA, SpawnerA ,19223);
				spawned(SpawnerA);
				break;
				}
			case 2:
				if(SpawnerB != null && !SpawnerB.getLifeStats().isAlreadyDead()){
				move(282056);
				SpawnerB.getEffectController().removeEffect(19222);
				applyEffect(SpawnerB, SpawnerB ,19223);
				spawned(SpawnerB);
				}
				break;
			case 3:
				if(SpawnerC != null && !SpawnerC.getLifeStats().isAlreadyDead()){
				move(282055);
				SpawnerC.getEffectController().removeEffect(19222);
				applyEffect(SpawnerC, SpawnerC ,19223);
				spawned(SpawnerC);
				}
				break;
			case 4:
				if(SpawnerD != null && !SpawnerD.getLifeStats().isAlreadyDead()){
				move(282054);
				SpawnerD.getEffectController().removeEffect(19222);
				applyEffect(SpawnerD, SpawnerD ,19223);
				spawned(SpawnerD);
				}
				break;
		}
		}
	}
