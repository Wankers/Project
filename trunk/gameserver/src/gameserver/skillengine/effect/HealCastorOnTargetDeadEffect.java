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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.HealType;
import gameserver.utils.MathUtil;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealCastorOnTargetDeadEffect")
public class HealCastorOnTargetDeadEffect extends EffectTemplate {

	@XmlAttribute
	protected HealType type;//unhandled for now
	@XmlAttribute
	protected float range;
	@XmlAttribute
	protected boolean healparty;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected() instanceof Player)
			super.calculate(effect, null, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		final Player player = (Player) effect.getEffector();
		final int valueWithDelta = value + delta * effect.getSkillLevel();

		ActionObserver observer = new ActionObserver(ObserverType.DEATH) {

			@Override
			public void died(Creature creature) {
				// Heal Caster first
				if (MathUtil.isIn3dRange(effect.getEffected(), player, range))
					player.getController().onRestore(HealType.HP, valueWithDelta);
				// Then check for party if healparty parameter is set
				if (healparty)
				{
					if (player.getPlayerGroup2() != null) {
						for (Player p : player.getPlayerGroup2().getMembers()) {
							if (p == player)
								continue;
							if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
								p.getController().onRestore(HealType.HP, valueWithDelta);
						}
					}
					else if (player.isInAlliance2()) {
						for (Player p : player.getPlayerAllianceGroup2().getMembers()) {
							if (!p.isOnline())
								continue;
							if (p.equals(player))
								continue;
							if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
								p.getController().onRestore(HealType.HP, valueWithDelta);
						}
					}
				}
			}
		};

		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		ActionObserver observer = effect.getActionObserver(position);
		if ((!effect.getEffected().getLifeStats().isAlreadyDead()) && (observer != null))
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
