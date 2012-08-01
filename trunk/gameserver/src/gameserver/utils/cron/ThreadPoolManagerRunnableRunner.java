package gameserver.utils.cron;

import commons.services.cron.RunnableRunner;
import gameserver.utils.ThreadPoolManager;

public class ThreadPoolManagerRunnableRunner extends RunnableRunner{

	@Override
	public void executeRunnable(Runnable r) {
		ThreadPoolManager.getInstance().execute(r);
	}

	@Override
	public void executeLongRunningRunnable(Runnable r) {
		ThreadPoolManager.getInstance().executeLongRunning(r);
	}
}
