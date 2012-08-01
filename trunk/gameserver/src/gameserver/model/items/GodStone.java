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
package gameserver.model.items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.utils.Rnd;
import gameserver.controllers.observer.ActionObserver;
import gameserver.controllers.observer.ObserverType;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.GodstoneInfo;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.skillengine.SkillEngine;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 */
public class GodStone extends ItemStone {

	private static final Logger log = LoggerFactory.getLogger(GodStone.class);

	private final GodstoneInfo godstoneInfo;
	private ActionObserver actionListener;
	private final int probability;
	private final int probabilityLeft;
	private final ItemTemplate godItem;

	public GodStone(int itemObjId, int itemId, PersistentState persistentState) {
		super(itemObjId, itemId, 0, persistentState);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		godItem = itemTemplate;
		godstoneInfo = itemTemplate.getGodstoneInfo();

		if (godstoneInfo != null) {
			probability = godstoneInfo.getProbability();
			probabilityLeft = godstoneInfo.getProbabilityleft();
		}
		else {
			probability = 0;
			probabilityLeft = 0;
			log.warn("CHECKPOINT: Godstone info missing for item : " + itemId);
		}

	}

	/**
	 * @param player
	 */
	public void onEquip(final Player player) {
		if (godstoneInfo == null || godItem == null)
			return;

		Item equippedItem = player.getEquipment().getEquippedItemByObjId(getItemObjId());
		int equipmentSlot = equippedItem.getEquipmentSlot();
		final int handProbability = equipmentSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? probability : probabilityLeft;
		actionListener = new ActionObserver(ObserverType.ATTACK) {

			@Override
			public void attack(Creature creature) {
				if (handProbability > Rnd.get(0, 1000)) {
					Skill skill = SkillEngine.getInstance().getSkill(player, godstoneInfo.getSkillid(),
						godstoneInfo.getSkilllvl(), player.getTarget(), godItem);
					skill.setFirstTargetRangeCheck(false);
					if (skill.canUseSkill()) {
						Effect effect = new Effect(player, creature, skill.getSkillTemplate(), 1, 0, godItem);
						effect.initialize();			
						effect.applyEffect();
						effect = null;
					}
				}
			}
		};

		player.getObserveController().addObserver(actionListener);
	}

	/**
	 * @param player
	 */
	public void onUnEquip(Player player) {
		if (actionListener != null)
			player.getObserveController().removeObserver(actionListener);

	}
}
