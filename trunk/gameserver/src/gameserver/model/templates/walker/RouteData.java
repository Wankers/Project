package gameserver.model.templates.walker;

import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="routes")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteData
{

  @XmlElement(name="routestep")
  private List<RouteStep> stepids;

  void afterUnmarshal(Unmarshaller paramUnmarshaller, Object paramObject)
  {
    for (int i = 0; i < stepids.size() - 1; i++)
    {
      ((RouteStep)stepids.get(i)).setNextStep((RouteStep)stepids.get(i + 1));
      ((RouteStep)stepids.get(i)).setRouteStep(i + 1);
    }
    ((RouteStep)stepids.get(stepids.size() - 1)).setRouteStep(stepids.size());
    ((RouteStep)stepids.get(stepids.size() - 1)).setNextStep((RouteStep)stepids.get(0));
  }

  public List<RouteStep> getRouteSteps()
  {
    return stepids;
  }

  public RouteStep getRouteStep(int paramInt)
  {
    return (RouteStep)stepids.get(paramInt - 1);
  }
}