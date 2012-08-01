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
package ai;

import commons.network.util.ThreadPoolManager;
import commons.utils.Rnd;
import java.util.ArrayList;
import java.util.List;
import gameserver.ai2.AI2Actions;
import gameserver.ai2.AIName;
import gameserver.model.ai.Percentage;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.spawnengine.SpawnEngine;
import gameserver.world.World;

/**
 * @author Luzien, xTz, GoodT
 *
 */
@AIName("omega")
public class OmegaAI2 extends SummonerAI2 {

    private List<Integer> adds = new ArrayList<Integer>();

    @Override
    protected void handleIndividualSpawnedSummons(Percentage percent) {
    	int percento = percent.getPercent();
    	
    	if (percento <= 81 && percento > 60) {
            showdown(281945);
        } else if (percento <= 60 && percento > 40) {
            showdown(281946);
        } else if (percento <= 40 && percento > 20) {
            showdown(281947);
        } else if (percento <= 20) {
            showdown(281948);
        }
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        despawnAdds();
        adds.clear();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        despawnAdds();
        adds.clear();
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
        despawnAdds();
        adds.clear();
    }

    private void despawnAdds() {
        for (Integer object : adds) {
            VisibleObject npc = World.getInstance().findVisibleObject(object);
            if (npc != null && npc.isSpawned()) {
                npc.getController().onDelete();
            }
        }
    }

    private void showdown(final int npcId) {
        AI2Actions.useSkill(this, 19189);
        AI2Actions.useSkill(this, 19191);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (hitPlayers()) {
                    spawnAdds(npcId); //spawn adds if a player gets hit
                }
            }
        }, 10000);
    }

    private boolean hitPlayers() {
        boolean hit = false;
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (isInRange(player, 30)) {
                hit = true;
            }
            break;
        }
        return hit;
    }

    private void spawnAdds(int npcId) {
        for (int i = 0; i < 3; i++) {
            SpawnTemplate template = rndSpawnInRange(npcId);
            VisibleObject npc = SpawnEngine.spawnObject(template, getPosition().getInstanceId());
            adds.add(npc.getObjectId());
        }
        if (npcId == 281948) {
            AI2Actions.useSkill(this, 18671);
        }
    }

    private SpawnTemplate rndSpawnInRange(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        float x1 = (float) (Math.cos(Math.PI * direction) * 5);
        float y1 = (float) (Math.sin(Math.PI * direction) * 5);
        return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
                + y1, getPosition().getZ(), getPosition().getHeading());
    }
}

