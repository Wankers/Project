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

import java.util.concurrent.ScheduledFuture;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import commons.utils.Rnd;
import gameserver.geoEngine.math.Vector3f;

import gameserver.ai2.AIState;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.event.AIEventType;
import gameserver.configs.main.GeoDataConfig;
import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.stats.container.StatEnum;
import gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import gameserver.skillengine.model.Effect;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.PositionUtil;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.geo.GeoService;

/**
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FearEffect")
public class FearEffect extends EffectTemplate {

	@XmlAttribute
	protected int resistchance = 100;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.FEAR_RESISTANCE, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effect.setAbnormal(AbnormalState.FEAR.getId());
		effected.getEffectController().setAbnormal(AbnormalState.FEAR.getId());

		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TARGET_IMMOBILIZE(effected));
		effected.getController().stopMoving();

		if (effected instanceof Npc)
			((NpcAI2)effected.getAi2()).setStateIfNot(AIState.FEAR);
		if (GeoDataConfig.FEAR_ENABLE) {
			ScheduledFuture<?> fearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(
				new FearTask(effector, effected), 0, 1000);
			effect.setPeriodicTask(fearTask, position);
		}
		
		//resistchance of fear effect to damage, if value is lower than 100, fear can be interrupted bz damage 
		//example skillId: 540 Terrible howl
		if (resistchance < 100) {
			ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

				@Override
				public void attacked(Creature creature) {
					if (Rnd.get(0, 100) > resistchance)
						effected.getEffectController().removeEffect(effect.getSkillId());
				}
			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);
		}
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.FEAR.getId());

		// for now we support only players
		if (GeoDataConfig.FEAR_ENABLE) {
			effect.getEffected().getMoveController().abortMove();// TODO impl stopMoving?
		}
		if (effect.getEffected() instanceof Npc){
			((NpcAI2)effect.getEffected().getAi2()).onCreatureEvent(AIEventType.ATTACK, effect.getEffector());
		}
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_TARGET_IMMOBILIZE(effect.getEffected()));
		
		if (resistchance < 100) {
			ActionObserver observer = effect.getActionObserver(position);
			if (observer != null)
				effect.getEffected().getObserveController().removeObserver(observer);
		}
	}

	class FearTask implements Runnable {

		private Creature effector;
		private Creature effected;

		FearTask(Creature effector, Creature effected) {
			this.effector = effector;
			this.effected = effected;
		}

		@Override
		public void run() {
			if (effected.getEffectController().isUnderFear()) {
				float x = effected.getX();
				float y = effected.getY();
				if (!MathUtil.isNearCoordinates(effected, effector, 40))
					return;
				byte moveAwayHeading = PositionUtil.getMoveAwayHeading(effector, effected);
				double radian = Math.toRadians(MathUtil.convertHeadingToDegree(moveAwayHeading));
				float maxDistance = effected.getGameStats().getMovementSpeedFloat();
				float x1 = (float) (Math.cos(radian) * maxDistance);
				float y1 = (float) (Math.sin(radian) * maxDistance);
				Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, x+x1, y+y1, effected.getZ(), true);
				if (effected.isFlying()) {
					closestCollision.setZ(effected.getZ());
				}
				if (effected instanceof Npc){
					((Npc)effected).getMoveController().resetMove();
					((Npc)effected).getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(),
					closestCollision.getZ());
				}
				else{
					effected.getMoveController().setNewDirection(closestCollision.getX(), closestCollision.getY(),
						closestCollision.getZ(), moveAwayHeading);
					effected.getMoveController().startMovingToDestination();
				}
			}
		}
	}
}
