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
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Creature;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.HealType;
import gameserver.skillengine.model.SkillTemplate;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CondSkillLauncherEffect")
public class CondSkillLauncherEffect extends EffectTemplate {

	@XmlAttribute(name = "skill_id")
	protected int skillId;
	@XmlAttribute
	protected HealType type;
	
	//TODO what if you fall? effect is not applied? what if you use skill that consume hp?

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getGameStats().endEffect(effect);
		ActionObserver observer = effect.getActionObserver(position);
		effect.getEffected().getObserveController().removeObserver(observer);
	}

	@Override
	public void startEffect(final Effect effect) {
		ActionObserver observer = new ActionObserver(ObserverType.ATTACK) {

			@Override
			public void attack(Creature creature) {
				if (!effect.getEffected().getEffectController().hasAbnormalEffect(skillId)) {
					if (effect.getEffected().getLifeStats().getCurrentHp() <= ((effect.getEffected().getLifeStats().getMaxHp() * value) / 100)) {
						SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
						Effect e = new Effect(effect.getEffector(), effect.getEffected(), template, template.getLvl(),
							template.getEffectsDuration());
						e.initialize();
						e.applyEffect();
					}
				}
			}
		};
		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}
}
