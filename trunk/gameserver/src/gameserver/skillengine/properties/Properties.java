/*
 * This file is part of aion-unique <aion-unique.com>.
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
package gameserver.skillengine.properties;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Properties")
public class Properties {

	@XmlAttribute(name = "first_target", required = true)
	protected FirstTargetAttribute firstTarget;

	@XmlAttribute(name = "first_target_range", required = true)
	protected int firstTargetRange;

	@XmlAttribute(name = "awr")
	protected boolean addWeaponRange;

	@XmlAttribute(name = "target_relation", required = true)
	protected TargetRelationAttribute targetRelation;

	@XmlAttribute(name = "target_type", required = true)
	protected TargetRangeAttribute targetType;

	@XmlAttribute(name = "target_distance")
	protected int targetDistance;

	@XmlAttribute(name = "target_maxcount")
	protected int targetMaxCount;
	
	@XmlAttribute(name = "target_status")
	private List<String> targetStatus;
	
	@XmlAttribute(name = "revision_distance")
	protected int revisionDistance;

	/**
	 * @param skill
	 */
	public boolean validate(Skill skill) {
		if (firstTarget != null) {
			if (!FirstTargetProperty.set(skill, this)) {
				return false;
			}
		}
		if (firstTargetRange != 0 || addWeaponRange) {
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_START)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!TargetRangeProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetRelation != null) {
			if (!TargetRelationProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!MaxCountProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetStatus != null) {
			if (!TargetStatusProperty.set(skill, this)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param skill
	 */
	public boolean endCastValidate(Skill skill) {
		Creature firstTarget = skill.getFirstTarget();
		skill.getEffectedList().clear();
		skill.getEffectedList().add(firstTarget);

		if (firstTargetRange != 0) {
			if (!FirstTargetRangeProperty.set(skill, this, CastState.CAST_END)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!TargetRangeProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetRelation != null) {
			if (!TargetRelationProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetType != null) {
			if (!MaxCountProperty.set(skill, this)) {
				return false;
			}
		}
		if (targetStatus != null) {
			if (!TargetStatusProperty.set(skill, this)) {
				return false;
			}
		}
		return true;
	}

	public FirstTargetAttribute getFirstTarget() {
		return firstTarget;
	}

	public int getFirstTargetRange() {
		return firstTargetRange;
	}

	public boolean isAddWeaponRange() {
		return addWeaponRange;
	}

	public TargetRelationAttribute getTargetRelation() {
		return targetRelation;
	}

	public TargetRangeAttribute getTargetType() {
		return targetType;
	}

	public int getTargetDistance() {
		return targetDistance;
	}

	public int getTargetMaxCount() {
		return targetMaxCount;
	}
	
	public List<String> getTargetStatus() {
		return targetStatus;
	}
	
	public int getRevisionDistance() {
		return revisionDistance;
	}

	public enum CastState {
		CAST_START(true),
		CAST_END(false);

		private final boolean isCastStart;

		CastState(boolean isCastStart) {
			this.isCastStart = isCastStart;
		}

		public boolean isCastStart() {
			return isCastStart;
		}
	}
}
