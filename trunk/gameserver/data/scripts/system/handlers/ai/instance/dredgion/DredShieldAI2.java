package ai.instance.dredgion;

import ai.NoActionAI2;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;

/**
 * AI for NPCs recieving 1dmg per hit
 * 
 * @author Luzien
 */
@AIName("dredshield")
public class DredShieldAI2 extends NoActionAI2 {
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		
	}
	
	@Override
	public int modifyDamage(int damage) {
		return 1;
	}
}
