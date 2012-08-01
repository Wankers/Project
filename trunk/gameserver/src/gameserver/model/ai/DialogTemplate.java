package gameserver.model.ai;

import gameserver.model.ai.Page;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="DialogTemplate")
public class DialogTemplate
{

  @XmlElement(name="page")
  private Page page;

  public Page getPage()
  {
    return this.page;
  }
}