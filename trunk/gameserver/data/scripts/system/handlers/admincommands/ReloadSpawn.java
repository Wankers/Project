package admincommands;

import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticObject;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.spawnengine.SpawnEngine;
import gameserver.utils.chathandlers.ChatCommand;
import gameserver.world.World;
import gameserver.world.knownlist.Visitor;

/**
 * @author Luno
 */
public class ReloadSpawn extends ChatCommand {

	public ReloadSpawn() {
		super("reload_spawn");
	}

	@Override
	public void execute(Player player, String... params) {
		int worldId = 0;
		if (params.length == 1 && "this".equals(params[0])) {
			worldId = player.getWorldId();
		}

		final int worldIdFinal = worldId;
		// despawn all
		World.getInstance().doOnAllObjects(new Visitor<VisibleObject>() {

			@Override
			public void visit(VisibleObject object) {
				if (worldIdFinal != 0 && object.getWorldId() != worldIdFinal) {
					return;
				}
				if (object instanceof Npc || object instanceof Gatherable || object instanceof StaticObject) {
					object.getController().delete();
				}
			}
		});

		if (worldId == 0) {
			SpawnEngine.spawnAll();
		}
		else {
			SpawnEngine.spawnWorldMap(worldId);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
