/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package ai.instance.steelRake;

import ai.AggressiveNpcAI2;
import commons.utils.Rnd;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.WorldMapInstance;
import java.util.List;

/**
 * @author xTz
 */
@AIName("goldeneyemantutu")
public class GoldenEyeMantutuAI2 extends AggressiveNpcAI2 {

	private boolean isHome = true;

	private int skill;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome) {
			isHome = false;
			check();
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		endBattle();
		Npc npc = getPosition().getWorldMapInstance().getNpc(215412);
		if (npc != null) {
			npc.getEffectController().removeEffect(18189);
		}
	}

	@Override
	protected void handleBackHome() {
		isHome = true;
		super.handleBackHome();
		endBattle();
	}

	private void check() {
		if (getPosition().isSpawned() && !isAlreadyDead() && !isHome) {
			int rnd = Rnd.get(1, 2);
			switch (rnd) {
				case 1:
					skill = 18180; // Hunger
					spawn1();
					break;
				case 2:
					skill = 18181; // Thirst
					spawn2();
					break;
			}
			AI2Actions.targetSelf(this);
			AI2Actions.useSkill(this, skill);
			doSchedule();
		}
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				check();
			}

		}, 25000);
	}

	private void spawn1() {
		int rnd = Rnd.get(1, 3);
		switch (rnd) {
			case 1:
				spawn(700527, 716.539978f, 494.670013f, 939.606934f, (byte) 119);
				break;
			case 2:
				spawn(700527, 709.598877f, 511.191559f, 939.606750f, (byte) 119);
				break;
			case 3:
				spawn(700527, 701.010010f, 495.019989f, 939.606750f, (byte) 119);
				break;
		}
	}

	private void spawn2() {
		int rnd = Rnd.get(1, 3);
		switch (rnd) {
			case 1:
				spawn(700528, 716.280396f, 509.384094f, 939.606750f, (byte) 119);
				break;
			case 2:
				spawn(700528, 704.931213f, 490.295959f, 939.606750f, (byte) 119);
				break;
			case 3:
				spawn(700528, 701.080017f, 506.279999f, 939.606750f, (byte) 119);
				break;
		}
	}

	private void endBattle() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		despawn(instance.getNpcs(700527));
		despawn(instance.getNpcs(700528));
	}

	private void despawn(List<Npc> npcs) {
		for (Npc npc : npcs) {
			npc.getController().onDelete();
		}
	}

}
