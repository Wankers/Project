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
package gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.player.PlayerReviveService;
import gameserver.skillengine.model.Effect;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectBaseEffect")
public class ResurrectBaseEffect extends BufEffect {

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);
		Creature effected = effect.getEffected();

		if (effected instanceof Player) {
			ActionObserver observer = new ActionObserver(ObserverType.DEATH) {

				@Override
				public void died(Creature creature) {
					if (creature instanceof Player) {
						Player effected = (Player) effect.getEffected();
						if (effected.isInInstance())
							PlayerReviveService.instanceRevive(effected);
						else if (effected.getKisk() != null)
							PlayerReviveService.kiskRevive(effected);
						else
							PlayerReviveService.bindRevive(effected);
					}
				}
			};
			effect.getEffected().getObserveController().attach(observer);
			effect.setActionObserver(observer, position);
		}
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);

		if (!effect.getEffected().getLifeStats().isAlreadyDead() && effect.getActionObserver(position) != null) {
			effect.getEffected().getObserveController().removeObserver(effect.getActionObserver(position));
		}
	}
}
