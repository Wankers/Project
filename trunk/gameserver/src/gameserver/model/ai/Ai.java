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

import gameserver.model.ai.DialogTemplate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ai")
public class Ai {

	@XmlElement(name = "summons")
	private Summons summons;

	@XmlElement(name = "bombs")
	private Bombs bombs;
	
	@XmlElement(name = "dialogs")
	private DialogTemplate dialogs;

	@XmlAttribute(name = "npcId")
	private int npcId;

	public Summons getSummons() {
		return this.summons;
	}

	public Bombs getBombs() {
		return this.bombs;
	}
	
	public DialogTemplate getDialogTemplate() {
		return this.dialogs;
	}

	public int getNpcId() {
		return this.npcId;
	}

}