package packetsamurai.filter.value.number;

import packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class FloatNumberValue extends NumberValue
{
    public abstract double getFloatValue(DataStructure dp);
}