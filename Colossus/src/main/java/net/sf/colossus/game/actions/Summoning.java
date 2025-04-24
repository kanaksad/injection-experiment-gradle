package net.sf.colossus.game.actions;


import net.sf.colossus.common.Constants;
import net.sf.colossus.game.Legion;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


public class Summoning extends AddCreatureAction
{
    private final @RUntainted Legion donor;

    public Summoning(@RUntainted Legion targetLegion, @RUntainted Legion donor,
        @RUntainted CreatureType summonedCreature)
    {
        super(targetLegion, summonedCreature);
        this.donor = donor;
    }

    @Override
    public @RUntainted String getReason()
    {
        return Constants.reasonSummon;
    }

    public @RUntainted Legion getDonor()
    {
        return donor;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Summoning of creature of type %s from legion %s into legion %s",
            getAddedCreatureType(), getLegion(), getDonor());
    }
}
