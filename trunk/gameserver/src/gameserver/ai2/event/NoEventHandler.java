package gameserver.ai2.event;

import gameserver.ai2.AbstractAI;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;

public class NoEventHandler
  implements AIEventHandler
{
  public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
  {
  }

  public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType, Creature paramCreature)
  {
  }

  public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType, Player paramPlayer)
  {
  }
}
