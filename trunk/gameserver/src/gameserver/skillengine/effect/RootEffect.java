/*
 * This file is part of aion-unique <aion-unique.com>.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.stats.container.StatEnum;
import gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import gameserver.skillengine.model.Effect;
import gameserver.utils.PacketSendUtility;
import commons.utils.Rnd;

/**
 * @author ATracer
 * rework GoodT
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate {

	@XmlAttribute
	protected int resistchance = 100;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.ROOT_RESISTANCE, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
		effect.setAbnormal(AbnormalState.ROOT.getId());
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		
		//fix for gliding with root
		if (effected instanceof Player)
		{
			if (((Player)effected).isInState(CreatureState.GLIDING))
			{
				((Player)effected).getFlyController().endFly();
			}
		}

		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

			@Override
			public void attacked(Creature creature) {
				if (Rnd.get(0, 100) > resistchance)
					effected.getEffectController().removeEffect(effect.getSkillId());
			}
		};
		effected.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);

	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
