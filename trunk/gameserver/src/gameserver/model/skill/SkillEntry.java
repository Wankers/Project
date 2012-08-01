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

import gameserver.dataholders.DataManager;
import gameserver.skillengine.model.SkillTemplate;

/**
 * @author ATracer
 */
public abstract class SkillEntry {

	protected final int skillId;

	protected int skillLevel;

	SkillEntry(int skillId, int skillLevel) {
		this.skillId = skillId;
		this.skillLevel = skillLevel;
	}

	public final int getSkillId() {
		return skillId;
	}

	public final int getSkillLevel() {
		return skillLevel;
	}

	public final String getSkillName() {
		return DataManager.SKILL_DATA.getSkillTemplate(getSkillId()).getName();
	}

	public void setSkillLvl(int skillLevel) {
		this.skillLevel = skillLevel;
	}

	public final SkillTemplate getSkillTemplate() {
		return DataManager.SKILL_DATA.getSkillTemplate(getSkillId());
	}

}
