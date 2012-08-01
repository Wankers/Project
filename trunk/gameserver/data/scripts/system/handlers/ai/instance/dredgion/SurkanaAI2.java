package ai.instance.dredgion;

import ai.OneDmgPerHitAI2;
import gameserver.ai2.AIName;
import gameserver.ai2.event.AIEventType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;

/**
 * recieve only 1 dmg with each attack(handled by super)
 * 
 * Aggro the whole room on attack
 * 
 * @author Luzien
 */
@AIName("surkana")
public class SurkanaAI2 extends OneDmgPerHitAI2 {

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		//roomaggro
		checkForSupport(creature);
	}

	private void checkForSupport(Creature creature) {
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			if (object instanceof Npc && isInRange(object, 25) && !((Npc) object).getLifeStats().isAlreadyDead())
				((Npc) object).getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
		}
	}
}
