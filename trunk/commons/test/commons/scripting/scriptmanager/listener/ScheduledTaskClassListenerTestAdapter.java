package commons.scripting.scriptmanager.listener;

import commons.scripting.classlistener.ScheduledTaskClassListener;
import commons.services.CronService;

public class ScheduledTaskClassListenerTestAdapter extends ScheduledTaskClassListener {

	private final CronService cronService;

	public ScheduledTaskClassListenerTestAdapter(CronService cronService) {
		this.cronService = cronService;
	}

	@Override
	protected CronService getCronService() {
		return cronService;
	}
}
