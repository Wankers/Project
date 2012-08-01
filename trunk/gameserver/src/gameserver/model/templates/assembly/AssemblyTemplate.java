/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.model.templates.assembly;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author pixfid
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssemblyTemplate")
public class AssemblyTemplate {

    protected List<Part> part;
    @XmlAttribute(name = "desc")
    protected int desc;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "parts_count")
    protected int parts_count = 0;
    @XmlAttribute(name = "itemid")
    protected int itemid;
    @XmlAttribute(name = "id")
    protected int id;
    @XmlElement(name = "part")
    protected List<AssemblyTemplate> partList;

    public List<Part> getPart() {
        if (part == null) {
            part = new ArrayList<Part>();
        }
        return this.part;
    }

    public Integer getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public Integer getParts_count() {
        return parts_count;
    }

    public Integer getItemid() {
        return itemid;
    }

    public Integer geId() {
        return id;
    }
}
