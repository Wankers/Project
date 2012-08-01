package gameserver.ai2.event;

import gameserver.ai2.AbstractAI;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;

public abstract interface AIEventHandler
{
  public abstract void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType);

  public abstract void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType, Creature paramCreature);

  public abstract void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType, Player paramPlayer);
}
