package net.sf.colossus.gui;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RPolyTainted;


/**
 * Class StrikeDie displays a die representing the threshold for
 * a successful hit during the Strike/strike-back phase.
 *
 * --Cloned from David Ripton's MovementDie.--
 *
 * @author Dranathi
 */
final class StrikeDie extends Chit
{
    private int lastRoll = 0;

    StrikeDie(@RUntainted int scale, int roll, @RUntainted String type)
    {
        this(scale, roll, type, null);
    }

    StrikeDie(@RUntainted int scale, int roll, @RUntainted String type, @RUntainted String[] overlays)
    {
        super(scale, getDieImageName(type, roll), overlays);
        lastRoll = roll;
        setOpaque(false);
    }

    static @RPolyTainted String getDieImageName(@RPolyTainted String type, int roll)
    {
        StringBuilder basename = new StringBuilder(type);
        basename.append(roll);
        return basename.toString();
    }

    int getLastRoll()
    {
        return lastRoll;
    }
}
