package admincommands;

import commons.services.CronService;
import gameserver.configs.shedule.TvtSchedule;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import gameserver.services.tvt.TvtService;
import gameserver.services.tvt.TvtStartRunnable;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.ChatCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Tvt2 extends ChatCommand {

    /**
     * Just a logger
     */
    private static final Logger log = LoggerFactory.getLogger(TvtService.class);

    public Tvt2() {
        super("tvt2");
    }

    @Override
    public void execute(Player player, String... params) {
        String param = params[0];
        if (param.equals("info")) {
            for (TvtSchedule.TvtLevel l : TvtService.getInstance().getTvtSchedule().getTvtLevelList()) {
                TvtService.getInstance().getTvt(l.getId()).getHolders().info(player, true);
            }
        } else if (param.equals("start")) {
            String time = params[3] + " " + params[2];
            int tvtId;
            try {
                tvtId = Integer.parseInt(params[1]);
            } catch (NumberFormatException ex) {
                PacketSendUtility.sendMessage(player, "You must give number to tvtId.");
                return;
            }
            TvtSchedule.TvtLevel l = TvtService.getInstance().getTvtSchedule().getTvtLevel(tvtId);
            String tvtTime = "0 " + time + " ? * *";
            CronService.getInstance().schedule(new TvtStartRunnable(l.getId(), l.getStartTime(), l.getDuration(), l.getLevel(), l.getMapId()), tvtTime);
            PacketSendUtility.sendMessage(player, "CronService add tvt2 event shedule at: " + params[2] + ":" + params[3]);
            log.info("[TVT] Scheduled tvt of id " + l.getId() + " based on cron expression: " + tvtTime);
        } else if (param.equals("reg")) {
            PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(4, 0, TvtService.getInstance().getTvtByLevel(player.getLevel()).getRemainingTime()));
            TvtService.getInstance().regPlayer(player);
        } else if (param.equals("unreg")) {
            TvtService.getInstance().unRegPlayer(player);
        }
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "syntax \\tvt2 <info> <start> <reg> <unreg>");
        PacketSendUtility.sendMessage(player, "syntax \\tvt2 <start> <id> <time (18 00 hours minutes) > ");
    }
}
