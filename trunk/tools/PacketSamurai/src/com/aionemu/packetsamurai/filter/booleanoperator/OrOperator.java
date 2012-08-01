package packetsamurai.filter.booleanoperator;

import java.util.List;


import packetsamurai.filter.Condition;
import packetsamurai.parser.DataStructure;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public class OrOperator implements BooleanOperator
{

    public boolean execute(List<Condition> conditions, DataStructure dp)
    {
        for(Condition cond : conditions)
        {
            if(cond.evaluate(dp))
                return true;
        }
        return false;
    }
    
}