/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.dataholders;

import gameserver.model.templates.assembly.AssemblyTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author pixfid
 */
@XmlRootElement(name = "assembly_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssemblyData {

    private static final Logger log = LoggerFactory.getLogger(AssemblyData.class);
    @XmlElement(name = "assembly_template")
    protected List<AssemblyTemplate> list;
    private TIntObjectHashMap<AssemblyTemplate> assemblyData;
    int[] keys;
    int aid_;

    void afterUnmarshal(Unmarshaller u, Object parent) {
        assemblyData = new TIntObjectHashMap<AssemblyTemplate>();
        for (AssemblyTemplate it : list) {
            assemblyData.put(it.getItemid(), it);
        }
        list = null;
        keys = assemblyData.keys();
    }

    public int size() {
        return assemblyData.size();
    }

    public int findaid(int aid) {
        for (int i = keys.length; i-- > 0;) {
            AssemblyTemplate temp = assemblyData.get(keys[i]);
            int _aid = temp.geId();
            if (aid == _aid)
            {
                return temp.getItemid();
            }
        }
        return aid_;
    }

    public AssemblyTemplate getAssemblyTemplate(int itemId) {

        return assemblyData.get(itemId);
    }
}
