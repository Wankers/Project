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
package gameserver.network.aion.serverpackets;

import java.util.List;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerCommonData;
import gameserver.model.stats.container.PlayerLifeStats;
import gameserver.model.team2.common.legacy.GroupEvent;
import gameserver.model.team2.group.PlayerGroup;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.skillengine.model.Effect;
import gameserver.world.WorldPosition;

/**
 * @author Lyahim, ATracer
 */
public class SM_GROUP_MEMBER_INFO extends AionServerPacket {

	private int groupId;
	private Player player;
	private GroupEvent event;

	public SM_GROUP_MEMBER_INFO(PlayerGroup group, Player player, GroupEvent event) {
		this.groupId = group.getTeamId();
		this.player = player;
		this.event = event;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PlayerLifeStats pls = player.getLifeStats();
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();

		if (event == GroupEvent.ENTER && !player.isOnline()) {
			event = GroupEvent.ENTER_OFFLINE;
		}

		writeD(groupId);
		writeD(player.getObjectId());
		if (player.isOnline()) {
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp()); // maxflighttime
			writeD(pls.getCurrentFp()); // currentflighttime
			writeD(wp.getMapId());
			writeD(wp.getMapId());
			writeF(wp.getX());
			writeF(wp.getY());
			writeF(wp.getZ());
		}
		else {
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeF(0);
			writeF(0);
			writeF(0);
		}

		writeC(pcd.getPlayerClass().getClassId()); // class id
		writeC(pcd.getGender().getGenderId()); // gender id
		writeC(pcd.getLevel()); // level

		writeC(event.getId()); // something events
		writeH(player.isOnline() ? 1 : 0); // TODO channel?
		writeC(player.isMentor() ? 0x01 : 0x00);

		switch (event) {
			case MOVEMENT:
				break;
			case LEAVE:
				writeH(0x00); // unk
				writeC(0x00); // unk
				break;
			case ENTER_OFFLINE:
			case JOIN:
				writeS(pcd.getName()); // name
				break;
			default:
				writeS(pcd.getName()); // name
				writeD(0x00); // unk
				writeD(0x00); // unk
				List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects.size()); // Abnormal effects
				for (Effect effect : abnormalEffects) {
					writeD(effect.getEffectorId()); // casterid
					writeH(effect.getSkillId()); // spellid
					writeC(effect.getSkillLevel()); // spell level
					writeC(effect.getTargetSlot()); // unk ?
					writeD(effect.getRemainingTime()); // estimatedtime
				}
				writeD(0x25F7); // unk 9719
				break;
		}
	}

}
