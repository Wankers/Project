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
package gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javolution.util.FastMap;

import gameserver.model.ai.Ai;
import gameserver.model.templates.ai.AITemplate;

/**
 * @author xTz
 */
@XmlRootElement(name = "ai_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class AIData {

	@XmlElement(name = "ai", type = Ai.class)
	private List<Ai> templates;
	private FastMap<Integer, AITemplate> aiTemplate = new FastMap<Integer, AITemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		aiTemplate.clear();
		for (Ai template : templates)
			aiTemplate.put(template.getNpcId(), new AITemplate(template));
	}

	public int size() {
		return aiTemplate.size();
	}

	public FastMap<Integer, AITemplate> getAiTemplate() {
		return aiTemplate;
	}
}
