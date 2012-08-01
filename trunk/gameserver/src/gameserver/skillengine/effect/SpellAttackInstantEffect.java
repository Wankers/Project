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

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.action.DamageType;
import gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * @moded by GoodT
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAttackInstantEffect")
public class SpellAttackInstantEffect extends DamageEffect {

	@Override
	public void calculate(Effect effect) {
		
		final Creature effected = effect.getEffected();				
		
		if (effected instanceof Player)
		{
			//core support for http://www.aiondatabase.com/skill/19332
			if (effect.getSkillId() == 19332)
			{
				if (((Player)effected).getFlyState() > 0)
				{
					return;
				}	
			}
			//core support for http://www.aiondatabase.com/skill/18916 and http://www.aiondatabase.com/skill/18915
			for(Effect ef : effect.getEffected().getEffectController().getAbnormalEffects())
			{
				if (ef.getSkillId() == 18916)
				{
					if (effect.getSkillId() == 18913)
						return;					
				}
				
				if (ef.getSkillId() == 18915)
				{
					if (effect.getSkillId() == 18912)
						return;					
				}					
			}
		}
		
		super.calculate(effect, DamageType.MAGICAL);
	}
}
