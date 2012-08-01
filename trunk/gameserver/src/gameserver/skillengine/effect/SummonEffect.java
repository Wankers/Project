/**
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
package gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;
import gameserver.utils.ThreadPoolManager;

/**
 * @author Simple
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonEffect")
public class SummonEffect extends EffectTemplate {

	@XmlAttribute(name = "npc_id", required = true)
	protected int npcId;
	@XmlAttribute(name = "time", required = true)
	protected int time; // in seconds

	@Override
	public void applyEffect(Effect effect) {
		Creature effected = effect.getEffected();
		effected.getController().createSummon(npcId, effect.getSkillId(), effect.getSkillLevel());
		if ((time > 0) && (effect.getEffected() instanceof Player)) {
			final Player effector = (Player)effect.getEffected();
			final Summon summon = effector.getSummon();
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if ((summon != null) && (summon.isSpawned()))
						summon.getController().release(UnsummonType.COMMAND);
				}
			}, time * 1000);
			summon.getController().addTask(TaskId.DESPAWN, task);
		}
	}

	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}
}
