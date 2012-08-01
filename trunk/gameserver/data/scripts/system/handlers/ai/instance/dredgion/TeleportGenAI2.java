package ai.instance.dredgion;

import gameserver.ai2.NpcAI2;
import gameserver.ai2.AIName;

/**
 * Starboard/portside teleport generator activates supply room teleporter when destroyed
 * 
 * @author Luzien
 */
@AIName("teleportGen")
public class TeleportGenAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		super.handleDied();
		switch (getNpcId()) {
			case 730349:
				spawn(730314, 572.104004f, 185.238998f, 432.559998f, (byte) 0);
				break;
			case 730350:
				spawn(730315, 415.076996f, 173.852997f, 432.533997f, (byte) 0);
				break;
		}
	}
}
