package ai.instance.abyssal_splinter;

import ai.AggressiveNpcAI2;
import commons.network.util.ThreadPoolManager;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Npc;

/**
 * TODO: Remove Shield by killing spawns of ebonsoul in rukrils range etc.
 * 
 * @author Luzien
 */
@AIName("dayshadetwin")
public class DayshadeTwinsAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleDied() {
		super.handleDied();
		if (checkTwin()) {
			spawnChests();
		}
		else {
			scheduleRespawn();
		}
		AI2Actions.deleteOwner(this);
	}

	private void scheduleRespawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!checkTwin()) {
					switch (getNpcId()) {
						case 216948:
							spawn(216948, 415.330994f, 664.830994f, 437.470001f, (byte) 10); // rukril
							break;
						case 216949:
							spawn(216949, 447.037994f, 735.560974f, 437.490997f, (byte) 94); // ebonsoul
							break;
					}
				}
			}

		}, 60000); // Both bosses have to be killed within 60s, otherwise the first one respawns
	}

	private boolean checkTwin() { // other boss killed?
		Npc twin = null;
		if (getNpcId() == 216948) {
			twin = getPosition().getWorldMapInstance().getNpc(216949);
		}
		else {
			twin = getPosition().getWorldMapInstance().getNpc(216948);
		}
		return (twin == null || twin.getLifeStats().isAlreadyDead());
	}

	private void spawnChests() {
		spawn(700934, 406.744f, 655.591f, 440.413f, (byte) 0);
		spawn(700934, 408.109f, 650.901f, 440.441f, (byte) 0);
		spawn(700934, 402.404f, 655.552f, 440.421f, (byte) 0);
		spawn(700936, 403.983f, 651.584f, 440.284f, (byte) 0);
		spawn(700955, 452.897f, 692.361f, 432.692f, (byte) 0);
	}
}
