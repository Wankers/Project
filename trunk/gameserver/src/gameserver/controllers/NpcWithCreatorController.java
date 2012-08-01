/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.controllers;

import gameserver.ai2.event.AIEventType;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.NpcWithCreator;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import gameserver.utils.PacketSendUtility;

/**
 * @author GoodT
 *
 */
public class NpcWithCreatorController extends NpcController {

    @Override
    public void onDie(Creature lastAttacker) {
        super.onCreatureDie(lastAttacker);

        PacketSendUtility.broadcastPacket(this.getOwner(),
                new SM_EMOTION(this.getOwner(), EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()));

        this.getOwner().getAi2().onGeneralEvent(AIEventType.DIED);

        // deselect target at the end
        this.getOwner().setTarget(null);
        PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOKATOBJECT(this.getOwner()));

        onDelete();
    }

    @Override
    public void onDialogRequest(Player player) {
        return;
    }

    @Override
    public NpcWithCreator getOwner() {
        return (NpcWithCreator) super.getOwner();
    }

    @Override
    public void onDelete() {
        getOwner().setCreator(null);
        super.onDelete();
    }
}
