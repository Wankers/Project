package admincommands;

import gameserver.controllers.attack.AggroInfo;
import gameserver.dataholders.DataManager;
import gameserver.model.Race;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.siege.SiegeNpc;
import gameserver.model.templates.walker.WalkerTemplate;
import gameserver.restrictions.RestrictionsManager;
import gameserver.spawnengine.ClusteredNpc;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;

/**
 * @author Nemiroff Date: 28.12.2009
 */
public class Info extends ChatCommand {

	public Info() {
		super("info");
	}

	@Override
	public void execute(Player admin, String... params) {
		VisibleObject target = admin.getTarget();
	
		if (target == null || target.equals(admin)) {
			PacketSendUtility.sendMessage(admin, "[Info about you]"
				+ "\nPlayer Id: " + admin.getObjectId()
				+ "\nMap ID: " + admin.getWorldId()
				+ "\nX: " + admin.getCommonData().getPosition().getX() + " / Y: " + admin.getCommonData().getPosition().getY()
				+ " / Z: " + admin.getCommonData().getPosition().getZ() + " / Heading: " + admin.getCommonData().getPosition().getHeading());
		}
		else if (target instanceof Npc) {
			Npc npc = (Npc) admin.getTarget();
			PacketSendUtility.sendMessage(admin,
				"[Info about target]" 
					+ "\nName: " + npc.getName() 
					+ "\nId: " + npc.getNpcId() + " / ObjectId: " + admin.getTarget().getObjectId() + " / StaticId: " + npc.getSpawn().getStaticId()
					+ "\nMap ID: " + admin.getTarget().getWorldId()
					+ "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY()
					+ " / Z: " + admin.getTarget().getZ() + " / Heading: " + admin.getTarget().getHeading());
			if (npc instanceof SiegeNpc){
				SiegeNpc siegeNpc = (SiegeNpc)npc;
				PacketSendUtility.sendMessage(admin,
					"[Siege info]" + "\nSiegeId: "+ siegeNpc.getSiegeId() + "\nSiegeRace: "+ siegeNpc.getSiegeRace());
			}
			PacketSendUtility.sendMessage(admin, "Tribe: " + npc.getTribe() + "\nRace: " + npc.getObjectTemplate().getRace()
				+ "\nNpcType: " + npc.getObjectTemplate().getNpcType().name() + "\nTemplateType: "
				+ npc.getObjectTemplate().getNpcTemplateType().name() + "\nAbyssType: "
				+ npc.getObjectTemplate().getAbyssNpcType().name() + "\nAI: " + npc.getAi2().getName());
			PacketSendUtility.sendMessage(
				admin,
				"[Relations to target]" + "\nisEnemy: " + admin.isEnemy(npc) + "\ncanAttack: "
					+ RestrictionsManager.canAttack(admin, target) + "\n[Relations to you]" + "\nisEnemy: " + npc.isEnemy(admin)
					+ "\nisAggressive: " + npc.isAggressiveTo(admin) + "\nisAggroIcon: " + admin.isAggroIconTo(npc));
			PacketSendUtility.sendMessage(admin, "[Life stats]" + "\nCur. HP: " + npc.getLifeStats().getCurrentHp()
				+ " / Cur. MP: " + npc.getLifeStats().getCurrentMp() + "\nMax. HP: " + npc.getLifeStats().getMaxHp()
				+ " / Max. MP: " + npc.getLifeStats().getMaxMp());
			int asmoDmg = 0;
			int elyDmg = 0;
			PacketSendUtility.sendMessage(admin, "[AgroList]");
			for (AggroInfo ai : npc.getAggroList().getList()) {
				if (!(ai.getAttacker() instanceof Creature))
					continue;
				Creature master = ((Creature) ai.getAttacker()).getMaster();
				if (master == null)
					continue;
				if (master instanceof Player) {
					Player player = (Player) master;
					PacketSendUtility.sendMessage(admin, "Name: " + player.getName() + " Dmg: " + ai.getDamage());
					if (player.getRace() == Race.ASMODIANS)
						asmoDmg += ai.getDamage();
					else
						elyDmg += ai.getDamage();
				}
			}
			PacketSendUtility.sendMessage(admin, "[TotalDmg]" + "\n(A) Dmg: " + asmoDmg + "\n(E) Dmg: " + elyDmg);
			if (npc.getSpawn().getWalkerId() != null)
			{
				WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(npc.getSpawn().getWalkerId());
				if (template != null) {
					PacketSendUtility.sendMessage(admin, "[Route]" + "\nRouteId: " + npc.getSpawn().getWalkerId() + " (Reversed: "
						+ template.isReversed() + ")" + "\nRandomWalk: " + npc.getSpawn().getRandomWalk());
					if (npc.getWalkerGroup() != null) {
						ClusteredNpc snpc = npc.getWalkerGroup().getClusterData(npc);
						PacketSendUtility.sendMessage(admin, "[Group]" + "\nType: " + npc.getWalkerGroup().getWalkType() + 
							" / XDelta: " + snpc.getXDelta() + " / YDelta: " + snpc.getYDelta() + " / Index: " + snpc.getWalkerIndex());						
					}
				}
			}
		}
		else if (target instanceof Gatherable) {
			Gatherable gather = (Gatherable) target;
			PacketSendUtility.sendMessage(admin, "[Info about gather]\n" + "Name: " + gather.getName()
				+ "\nId: " + gather.getObjectTemplate().getTemplateId() + " / ObjectId: " + admin.getTarget().getObjectId()
				+ "\nMap ID: " + admin.getTarget().getWorldId()
				+ "\nX: " + admin.getTarget().getX() + " / Y: " + admin.getTarget().getY() + " / Z: " + admin.getTarget().getZ()
				+ " / Heading: " + admin.getTarget().getHeading());
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
