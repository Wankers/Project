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

import gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import java.util.List;
import java.util.Map;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javolution.util.FastMap;

/**
 *
 * @author xTz
 */
@XmlRootElement(name = "assembled_npcs")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssembledNpcsData {

	@XmlElement(name = "assembled_npc", type = AssembledNpcTemplate.class)
	private List<AssembledNpcTemplate> templates;
	private final Map<Integer, AssembledNpcTemplate> assembledNpcsTemplates = new FastMap<Integer, AssembledNpcTemplate>().shared();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (AssembledNpcTemplate template : templates) {
			assembledNpcsTemplates.put(template.getNr(), template);
		}
		templates.clear();
		templates = null;
	}

	public int size() {
		return assembledNpcsTemplates.size();
	}

	public AssembledNpcTemplate getAssembledNpcTemplate(Integer i) {
		return assembledNpcsTemplates.get(i);
	}
	
}
