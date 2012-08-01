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
package gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.handlers.template.SkillUse;

/**
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseData")
public class SkillUseData extends XMLQuest {

	@XmlAttribute(name = "start_npc_id")
	protected int startNpc;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpc;
	@XmlElement(name = "skill", required = true)
	protected List<QuestSkillData> skills;

	@Override
	public void register(QuestEngine questEngine) {
		FastMap<Integer, QuestSkillData> questSkills = new FastMap<Integer, QuestSkillData>();
		for (QuestSkillData qsd : skills) {
			questSkills.put(qsd.getSkillId(), qsd);
		}
		SkillUse questTemplate = new SkillUse(id, startNpc, endNpc, questSkills);
		questEngine.addQuestHandler(questTemplate);
	}
}
