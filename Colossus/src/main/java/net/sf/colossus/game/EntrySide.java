package net.sf.colossus.game;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * The entry side for a battle.
 */
public enum EntrySide // NO_UCD
{
    TOP_DEFENSE("Top defense"), RIGHT("Right"), RIGHT_DEFENSE("Right defense"), BOTTOM(
        "Bottom"), LEFT_DEFENSE("Left defense"), LEFT("Left"), NOT_SET(
        "Not set");

    private final @RUntainted String label;

    private EntrySide(@RUntainted String label)
    {
        this.label = label;
    }

    public @RUntainted String getLabel()
    {
        return label;
    }

    public boolean isAttackerSide()
    {
        return ordinal() % 2 == 1;
    }

    public EntrySide getOpposingSide()
    {
        return values()[(ordinal() + 3) % 6];
    }

    public static @RUntainted EntrySide fromLabel(String label)
    {
        for (EntrySide entrySide : values())
        {
            if (entrySide.getLabel().equals(label))
            {
                return entrySide;
            }
        }
        throw new IllegalArgumentException(
            "No attacker entry side with label '" + label + "'");
    }
}
