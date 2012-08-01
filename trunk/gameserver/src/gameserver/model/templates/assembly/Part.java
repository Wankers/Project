/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver.model.templates.assembly;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author pixfid
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Part")
public class Part {

    @XmlAttribute(name = "id")
    protected int id;

    public Integer getId() {
        return id;
    }
}
