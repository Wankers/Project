package packetsamurai.filter.value.string;


import packetsamurai.filter.value.Value;
import packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class StringValue extends Value
{
    public abstract String getStringValue(DataStructure dp);
}