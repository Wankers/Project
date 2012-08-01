/*
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
package gameserver.model.siege;

import gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import java.util.List;

/**
 * @author Source
 *
 * These bosses only appear when an faction conquer all balaurea fortress... If
 * Elyos conquer all fortress the Enraged Mastarius appear on Ancient City of
 * Marayas If Asmodians conquer all fortress the Enraged Veille appear on
 * Inggison Outpost He/She still active for 2 hours after that he/she disappear
 * and respawn again next day on the end of Siege (if the faction owns all
 * fortress)
 */
public class OutpostLocation extends SiegeLocation {

	public OutpostLocation() {
	}

	public OutpostLocation(SiegeLocationTemplate template) {
		super(template);
	}

	@Override
	public int getNextState() {
		return isVulnerable() ? STATE_INVULNERABLE : STATE_VULNERABLE;
	}

	/**
	 * @deprecated Should be configured from datapack
	 * @return Outpost Location Race
	 */
	@Deprecated
	public SiegeRace getLocationRace() {
		switch (getLocationId()) {
			case 3111:
				return SiegeRace.ASMODIANS;
			case 2111:
				return SiegeRace.ELYOS;
			default:
				throw new RuntimeException("Please move this to datapack");
		}
	}

	/**
	 * @return Fortresses that must be captured to own this outpost
	 */
	public List<Integer> getFortressDependency() {
		return template.getFortressDependency();
	}

	public boolean isSiegeAllowed() {
		return getLocationRace() == getRace();
	}

}