/*
 * This file is part of Aion Extreme Emulator  <aion-core.net>
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
package gameserver.model.templates.item.actions;

import commons.database.dao.DAOManager;
import gameserver.dao.PlayerAppearanceDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.PlayerAppearance;
import gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.utils.PacketSendUtility;
import gameserver.world.knownlist.Visitor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosmeticItemAction")
public class CosmeticItemAction extends AbstractItemAction {

	@XmlAttribute
	protected String cosmeticName;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		if (template == null) {
			return false;
		}
		if (!template.getRace().equals(player.getRace())) {
			return false;
		}
		if (!template.getGenderPermitted().equals("ALL")) {
			if (!player.getGender().toString().equals(template.getGenderPermitted())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void act(final Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		PlayerAppearance playerAppearance = player.getPlayerAppearance();
		String type = template.getType();
		int id = template.getId();
		if (type.equals("hair_color")) {
			playerAppearance.setHairRGB(id);
		}
		else if (type.equals("face_color")) {
			playerAppearance.setSkinRGB(id);
		}
		else if (type.equals("lip_color")) {
			playerAppearance.setLipRGB(id);
		}
		else if (type.equals("eye_color")) {
			playerAppearance.setEyeRGB(id);
		}
		else if (type.equals("hair_type")) {
			playerAppearance.setHair(id);
		}
		else if (type.equals("face_type")) {
			playerAppearance.setFace(id);
		}
		else if (type.equals("voice_type")) {
			playerAppearance.setVoice(id);
		}
		else if (type.equals("makeup_type")) {
			playerAppearance.setTattoo(id);
		}
		else if (type.equals("tattoo_type")) {
			playerAppearance.setDeco(id);
		}
		else if (type.equals("preset_name")) {
			CosmeticItemTemplate.Preset preset = template.getPreset();
			playerAppearance.setEyeRGB((preset.getEyeColor()));
			playerAppearance.setLipRGB((preset.getLipColor()));
			playerAppearance.setHairRGB((preset.getHairColor()));
			playerAppearance.setSkinRGB((preset.getEyeColor()));
			playerAppearance.setHair((preset.getHairType()));
			playerAppearance.setFace((preset.getFaceType()));
			playerAppearance.setHeight((preset.getScale()));
		}
		DAOManager.getDAO(PlayerAppearanceDAO.class).store(player);
		player.getInventory().delete(targetItem);
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		player.getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player rangePlayer) {
				if (rangePlayer.isOnline()) {
					PacketSendUtility.sendPacket(rangePlayer, new SM_PLAYER_INFO(player, player.isEnemy(rangePlayer)));
				}
			}
		});
	}
}
