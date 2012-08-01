package ai.instance.dredgion;

import gameserver.ai2.NpcAI2;
import gameserver.ai2.AIName;

/**
 * Starboard/portside shield generator activates defense shield when destroyed
 * 
 * @author Luzien
 */
@AIName("shieldGen")
public class ShieldGenAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		super.handleDied();
		switch (getNpcId()) {
			case 730351:
				spawn(730345, 448.391998f, 493.641998f, 394.131989f, (byte) 90, 12);
				break;
			case 730352:
				spawn(730346, 520.875977f, 493.401001f, 394.433014f, (byte) 90, 133);
				break;
		}
	}
}
