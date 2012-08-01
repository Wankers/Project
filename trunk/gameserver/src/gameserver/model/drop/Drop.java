/**
 * This file is part of Aion Extreme Emulator  <aion-core.net>.
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

package gameserver.model.drop;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import commons.utils.Rnd;
import gameserver.model.Race;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import java.util.Iterator;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drop")
public class Drop implements DropCalculator{

    @XmlAttribute(name = "item_id", required = true)
    protected int itemId;
    @XmlAttribute(name = "min_amount", required = true)
    protected int minAmount;
    @XmlAttribute(name = "max_amount", required = true)
    protected int maxAmount;
    @XmlAttribute(name = "chance", required = true)
    protected float chance;
    @XmlAttribute(name = "no_reduce")
    protected Boolean noReduce = false;


        public Drop() {
        }

        public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce) {
            this.itemId = itemId;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.chance = chance;
            this.noReduce = noReduce;
        }

    /**
     * Gets the value of the itemId property.
     * 
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Gets the value of the minAmount property.
     * 
     */
    public int getMinAmount() {
        return minAmount;
    }

    /**
     * Gets the value of the maxAmount property.
     * 
     */
    public int getMaxAmount() {
        return maxAmount;
    }

    /**
     * Gets the value of the chance property.
     * 
     */
    public float getChance() {
        return chance;
    }

    public Boolean isNoReduction() {
        return noReduce;
    }

        @Override
        public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race) {
            float percent = chance;
            if (!noReduce){
                percent *= dropModifier;
            }
            if (Rnd.get()*100 < percent){
                DropItem dropitem = new DropItem(this);
                dropitem.calculateCount();
                dropitem.setIndex(index++);
                result.add(dropitem);
            }
            return index;
        }
}
