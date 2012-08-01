/*
 * This file is part of Aion Extreme Emulator <aion-core.net>
 *
 * Aion Extreme Emulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aion Extreme Emulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.configs.shedule;

import commons.utils.xml.JAXBUtil;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

/**
 * @author xTz
 * 
 */
@XmlRootElement(name = "limited_items_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class LimitedItemSchedule {

	@XmlElement(name = "limitedNpcs", required = true)
	private List<LimitedNpc> limitedNpcs;

	public List<LimitedNpc> getLimitedNpcs() {
		return limitedNpcs;
	}

	public void setLimitedNpcs(List<LimitedNpc> limitedNpcs) {
		this.limitedNpcs = limitedNpcs;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "limitedNpcs")
	public static class LimitedNpc {

		@XmlAttribute(required = true)
		private int npcId;

		@XmlElement(name = "limitedItemsTime", required = true)
		private List<String> limitedItemsTime;

		public int getNpcId() {
			return npcId;
		}

		public List<String> getLimitedItemsTimes() {
			return limitedItemsTime;
		}

		public void setLimitedItemsTimes(List<String> siegeTimes) {
			this.limitedItemsTime = siegeTimes;
		}
	}

	public static LimitedItemSchedule load() {
		LimitedItemSchedule li;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/limited_items_schedule.xml"));
			li = JAXBUtil.deserialize(xml, LimitedItemSchedule.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize limited items", e);
		}
		return li;
	}
}
