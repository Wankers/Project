package gameserver.ai2.eventcallback;

import commons.callbacks.Callback;
import commons.callbacks.CallbackResult;
import gameserver.ai2.AbstractAI;
import gameserver.ai2.event.AIEventType;

/**
 * Callback that is broadcasted when general ai event occurs.
 * 
 * @author SoulKeeper
 */
@SuppressWarnings("rawtypes")
public abstract class OnHandleAIGeneralEvent implements Callback<AbstractAI> {

	@Override
	public CallbackResult beforeCall(AbstractAI obj, Object[] args) {
		return CallbackResult.newContinue();
	}

	@Override
	public CallbackResult afterCall(AbstractAI obj, Object[] args, Object methodResult) {
		AIEventType eventType = (AIEventType) args[0];
		onAIHandleGeneralEvent(obj, eventType);
		return CallbackResult.newContinue();
	}

	@Override
	public Class<? extends Callback> getBaseClass() {
		return OnHandleAIGeneralEvent.class;
	}

	protected abstract void onAIHandleGeneralEvent(AbstractAI obj, AIEventType eventType);
}
