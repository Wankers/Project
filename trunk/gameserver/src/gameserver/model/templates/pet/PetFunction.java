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
package gameserver.model.templates.pet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "petfunction")
public class PetFunction {

	@XmlAttribute(name = "type")
	private PetFunctionType type;
	@XmlAttribute(name = "id")
	private int id;
	@XmlAttribute(name = "slots")
	private int slots;

	public PetFunctionType getPetFunctionType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public int getSlots() {
		return slots;
	}

	public static PetFunction CreateEmpty() {
		PetFunction result = new PetFunction();
		result.type = PetFunctionType.NONE;
		return result;
	}

}
