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
package gameserver.model.skill;

import commons.utils.Rnd;
import gameserver.model.templates.npcskill.NpcSkillTemplate;

/**
 * @author ATracer
 */
public abstract class NpcSkillEntry extends SkillEntry {

	public NpcSkillEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}

	public abstract boolean chanceReady();

	public abstract boolean hpReady(int hpPercentage);
}

/**
 * Skill entry which inherits properties from template (regular npc skills)
 */
class NpcSkillTemplateEntry extends NpcSkillEntry {

	private final NpcSkillTemplate template;

	public NpcSkillTemplateEntry(NpcSkillTemplate template) {
		super(template.getSkillid(), template.getSkillLevel());
		this.template = template;
	}

	@Override
	public boolean chanceReady() {
		return Rnd.get(0, 100) < template.getProbability();
	}

	@Override
	public boolean hpReady(int hpPercentage) {
		return !template.isAbooutHp() || (template.getMaxhp() >= hpPercentage && template.getMinhp() <= hpPercentage);
	}

}

/**
 * Skill entry which can be created on the fly (skills of servants, traps)
 */
class NpcSkillParameterEntry extends NpcSkillEntry {

	public NpcSkillParameterEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}

	@Override
	public boolean chanceReady() {
		return true;
	}

	@Override
	public boolean hpReady(int hpPercentage) {
		return true;
	}

}
