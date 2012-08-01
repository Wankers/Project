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
package gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.state.CreatureSeeState;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.skillengine.model.Effect;
import gameserver.utils.PacketSendUtility;

/**
 * @author Sweetkr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchEffect")
public class SearchEffect extends EffectTemplate {

	//TODO! value should be enum already (@XmlEnum) - having int here is just stupid 

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void endEffect(Effect effect) {
		Creature effected = effect.getEffected();

		CreatureSeeState seeState;

		switch (value) {
			case 1:
				seeState = CreatureSeeState.SEARCH1;
				break;
			case 2:
				seeState = CreatureSeeState.SEARCH2;
				break;
			case 5:
				seeState = CreatureSeeState.SEARCH5;
				break;
			default:
				seeState = CreatureSeeState.NORMAL;
				break;
		}
		effected.unsetSeeState(seeState);

		if (effected instanceof Player) {
			PacketSendUtility.broadcastPacket((Player) effected, new SM_PLAYER_STATE(effected), true);
		}
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();

		CreatureSeeState seeState;

		switch (value) {
			case 1:
				seeState = CreatureSeeState.SEARCH1;
				break;
			case 2:
				seeState = CreatureSeeState.SEARCH2;
				break;
			case 5:
				seeState = CreatureSeeState.SEARCH5;
				break;
			default:
				seeState = CreatureSeeState.NORMAL;
				break;
		}
		effected.setSeeState(seeState);

		if (effected instanceof Player) {
			PacketSendUtility.broadcastPacket((Player) effected, new SM_PLAYER_STATE(effected), true);
		}
	}
}
