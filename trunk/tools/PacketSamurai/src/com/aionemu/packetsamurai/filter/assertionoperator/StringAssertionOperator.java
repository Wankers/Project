package packetsamurai.filter.assertionoperator;


import packetsamurai.filter.value.string.StringValue;
import packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface StringAssertionOperator extends AssertionOperator
{
    public boolean execute(StringValue value1, StringValue value2, DataStructure dp);
}