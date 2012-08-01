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

package gameserver.model.templates.zone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cylinder")
public class Cylinder {

    @XmlAttribute
    protected Float top;
    @XmlAttribute
    protected Float bottom;
    @XmlAttribute
    protected Float x;
    @XmlAttribute
    protected Float y;
    @XmlAttribute
    protected Float r;

    public Float getTop() {
        return top;
    }

    public Float getBottom() {
        return bottom;
    }

    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public Float getR() {
        return r;
    }
}
