package packetsamurai.parser.parttypes;


import packetsamurai.parser.PartType;
import packetsamurai.parser.datatree.DataTreeNodeContainer;
import packetsamurai.parser.datatree.LongValuePart;
import packetsamurai.parser.datatree.ValuePart;
import packetsamurai.parser.formattree.Part;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class LongPartType extends PartType
{
    public LongPartType(String name)
    {
        super(name);
    }

    @Override
    public ValuePart getValuePart(DataTreeNodeContainer parent, Part part)
    {
        return new LongValuePart(parent, part);
    }

    @Override
    public boolean isBlockType()
    {
        return false;
    }

    @Override
    public boolean isReadableType()
    {
        return true;
    }

    @Override
    public int getTypeByteNumber()
    {
        return 8;
    }

}
