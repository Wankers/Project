package playercommands;

import gameserver.configs.shedule.TvtSchedule;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.tvt.TvtService;
import gameserver.utils.chathandlers.ChatCommand;


public class cmd_tvt2 extends ChatCommand {

    public cmd_tvt2() {
        super("tvt");
    }

    @Override
    public void execute(Player player, String... params) {
        for (TvtSchedule.TvtLevel l : TvtService.getInstance().getTvtSchedule().getTvtLevelList()) {
            TvtService.getInstance().getTvt(l.getId()).getHolders().info(player, false);
        }
    }

    @Override
    public void onFail(Player player, String message) {
        // TODO Auto-generated method stub
    }
}
