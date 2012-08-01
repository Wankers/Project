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
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;
import gameserver.skillengine.model.SkillType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillCriticalEffect")
public class OneTimeBoostSkillCriticalEffect extends BufEffect {

	@XmlAttribute
	private int count;
	@XmlAttribute
	private int value;//TODO handle this
	@XmlAttribute
	private boolean percent;//TODO handle this

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		effect.getEffector().setOneTimeBoostSkillCritical(true);

		final int stopCount = count;

		// Count Physical Skills
		ActionObserver observer = new ActionObserver(ObserverType.SKILLUSE) {

			private int count = 0;

			@Override
			public void skilluse(Skill skill) {
				if (count == stopCount)
					effect.endEffect();

				if ((count < stopCount) && (skill.getSkillTemplate().getType() == SkillType.PHYSICAL))
					count++;
			}
		};

		// TODO: verify if the effect counts normal hits too

		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffector().setOneTimeBoostSkillCritical(false);
		ActionObserver observer = effect.getActionObserver(position);
		effect.getEffected().getObserveController().removeObserver(observer);
	}
}
