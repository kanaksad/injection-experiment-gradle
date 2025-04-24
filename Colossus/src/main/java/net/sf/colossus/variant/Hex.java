package net.sf.colossus.variant;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * Class Hex describes one general hex.
 *
 * @author David Ripton
 */
public abstract class Hex
{
    // The hex vertexes are numbered like this:
    //
    //              0---------1
    //             /           \
    //            /             \
    //           /               \
    //          /                 \
    //         5                   2
    //          \                 /
    //           \               /
    //            \             /
    //             \           /
    //              4---------3

    // Game state variables
    final private @RUntainted String label;
    private final @RUntainted int xCoord;
    private final @RUntainted int yCoord;

    public Hex(@RUntainted String label, @RUntainted int xCoord, @RUntainted int yCoord)
    {
        this.label = label;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public @RUntainted String getLabel()
    {
        return label;
    }

    public abstract @RUntainted String getTerrainName();

    public @RUntainted String getDescription()
    {
        return getTerrainName() + " hex " + getLabel();
    }

    @Override
    public String toString()
    {
        return getDescription();
    }

    public @RUntainted int getXCoord()
    {
        return xCoord;
    }

    public @RUntainted int getYCoord()
    {
        return yCoord;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + xCoord;
        result = prime * result + yCoord;
        return result;
    }

    /**
     * We consider two hexes equal if their x/y coordinates are the same.
     *
     * This gives equality within the context of a HexMap, since we don't know to
     * which map the Hex belongs we can't do any better.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hex other = (Hex)obj;
        if (xCoord != other.xCoord)
            return false;
        if (yCoord != other.yCoord)
            return false;
        return true;
    }
}
