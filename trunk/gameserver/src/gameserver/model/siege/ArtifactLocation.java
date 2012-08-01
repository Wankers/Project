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
package gameserver.model.siege;

import gameserver.configs.main.SiegeConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.templates.siegelocation.ArtifactActivation;
import gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import gameserver.services.SiegeService;
import gameserver.skillengine.model.SkillTemplate;

/**
 * @author Source
 */
public class ArtifactLocation extends SiegeLocation {

	private ArtifactStatus status;

	public ArtifactLocation() {
		this.status = ArtifactStatus.IDLE;
	}

	public ArtifactLocation(SiegeLocationTemplate template) {
		super(template);
		// Artifacts Always Vulnerable
		setVulnerable(true);
	}

	@Override
	public int getNextState() {
		return STATE_VULNERABLE;
	}

	@Override
	public int getInfluenceValue() {
		return SiegeConfig.SIEGE_POINTS_ARTIFACT;
	}

	public long getLastActivation() {
		return this.lastArtifactActivation;
	}

	public void setLastActivation(long paramLong) {
		this.lastArtifactActivation = paramLong;
	}

	public int getCoolDown() {
		long i = this.template.getActivation().getCd();
		long l = System.currentTimeMillis() - this.lastArtifactActivation;
		if (l > i)
			return 0;
		else
			return (int) ((i - l) / 1000);
	}

	/**
	 * Returns DescriptionId that describes name of this artifact.<br>
	 *
	 * @return DescriptionId with name
	 */
	public DescriptionId getNameAsDescriptionId() {
		// Get Skill id, item, count and target defined for each artifact.
		ArtifactActivation activation = getTemplate().getActivation();
		int skillId = activation.getSkillId();
		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		return new DescriptionId(skillTemplate.getNameId());
	}

	public boolean isStandAlone() {
		return !SiegeService.getInstance().getFortresses().containsKey(getLocationId());
	}

	public FortressLocation getOwningFortress() {
		return SiegeService.getInstance().getFortressById(getLocationId());
	}

	/**
	 * @return the status
	 */
	public ArtifactStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ArtifactStatus status) {
		this.status = status;
	}

}