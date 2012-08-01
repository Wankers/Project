/*
 * This file is part of Aion Ascencion <aion-ascencion.org>.
 *
 *  Aion Ascencion is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Ascencion is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Ascencion.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.chatserver.model.channel;

import com.aionemu.chatserver.configs.Config;
import org.slf4j.Logger;
import java.io.UnsupportedEncodingException;

/**
 * @author Shadow = Multilanguage Support Localisation
 */

public class LocalizationChannels 
{
	public static byte[] Translate(byte[] channelIdentifier)
	{
		try {
			String channelIdentifierStr = new String(channelIdentifier, "UTF-16le");
			
			/**
			*
			* French Translations
			*
			* Job channels
			*/
			channelIdentifierStr = channelIdentifierStr.replace("Gladiateur[f:\"Gladiatrice\"]","Gladiator");
			channelIdentifierStr = channelIdentifierStr.replace("Templier[f:\"Templière\"]","Templar");
			channelIdentifierStr = channelIdentifierStr.replace("Sorcier[f:\"Sorcière\"]","Sorcerer");
			channelIdentifierStr = channelIdentifierStr.replace("Spiritualiste","Spiritmaster");
			channelIdentifierStr = channelIdentifierStr.replace("Aède","Chanter");
			channelIdentifierStr = channelIdentifierStr.replace("Rôdeur[f:\"Rôdeuse\"]","Ranger");
			channelIdentifierStr = channelIdentifierStr.replace("Assassin[f:\"Assassine\"]","Assassin");
			channelIdentifierStr = channelIdentifierStr.replace("Clerc","Cleric");
			
			/**
			* Language channels
			*/
			channelIdentifierStr = channelIdentifierStr.replace("Anglais","English");
			channelIdentifierStr = channelIdentifierStr.replace("Francais","French");
			channelIdentifierStr = channelIdentifierStr.replace("Allemand","German");
			channelIdentifierStr = channelIdentifierStr.replace("Italien","Italian");
			channelIdentifierStr = channelIdentifierStr.replace("Espagnol","Spanish");
			channelIdentifierStr = channelIdentifierStr.replace("Danois","Danish");
			channelIdentifierStr = channelIdentifierStr.replace("Suedois","Swedish");
			channelIdentifierStr = channelIdentifierStr.replace("Finnois","Finnish");
			channelIdentifierStr = channelIdentifierStr.replace("Norvegien","Norwegian");
			channelIdentifierStr = channelIdentifierStr.replace("Grec","Greek");
			
			/**
			* Unify channels
			*/
			if ( Config.REGION_TO_WORLD == true) channelIdentifierStr = channelIdentifierStr.replaceAll("public_([a-zA-Z0-9_]*)","public_LF1");
			if ( Config.TRADE_TO_WORLD == true) channelIdentifierStr = channelIdentifierStr.replaceAll("trade_([a-zA-Z0-9_]*)","trade_LF1");
			if ( Config.NO_FACTIONS_SEPARATION == true) channelIdentifierStr = channelIdentifierStr.replace(".1.AION.KOR",".0.AION.KOR");
			
			return channelIdentifierStr.getBytes("UTF-16le");
		
		} catch(UnsupportedEncodingException e) {
			return channelIdentifier;
		}
		
	}

}
