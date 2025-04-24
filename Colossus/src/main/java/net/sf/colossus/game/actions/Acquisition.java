package net.sf.colossus.game.actions;


import net.sf.colossus.common.Constants;
import net.sf.colossus.game.Legion;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


public class Acquisition extends AddCreatureAction
{
    public Acquisition(@RUntainted Legion legion, @RUntainted CreatureType creatureType)
    {
        super(legion, creatureType);
    }

    @Override
    public @RUntainted String getReason()
    {
        return Constants.reasonAcquire;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Acquisition of creature of type %s in legion %s",
            getAddedCreatureType(), getLegion());
    }
}
