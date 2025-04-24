package net.sf.colossus.game.actions;


import net.sf.colossus.game.Legion;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * An event modelling the addition of a creature to a legion.
 *
 * This is meant to be used only as interface or through subclasses.
 *
 * TODO make abstract once History handles the subclasses properly
 */
public class AddCreatureAction extends LegionAction implements RevealingAction
{
    private final @RUntainted CreatureType creatureType;

    public AddCreatureAction(@RUntainted Legion legion, @RUntainted CreatureType creatureType)
    {
        super(legion);
        this.creatureType = creatureType;
    }

    /**
     * The type of creature that was added.
     */
    public @RUntainted CreatureType getAddedCreatureType()
    {
        return creatureType;
    }

    public CreatureType[] getRevealedCreatures()
    {
        return new CreatureType[] { creatureType };
    }

    /**
     * Returns a string representing the reason for the addition.
     *
     * TODO remove in favour of using the event hierarchy
     * TODO should be abstract here, but History still creates instances of this class
     */
    public @RUntainted String getReason()
    {
        return "unknown";
    }

    @Override
    public String toString()
    {
        return String.format(
            "AddCreatureAction: add creature of type %s to legion %s",
            getAddedCreatureType(), getLegion());
    }
}
