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
package admincommands;

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.stats.calc.StatOwner;
import gameserver.model.stats.calc.functions.IStatFunction;
import gameserver.model.stats.calc.functions.StatFunctionProxy;
import gameserver.model.stats.container.StatEnum;
import gameserver.skillengine.model.Effect;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author MrPoke
 */
public class Stat extends ChatCommand {

	private static final Logger log = LoggerFactory.getLogger(Stat.class);
	/**
	 * @param alias
	 */
	public Stat() {
		super("stat");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length >= 1) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			if (target instanceof Creature) {
				Creature creature = (Creature) target;

				TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
				
				if (params.length == 1) {
					for (IStatFunction stat : stats) {
						PacketSendUtility.sendMessage(admin, stat.toString());
					}
				}
				else if ("details".equals(params[1])) {
					for (IStatFunction stat : stats) {
						String details = collectDetails(stat);
						PacketSendUtility.sendMessage(admin, details);
						log.info(details);
					}
				}
			}
		}
	}

	private String collectDetails(IStatFunction stat) {
		StringBuffer sb = new StringBuffer();
		sb.append(stat.toString() + "\n");
		if(stat instanceof StatFunctionProxy){
			StatFunctionProxy proxy = (StatFunctionProxy) stat;
			sb.append(" -- " + proxy.getProxiedFunction().toString());
		}
		StatOwner owner = stat.getOwner();
		if(owner instanceof Effect){
			Effect effect = (Effect) owner;
			sb.append("\n -- skillId: " + effect.getSkillId());
			sb.append("\n -- skillName: " + effect.getSkillName());
		}
		return sb.toString();
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub

	}

}
