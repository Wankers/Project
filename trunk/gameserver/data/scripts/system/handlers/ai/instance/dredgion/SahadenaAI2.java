package ai.instance.dredgion;

import ai.AggressiveNpcAI2;

import gameserver.ai2.AIName;

/**
 * Sahadena the Abettor Spawns captain's cabin teleport for race that kills him
 * 
 * @author Luzien
 */
@AIName("sahadena")
public class SahadenaAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleDied() {
		switch (getAggroList().getPlayerWinnerRace()) {
			case ASMODIANS:
				spawn(730358, 496.522003f, 761.994995f, 388.660004f, (byte) 0);
				break;
			case ELYOS:
				spawn(730357, 496.522003f, 761.994995f, 388.660004f, (byte) 0);
				break;
		}
		super.handleDied();
	}
}
