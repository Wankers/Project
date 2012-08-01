package packetsamurai.parser.parttypes;


import packetsamurai.parser.PartType;
import packetsamurai.parser.datatree.DataTreeNodeContainer;
import packetsamurai.parser.datatree.StringValuePart;
import packetsamurai.parser.datatree.ValuePart;
import packetsamurai.parser.formattree.Part;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class StringPartType extends PartType
{
    public enum stringType
    {
        Ss,
        S,
        s
    }
    
    private stringType _type;
    
    public StringPartType(String name, stringType type)
    {
        super(name);
        _type = type;
    }

    @Override
    public ValuePart getValuePart(DataTreeNodeContainer parent, Part part)
    {
        return new StringValuePart(parent, part, _type);
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
        return 0;
    }

}
