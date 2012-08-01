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
package gameserver.ai2.handler;

import java.util.Collections;

import gameserver.ai2.NpcAI2;
import gameserver.controllers.attack.AttackResult;
import gameserver.controllers.attack.AttackStatus;
import gameserver.model.TribeClass;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_ATTACK;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class AggroEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onAggro(NpcAI2 npcAI, final Creature creature) {
		final Npc owner = npcAI.getOwner();
		// TODO move out?
		if (creature.getAdminNeutral() == 1 || creature.getAdminNeutral() == 3 || creature.getAdminEnmity() == 1
				|| creature.getAdminEnmity() == 3)
			return;
		PacketSendUtility.broadcastPacket(
				owner,
				new SM_ATTACK(owner, creature, 0, 633, 0, Collections.singletonList(new AttackResult(0, AttackStatus.NORMALHIT))));

		ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, creature, true), 500);
	}

	public static void onCreatureAttacked(NpcAI2 npcAI, Creature creature) {
		Npc owner = npcAI.getOwner();
		if (creature.isSupportFrom(owner) && MathUtil.isInRange(owner, creature, owner.getAggroRange())
				&& GeoService.getInstance().canSee(owner, creature)) {
			VisibleObject target = creature.getTarget();
			if (target != null && target instanceof Creature) {
				Creature targetCreature = (Creature) target;
				PacketSendUtility.broadcastPacket(
						owner,
						new SM_ATTACK(owner, targetCreature, 0, 633, 0, Collections.singletonList(new AttackResult(0,
						AttackStatus.NORMALHIT))));
				ThreadPoolManager.getInstance().schedule(new AggroNotifier(owner, targetCreature, false), 500);
			}
		}
	}

	public static void onCreatureAttacking(NpcAI2 npcAI, Creature attacker) {
		Npc owner = npcAI.getOwner();
		TribeClass tribe = owner.getTribe();
		if (!tribe.isGuard()) {
			return;
		}
		VisibleObject target = attacker.getTarget();
		if (target != null && target instanceof Player) {
			Player playerTarget = (Player) target;
			if (!owner.isEnemy(playerTarget) && owner.isEnemy(attacker) && MathUtil.isInRange(owner, playerTarget, owner.getAggroRange())
					&& GeoService.getInstance().canSee(owner, attacker)) {
				owner.getAggroList().startHate(attacker);
			}
		}
	}

	private static final class AggroNotifier implements Runnable {

		private Creature aggressive;
		private Creature target;
		private boolean callSocial;

		AggroNotifier(Creature aggressive, Creature target, boolean callSocial) {
			this.aggressive = aggressive;
			this.target = target;
			this.callSocial = callSocial;
		}

		@Override
		public void run() {
			aggressive.getAggroList().addHate(target, 1, callSocial);
			aggressive = null;
			target = null;
		}

	}

}