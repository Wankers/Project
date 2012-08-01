package packetsamurai.filter.booleanoperator;

import java.util.List;


import packetsamurai.filter.Condition;
import packetsamurai.parser.DataStructure;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface BooleanOperator
{
    public boolean execute(List<Condition> conditions, DataStructure dp);
}