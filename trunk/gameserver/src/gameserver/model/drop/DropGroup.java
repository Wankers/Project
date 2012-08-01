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

import java.util.List;
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
@XmlType(name = "dropGroup", propOrder = {
    "drop"
})
public class DropGroup implements DropCalculator{
    protected List<Drop> drop;
    @XmlAttribute
    protected Race race = Race.PC_ALL;
    @XmlAttribute(name = "use_category")
    protected Boolean useCategory = true;
    @XmlAttribute(name = "name")
    protected String group_name;

    
    public List<Drop> getDrop() {
        return this.drop;
    }

    public Race getRace() {
        return race;
    }

    public Boolean isUseCategory() {
        return useCategory;
    }

		
		/**
		 * @return the name
		 */
		public String getGroupName() {
			if (group_name == null)
				return "";
			return group_name;
		}

		@Override
		public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race) {
			
			int maxDrop = 2;
			int maxDropNoCat = 4;
			int DropSize = 0;
			
			
			if (useCategory){
			
				for(int i=0; i<drop.size(); i++){
					if(drop.size()>=2)
                                            DropSize=Rnd.get(0, (drop.size()));
                                        else
                                            DropSize=drop.size();
                                        
					if(DropSize!=0)
						DropSize-=1;
						
					Drop d = drop.get(DropSize);//random acces
					index = d.dropCalculator(result, index, dropModifier, race);
					
					if(index>maxDrop)//Max dropsize for each group
						dropModifier=0; //No more loot
				}				
			
				/*Drop d = drop.get(Rnd.get(0,drop.size()-1));
                                
                                
                                
				return d.dropCalculator(result, index, dropModifier, race);*/
			}
			else{
				for(int i=0; i<drop.size(); i++){
					Drop d = drop.get(i);
					index = d.dropCalculator(result, index, dropModifier, race);
					if(index>maxDropNoCat)//Max dropsize for none Cat Group
						dropModifier=0; //No more loot				
				}
			}
			return index;
		}
}
