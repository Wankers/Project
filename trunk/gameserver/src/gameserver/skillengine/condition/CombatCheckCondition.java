package gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Skill;

/**
 * @author nrg
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CombatCheckCondition")
public class CombatCheckCondition extends Condition {

	@Override
	public boolean validate(Skill skill) {
		if(skill.getEffector() instanceof Player) {
		  return !((Player) skill.getEffector()).getController().isInCombat();
		}
		return true;
	}
}
