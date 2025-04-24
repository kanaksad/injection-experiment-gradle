package net.sf.colossus.gui;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * Class MovementDie displays dice rolls during a battle.
 *
 * @author David Ripton
 */
final class MovementDie extends Chit
{
    private final int lastRoll = 0;

    MovementDie(@RUntainted int scale, @RUntainted String id)
    {
        // null: no overlays
        super(scale, id, null);
    }

    static @RUntainted String getDieImageName(int roll)
    {
        StringBuilder basename = new StringBuilder("Hit");
        basename.append(roll);

        return basename.toString();
    }

    int getDisplayedRoll()
    {
        return lastRoll;
    }
}
