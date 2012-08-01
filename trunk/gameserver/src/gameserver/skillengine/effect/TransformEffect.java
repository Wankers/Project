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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_TRANSFORM;
import gameserver.skillengine.model.Effect;
import gameserver.skillengine.model.TransformType;
import gameserver.utils.PacketSendUtility;

/**
 * @author Sweetkr, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformEffect")
public abstract class TransformEffect extends EffectTemplate {

	@XmlAttribute
	protected int model;

	@XmlAttribute
	protected TransformType type = TransformType.NONE;

	@XmlAttribute
	protected int panelid;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	public void endEffect(Effect effect, AbnormalState state) {
		final Creature effected = effect.getEffected();

		if (state != null)
			effected.getEffectController().unsetAbnormal(state.getId());
		
		if (effected instanceof Player) {
			int newModel = 0;
			for (Effect tmp : effected.getEffectController().getAbnormalEffects())	{
				for (EffectTemplate template : tmp.getEffectTemplates()) {
					if (template instanceof TransformEffect) {
						if (((TransformEffect)template).getTransformId() == model)
							continue;
						newModel = ((TransformEffect)template).getTransformId();
						break;
					}
				}
			}
			effected.setTransformedModelId(newModel);
		}
		else if (effected instanceof Summon) {
			effected.setTransformedModelId(0);
		}
		else if (effected instanceof Npc) {
			effected.setTransformedModelId(effected.getObjectTemplate().getTemplateId());
		}
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, panelid, false));

		if (effected instanceof Player)
			((Player) effected).setTransformed(false);
	}

	public void startEffect(Effect effect, AbnormalState effectId) {
		final Creature effected = effect.getEffected();
		
		if (effectId != null) {
			effect.setAbnormal(effectId.getId());
			effected.getEffectController().setAbnormal(effectId.getId());
		}
		
		effected.setTransformedModelId(model);
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, panelid, true));

		if (effected instanceof Player) {
			((Player) effected).setTransformed(true);
		}
	}

	public TransformType getTransformType() {
		return type;
	}
	
	public int getTransformId()	{
		return model;
	}

	public int getPanelId()	{
		return panelid;
	}
}
