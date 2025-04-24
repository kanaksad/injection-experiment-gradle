package net.sf.colossus.ai.objectives;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/** Abstract implementation of @TacticalObjective, handling the priority
 * stuff to avoid duplication.
 *
 * @author Romain Dolbeau
 */
public abstract class AbstractTacticalObjective implements TacticalObjective
{
    private @RUntainted float priority;

    public AbstractTacticalObjective(@RUntainted float priority)
    {
        this.priority = priority;
    }

    public @RUntainted float getPriority()
    {
        return priority;
    }

    public float changePriority(@RUntainted float newPriority)
    {
        float oldPriority = priority;
        priority = newPriority;
        return oldPriority;
    }
}
