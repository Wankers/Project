package gameserver.utils.chathandlers;

import commons.scripting.classlistener.ClassListener;
import commons.utils.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * Created on: 12.09.2009 14:13:24
 * 
 * @author Aquanox
 */
public class ChatCommandsLoader implements ClassListener {

	private ChatProcessor processor;

	public ChatCommandsLoader(ChatProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void postLoad(Class<?>[] classes) {
		for (Class<?> c : classes) {
			if (!isValidClass(c))
				continue;

			if (ClassUtils.isSubclass(c, ChatCommand.class)) {
				@SuppressWarnings("unchecked")
				Class<? extends ChatCommand> tmp = (Class<? extends ChatCommand>) c;
				if (tmp != null)
					try {
						processor.registerCommand(tmp.newInstance());
					}
					catch (InstantiationException e) {
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			}
		}

		processor.onCompileDone();
	}

	@Override
	public void preUnload(Class<?>[] classes) {

	}

	public boolean isValidClass(Class<?> clazz) {
		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		return true;
	}
}
