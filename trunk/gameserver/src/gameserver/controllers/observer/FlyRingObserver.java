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
package gameserver.controllers.observer;

import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.flyring.FlyRing;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.utils3d.Point3D;
import gameserver.questEngine.QuestEngine;
import gameserver.questEngine.model.QuestEnv;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.SkillTemplate;
import gameserver.utils.MathUtil;

/**
 * @author xavier, Source
 */
public class FlyRingObserver extends ActionObserver {

	private Player player;

	private FlyRing ring;

	private Point3D oldPosition;

	SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(1856);

	public FlyRingObserver() {
		super(ObserverType.MOVE);
		this.player = null;
		this.ring = null;
		this.oldPosition = null;
	}

	public FlyRingObserver(FlyRing ring, Player player) {
		super(ObserverType.MOVE);
		this.player = player;
		this.ring = ring;
		this.oldPosition = new Point3D(player.getX(), player.getY(), player.getZ());
	}

	@Override
	public void moved() {
		Point3D newPosition = new Point3D(player.getX(), player.getY(), player.getZ());
		boolean passedThrough = false;

		if (ring.getPlane().intersect(oldPosition, newPosition)) {
			Point3D intersectionPoint = ring.getPlane().intersection(oldPosition, newPosition);
			if (intersectionPoint != null) {
				double distance = Math.abs(ring.getPlane().getCenter().distance(intersectionPoint));

				if (distance < ring.getTemplate().getRadius()) {
					passedThrough = true;
				}
			}
			else {
				if (MathUtil.isIn3dRange(ring, player, ring.getTemplate().getRadius())) {
					passedThrough = true;
				}
			}
		}

		if (passedThrough) {
			if (ring.getTemplate().getMap() == 400010000 || isQuestactive() || isInstancetactive()) {
				Effect speedUp = new Effect(player, player, skillTemplate, skillTemplate.getLvl(),
						skillTemplate.getDuration());
				speedUp.initialize();
				speedUp.addAllEffectToSucess();
				speedUp.applyEffect();
			}

			QuestEngine.getInstance().onPassFlyingRing(new QuestEnv(null, player, 0, 0), ring.getName());
		}

		oldPosition = newPosition;
	}

	private boolean isInstancetactive() {
		return ring.getPosition().getWorldMapInstance().getInstanceHandler().onPassFlyingRing(player, ring.getName());
	}

	private boolean isQuestactive() {
		int questId = player.getRace() == Race.ASMODIANS ? 2042 : 1044;
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		return qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) >= 2 && qs.getQuestVarById(0) <= 8;
	}

}