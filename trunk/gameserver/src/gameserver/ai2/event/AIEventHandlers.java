package gameserver.ai2.event;

import java.util.EnumMap;
import gameserver.ai2.AbstractAI;
import gameserver.ai2.NpcAI2;
import gameserver.ai2.handler.ActivateEventHandler;
import gameserver.ai2.handler.DiedEventHandler;
import gameserver.ai2.handler.SpawnEventHandler;

public final class AIEventHandlers
{
  public static final EnumMap<AIEventType, AIEventHandler> NPC_HANDLERS = new EnumMap(AIEventType.class);
  public static final AIEventHandler ACTIVATE_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      ActivateEventHandler.onActivate((NpcAI2)paramAbstractAI);
    }
  };
  public static final AIEventHandler DEACTIVATE_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      ActivateEventHandler.onDeactivate((NpcAI2)paramAbstractAI);
    }
  };
  public static final AIEventHandler SPAWNED_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      SpawnEventHandler.onSpawn((NpcAI2)paramAbstractAI);
    }
  };
  public static final AIEventHandler RESPAWNED_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      SpawnEventHandler.onRespawn((NpcAI2)paramAbstractAI);
    }
  };
  public static final AIEventHandler DESPAWNED_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      SpawnEventHandler.onDespawn((NpcAI2)paramAbstractAI);
    }
  };
  public static final AIEventHandler DIED_HANDLER = new NoEventHandler()
  {
    public void handle(AbstractAI paramAbstractAI, AIEventType paramAIEventType)
    {
      DiedEventHandler.onSimpleDie((NpcAI2)paramAbstractAI);
    }
  };

  static
  {
    NPC_HANDLERS.put(AIEventType.ACTIVATE, ACTIVATE_HANDLER);
    NPC_HANDLERS.put(AIEventType.DEACTIVATE, DEACTIVATE_HANDLER);
    NPC_HANDLERS.put(AIEventType.SPAWNED, SPAWNED_HANDLER);
    NPC_HANDLERS.put(AIEventType.RESPAWNED, RESPAWNED_HANDLER);
    NPC_HANDLERS.put(AIEventType.DESPAWNED, DESPAWNED_HANDLER);
    NPC_HANDLERS.put(AIEventType.DIED, DIED_HANDLER);
  }
}
