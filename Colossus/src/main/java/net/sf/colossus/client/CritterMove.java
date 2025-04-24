package net.sf.colossus.client;


import net.sf.colossus.game.BattleCritter;
import net.sf.colossus.variant.BattleHex;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * One battle move for one critter.
 *
 * @author David Ripton
 */

public class CritterMove
{
    private int value;
    private final BattleCritter critter;
    private final @RUntainted BattleHex startingHex;
    private final @RUntainted BattleHex endingHex;

    public CritterMove(BattleCritter critter, @RUntainted BattleHex startingHex,
        @RUntainted BattleHex endingHex)
    {
        super();
        this.critter = critter;
        this.startingHex = startingHex;
        this.endingHex = endingHex;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public BattleCritter getCritter()
    {
        return critter;
    }

    int getTag()
    {
        return critter.getTag();
    }

    public @RUntainted BattleHex getStartingHex()
    {
        return startingHex;
    }

    public @RUntainted BattleHex getEndingHex()
    {
        return endingHex;
    }

    @Override
    public String toString()
    {
        return critter.getDescription() + " to " + getEndingHex().getLabel();
    }
}
