/*
 * Copyright (c) 2011 by Aion Extreme
 *
 * This file is part of Aion Extreme <http://aion-core.net>.
 *
 * Aion Extreme <http://www.aion-core.net> is free software: you
 * can  redistribute  it and/or modify it under the terms
 * of  the GNU General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Aion Extreme <http://www.aion-core.net> is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without  even  the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See  the  GNU General Public License for more details.
 * You  should  have  received  a copy of the GNU General
 * Public License along with Aion Extreme 
 * <http://www.aion-core.net>.If not,see <http://www.gnu.org/licenses/>.
 */

package gameserver.services;

import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.spawns.SpawnMap;
import gameserver.model.templates.spawns.SpawnTemplate;
import gameserver.services.teleport.TeleportService;
import gameserver.spawnengine.SpawnEngine;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;


 /**
  * @author Luno
  * @co-author Aion Germany, Dallas, Iven, Dex, readapted by Seraphin
  */

public class PvPZoneService
{
	private static VisibleObject ELYGATE;
	private static VisibleObject ASMOGATE;
	private static VisibleObject CANNON;
	private static VisibleObject[] eventnpc;
	private static boolean opened = false;
	
	public static boolean Spawn(int ELYGATEnpcID, int ASMOGATEnpcID, int CANNONnpcID)
	{
		if (!opened)
		{
			//Gelkmaros Gate
			float x = 1812.5325f;
			float y = 2929.5986f;
			float z = 554.7982f;
			byte heading = 0;
			int worldId = 220070000;
			SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, ASMOGATEnpcID, x, y, z, heading);
			VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, 1);
			
			//Inggison Gate
			float x2 = 1272.4163f;
			float y2 = 330.46143f;	
			float z2 = 597.85114f;
			byte heading2 = 0;
			int worldId2 = 210050000;
			SpawnTemplate spawn2 = SpawnEngine.addNewSingleTimeSpawn(worldId2, ELYGATEnpcID, x2, y2, z2, heading2);
			VisibleObject visibleObject2 = SpawnEngine.spawnObject(spawn2, 1);
			
			//Cannon
			float x3 = 681.724f;
			float y3 = 550.67f;
			float z3 = 1023.79f;
			byte heading3 = 2;
			int worldId3 = 300100000;
			SpawnTemplate spawn3 = SpawnEngine.addNewSingleTimeSpawn(worldId3, CANNONnpcID, x3, y3, z3, heading3);
			VisibleObject visibleObject3 = SpawnEngine.spawnObject(spawn3, 1);
			
			
			
			
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player object)
				{
					PacketSendUtility.sendWhiteMessageOnCenter(object, "PvP Event, Steel Rake Zone, is ready for fighting!");
					return;
				}
				
			});
			
			ELYGATE = visibleObject;
			ASMOGATE = visibleObject2;
			CANNON = visibleObject3;
			opened = true;
			
			int mapId = 300100000;			
			eventnpc = new VisibleObject[34];
			
		//Eventnpc
			SpawnTemplate spawn4 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 505.083, (float) 522.940, (float) 1033.27, (byte) 103);
			SpawnEngine.spawnObject(spawn4, 1);
			SpawnTemplate spawn5 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 493.386, (float) 523.142, (float) 1033.27, (byte) 95);
			SpawnEngine.spawnObject(spawn5, 1);
			SpawnTemplate spawn6 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 478.550, (float) 522.938, (float) 1033.27, (byte) 119);
			SpawnEngine.spawnObject(spawn6, 1);
			SpawnTemplate spawn7 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 494.086, (float) 543.000, (float) 1034.76, (byte) 29);
			SpawnEngine.spawnObject(spawn7, 1);
			SpawnTemplate spawn8 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 493.904, (float) 535.081, (float) 1034.75, (byte) 90);
			SpawnEngine.spawnObject(spawn8, 1);
			SpawnTemplate spawn9 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 486.043, (float) 522.754, (float) 1033.27, (byte) 89);
			SpawnEngine.spawnObject(spawn9, 1);
			SpawnTemplate spawn10 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204510, (float) 515.335, (float) 523.375, (float) 1033.27, (byte) 91);
			SpawnEngine.spawnObject(spawn10, 1);
			SpawnTemplate spawn11 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 618.918, (float) 541.987, (float) 1031.07, (byte) 57);
			SpawnEngine.spawnObject(spawn11, 1);
			SpawnTemplate spawn12 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 618.587, (float) 550.355, (float) 1031.06, (byte) 62);
			SpawnEngine.spawnObject(spawn12, 1);
			SpawnTemplate spawn13 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 615.788, (float) 545.923, (float) 1031.05, (byte) 61);
			SpawnEngine.spawnObject(spawn13, 1);
			SpawnTemplate spawn14 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 638.013, (float) 469.111, (float) 1031.04, (byte) 61);
			SpawnEngine.spawnObject(spawn14, 1);
			SpawnTemplate spawn15 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 637.519, (float) 475.227, (float) 1031.05, (byte) 63);
			SpawnEngine.spawnObject(spawn15, 1);
			SpawnTemplate spawn16 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 626.466, (float) 469.339, (float) 1031.04, (byte) 66);
			SpawnEngine.spawnObject(spawn16, 1);
			SpawnTemplate spawn17 = SpawnEngine.addNewSingleTimeSpawn(mapId, 204711, (float) 626.797, (float) 476.795, (float) 1031.05, (byte) 73);
			SpawnEngine.spawnObject(spawn17, 1);
			SpawnTemplate spawn18 = SpawnEngine.addNewSingleTimeSpawn(mapId, 798920, (float) 463.071, (float) 559.572, (float) 1032.98, (byte) 105);
			SpawnEngine.spawnObject(spawn18, 1);
			SpawnTemplate spawn19 = SpawnEngine.addNewSingleTimeSpawn(mapId, 799219, (float) 686.955, (float) 465.914, (float) 1022.67, (byte) 57);
			SpawnEngine.spawnObject(spawn19, 1);
			SpawnTemplate spawn20 = SpawnEngine.addNewSingleTimeSpawn(mapId, 730207, (float) 482.904, (float) 540.082, (float) 1034.74, (byte) 53);
			SpawnEngine.spawnObject(spawn20, 1);
			SpawnTemplate spawn21 = SpawnEngine.addNewSingleTimeSpawn(mapId, 250146, (float) 411.274, (float) 544.287, (float) 1072.08, (byte) 91);
			SpawnEngine.spawnObject(spawn21, 1);
			SpawnTemplate spawn22 = SpawnEngine.addNewSingleTimeSpawn(mapId, 250146, (float) 416.153, (float) 473.500, (float) 1072.08, (byte) 31);
			SpawnEngine.spawnObject(spawn22, 1);
			SpawnTemplate spawn23 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 624.570, (float) 541.108, (float) 936.094, (byte) 60);
			SpawnEngine.spawnObject(spawn23, 1);
			SpawnTemplate spawn24 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700473, (float) 609.516, (float) 481.285, (float) 936.027, (byte) 28);
			SpawnEngine.spawnObject(spawn24, 1);
			SpawnTemplate spawn25 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 578.419, (float) 514.348, (float) 944.670, (byte) 88);
			SpawnEngine.spawnObject(spawn25, 1);
			SpawnTemplate spawn26 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 351.892, (float) 587.142, (float) 948.015, (byte) 77);
			SpawnEngine.spawnObject(spawn26, 1);
			SpawnTemplate spawn27 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 283.347, (float) 452.374, (float) 952.558, (byte) 90);
			SpawnEngine.spawnObject(spawn27, 1);
			SpawnTemplate spawn28 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 239.519, (float) 523.133, (float) 948.674, (byte) 80);
			SpawnEngine.spawnObject(spawn28, 1);
			SpawnTemplate spawn29 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 428.203, (float) 536.419, (float) 946.658, (byte) 39);
			SpawnEngine.spawnObject(spawn29, 1);
			SpawnTemplate spawn30 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 474.668, (float) 573.676, (float) 958.078, (byte) 114);
			SpawnEngine.spawnObject(spawn30, 1);
			SpawnTemplate spawn31 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 516.533, (float) 533.960, (float) 958.744, (byte) 6);
			SpawnEngine.spawnObject(spawn31, 1);
			SpawnTemplate spawn32 = SpawnEngine.addNewSingleTimeSpawn(mapId, 281443, (float) 242.355, (float) 506.127, (float) 948.674, (byte) 119);
			SpawnEngine.spawnObject(spawn32, 1);
			SpawnTemplate spawn33 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700473, (float) 237.316, (float) 506.174, (float) 948.674, (byte) 62);
			SpawnEngine.spawnObject(spawn33, 1);
			SpawnTemplate spawn34 = SpawnEngine.addNewSingleTimeSpawn(mapId, 250147, (float) 233.975, (float) 489.493, (float) 948.674, (byte) 89);
			SpawnEngine.spawnObject(spawn34, 1);
			SpawnTemplate spawn35 = SpawnEngine.addNewSingleTimeSpawn(mapId, 281938, (float) 413.900, (float) 510.320, (float) 1071.85, (byte) 62);
			SpawnEngine.spawnObject(spawn35, 1);
			SpawnTemplate spawn36 = SpawnEngine.addNewSingleTimeSpawn(mapId, 217778, (float) 264.693, (float) 527.494, (float) 947.018, (byte) 26);
			SpawnEngine.spawnObject(spawn36, 1);
			SpawnTemplate spawn37 = SpawnEngine.addNewSingleTimeSpawn(mapId, 700554, (float) 457.228, (float) 511.480, (float) 952.556, (byte) 6);
			SpawnEngine.spawnObject(spawn37, 1);
			
		}
		return false;
	}
	
	public static void AdminReset()
	{
		try { Delete(); } catch (Exception ex) { }
		ELYGATE = null;
		ASMOGATE = null;
		CANNON = null;
	}

	public static boolean Delete()
	{
		if (opened)
		{
			DataManager.SPAWNS_DATA2.getTemplates().remove(ELYGATE);
			
			DataManager.SPAWNS_DATA2.getTemplates().remove(ASMOGATE);
			
			DataManager.SPAWNS_DATA2.getTemplates().remove(CANNON);
			
			for(VisibleObject npcs : eventnpc){
				Npc spawn = (Npc) npcs;
				spawn.getController().delete();
			}
            
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player object)
				{
					if (object.getWorldId() == 300100000)
					{
						if (object.getX() <= 1000f && object.getX() >= 100f && object.getY() <= 1000f && object.getY() >= 100f)
						if (object.getCommonData().getRace().equals(Race.ELYOS))
						{
							TeleportService.teleportTo(object, 210050000, 1275.1191f, 328.14645f, 597.85114f, 0);
							PacketSendUtility.sendMessage(object, "PvP Event, Steel Rake Zone, was closed and you will be ported to Inggison.");
						}
						else if (object.getCommonData().getRace().equals(Race.ASMODIANS))
						{
							TeleportService.teleportTo(object, 220070000, 1808.944f, 2931.2979f, 554.8001f, 0); //Gelkmaros
							PacketSendUtility.sendMessage(object, "PvP Event, Steel Rake Zone, was closed and you will be ported to Gelkmaros.");
						}
					}
					PacketSendUtility.sendYellowMessageOnCenter(object, "PvP Event,Steel Rake Zone,is not accesible anymore!");
					return;
				}
			});
			opened = false;
			return true;
		}
		else
			return false;
	}
}