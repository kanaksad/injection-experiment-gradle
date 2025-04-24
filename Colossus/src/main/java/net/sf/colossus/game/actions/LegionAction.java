package net.sf.colossus.game.actions;


import net.sf.colossus.game.Legion;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * A base class for all actions affecting a single legion in the game.
 *
 * This exists only for implementation purposes and is not intended to
 * be instantiated directly.
 */
public abstract class LegionAction implements GameAction
{
    protected final @RUntainted Legion legion;

    public LegionAction(@RUntainted Legion legion)
    {
        this.legion = legion;
    }

    /**
     * The legion that was changed.
     */
    public @RUntainted Legion getLegion()
    {
        return legion;
    }
}
