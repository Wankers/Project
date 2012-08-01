package packetsamurai.parser.parttypes;

import packetsamurai.parser.datatree.DataTreeNodeContainer;
import packetsamurai.parser.datatree.IntBCValuePart;
import packetsamurai.parser.datatree.ValuePart;
import packetsamurai.parser.formattree.Part;

/**
 * @author -Nemesiss-
 *
 */
public class IntBSPartType extends IntPartType
{
	public IntBSPartType(String name, intType type)
	{
		super(name, type);
	}

    @Override
    public ValuePart getValuePart(DataTreeNodeContainer parent, Part part)
    {
        return new IntBCValuePart(parent, part, _type);
    }

}
