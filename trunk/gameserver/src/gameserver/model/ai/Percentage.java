/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package gameserver.model.ai;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Percentage")
public class Percentage {

	@XmlAttribute(name = "percent")
	protected int percent;
	@XmlAttribute(name = "skillId")
	protected int skillId = 0;
	@XmlAttribute(name = "isIndividual")
	protected boolean isIndividual = false;
	@XmlElement(name = "summonGroup")
	protected List<SummonGroup> summons;

	public List<SummonGroup> getSummons() {
		return summons;
	}

	public int getPercent() {
		return percent;
	}

	public int getSkillId() {
		return skillId;
	}
	public boolean isIndividual() {
		return isIndividual;
	}
}