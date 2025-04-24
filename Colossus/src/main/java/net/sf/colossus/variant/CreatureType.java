package net.sf.colossus.variant;


import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * A type of creature in a variant.
 *
 * This class models a generic creature type, i.e. all features that are common
 * through all creatures of a specific type.
 *
 * Default equality and sorting order is class (in case of subclasses) then name.
 */
public class CreatureType implements Comparable<CreatureType>
{
    private static final Logger LOGGER = Logger.getLogger(CreatureType.class
        .getName());

    /**
     * A comparator sorting creature types by name.
     */
    public static final Comparator<CreatureType> NAME_ORDER = new Comparator<CreatureType>()
    {
        public int compare(CreatureType type1, CreatureType type2)
        {
            return type1.getName().compareTo(type2.getName());
        }
    };

    private static boolean noBaseColor = false;

    public static void setNoBaseColor(boolean b)
    {
        noBaseColor = b;
    }

    private final @RUntainted String name;

    private final @RUntainted String pluralName;

    private final @RUntainted int power;

    private final @RUntainted int skill;

    private final boolean rangestrikes;

    private final boolean flies;

    private final boolean nativeSlope;

    private final boolean nativeRiver;

    private final boolean nativeDune;

    private final boolean waterDwelling;

    private final boolean magicMissile;

    private final boolean lord;

    private final boolean demilord;

    private @RUntainted int maxCount;

    private final @RUntainted int poison;

    private final int slows;

    private final @RUntainted String baseColor;

    private final Set<HazardTerrain> nativeTerrains = new HashSet<HazardTerrain>();

    private final boolean isSummonable;

    /* NOTE for variant builder:
     * The subclasses in variant must use the following signature for the
     * constructor:
     *     public CreatureXXX(String name, Integer power, Integer skill,
            Boolean rangestrikes, Boolean flies,
            HashSet<HazardTerrain> nativeTerrrains, Boolean nativeSlope,
            Boolean nativeRiver, Boolean nativeDune, Boolean waterDwelling,
            Boolean magicMissile,
            Boolean summonable, Boolean lord, Boolean demilord,
            Integer maxCount,
            String pluralName, String baseColor, int poison, int slows);
     * Otherwise the class loading system won't find it. Not that this is
     * really HashSet, not just Set.
     */

    public CreatureType(@RUntainted String name, @RUntainted int power, @RUntainted int skill,
        boolean rangestrikes, boolean flies,
        Set<HazardTerrain> nativeTerrains, boolean nativeSlope,
        boolean nativeRiver, boolean nativeDune, boolean waterDwelling,
        boolean magicMissile, boolean summonable, boolean lord,
        boolean demilord, @RUntainted int maxCount, @RUntainted String pluralName, @RUntainted String baseColor,
        @RUntainted int poison, int slows)
    {
        this.name = name;
        this.pluralName = pluralName;

        // defensive, but shallow copy of terrains
        this.nativeTerrains.addAll(nativeTerrains);

        this.isSummonable = summonable;
        this.power = power;
        this.skill = skill;
        this.rangestrikes = rangestrikes;
        this.flies = flies;
        this.nativeSlope = nativeSlope;
        this.nativeRiver = nativeRiver;
        this.nativeDune = nativeDune;
        this.waterDwelling = waterDwelling;
        this.magicMissile = magicMissile;
        this.lord = lord;
        this.demilord = demilord;
        this.maxCount = maxCount;
        this.baseColor = baseColor;
        this.poison = poison;
        this.slows = slows;

        /* warn about likely inappropriate combinations */
        if (waterDwelling && isNativeDune())
        {
            LOGGER.warning("Creature " + name
                + " is both a Water Dweller and native to Dune.");
        }
    }

    /**
     * The name used for creatures of this type.
     */
    public @RUntainted String getName()
    {
        return name;
    }

    /**
     * The name used for multiple creatures of this type.
     */
    public @RUntainted String getPluralName()
    {
        return pluralName;
    }

    /**
     * Checks if the type of creature is native in a terrain type.
     *
     * @param terrain The terrain to check. Not null.
     * @return true iff creatures of this type are native in the terrain.
     */
    public boolean isNativeIn(HazardTerrain terrain)
    {
        assert terrain != null;
        return nativeTerrains.contains(terrain);
    }

    public boolean isSummonable()
    {
        return isSummonable;
    }

    /**
     * Returns true if this is a Titan.
     *
     * The default implementation is a constant false, to be overridden in classes
     * representing Titans.
     *
     * @return true iff this creature type is a Titan.
     */
    public boolean isTitan()
    {
        return false;
    }

    @Override
    public @RUntainted String toString()
    {
        return getName();
    }

    /** Compare by name. */
    @Override
    public final boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }
        if (object.getClass() != this.getClass())
        {
            return false;
        }
        CreatureType other = (CreatureType)object;
        return getName().equals(other.getName());
    }

    public @RUntainted int getMaxCount()
    {
        return maxCount;
    }

    /** Only called on Titans after numPlayers is known.
     *  08/2009 Clemens: And on Balrogs when players scores raise.
     */
    public void setMaxCount(@RUntainted int maxCount)
    {
        this.maxCount = maxCount;
    }

    public boolean isLord()
    {
        return lord;
    }

    public boolean isDemiLord()
    {
        return demilord;
    }

    public boolean isLordOrDemiLord()
    {
        return (isLord() || isDemiLord());
    }

    public boolean isImmortal()
    { // might not the same for derived class
        return isLordOrDemiLord();
    }

    /** true if any if the values can change during the game returned by:
     * - getPower, getSkill, (and therefore getPointValue)
     * - isRangestriker, isFlier, useMagicMissile
     * - isNativeTerraion(t), for all t
     * - isNativeHexSide(h) for all h
     * In Standard game only the titans change their attributes
     */
    public boolean canChangeValue()
    {
        return isTitan();
    }

    protected @RUntainted String getImageName()
    {
        return getName();
    }

    public @RUntainted String[] getImageNames()
    {
        @RUntainted String[] tempNames;
        if (baseColor != null)
        {
            int specialIncrement = ((isFlier() || isRangestriker()) ? 1 : 0);
            tempNames = new String[4 + specialIncrement];
            String colorSuffix = "-" + (noBaseColor ? "black" : baseColor);
            tempNames[0] = getImageName();
            tempNames[1] = "Power-" + getPower() + colorSuffix;

            tempNames[2] = "Skill-" + getSkill() + colorSuffix;
            tempNames[3] = getName() + "-Name" + colorSuffix;
            if (specialIncrement > 0)
            {
                tempNames[4] = (isFlier() ? "Flying" : "")
                    + (isRangestriker() ? "Rangestrike" : "") + colorSuffix;
            }
        }
        else
        {
            tempNames = new String[1];
            tempNames[0] = getImageName();
        }
        return tempNames;
    }

    public @RUntainted int getPower()
    {
        return power;
    }

    public @RUntainted int getSkill()
    {
        return skill;
    }

    public @RUntainted int getPointValue()
    { // this function is replicated in Critter
        return getPower() * getSkill();
    }

    public boolean isRangestriker()
    {
        return rangestrikes;
    }

    public boolean isFlier()
    {
        return flies;
    }

    public boolean isPoison()
    {
        return poison > 0;
    }

    public boolean slows()
    {
        return slows > 0;
    }

    public boolean isNativeAt(HazardHexside hazard)
    {
        return isNativeAt(hazard.getCode());
    }

    // TODO get rid of this char based version.
    // Only used by ShowCreatureDetails nowadays.
    public boolean isNativeAt(char h)
    {
        switch (h)
        {
            default:
                return false;

            case ' ': /* undefined */
                return false;

            case 'd':
                return isNativeDune();

            case 'c': /* undefined */
                return false;

            case 's':
                return isNativeSlope();

            case 'w': /* undefined, beneficial for everyone */
                return true;

            case 'r':
                return isNativeRiver();
        }
    }

    public boolean isNativeSlope()
    {
        return nativeSlope;
    }

    public boolean isNativeRiver()
    {
        return nativeRiver;
    }

    public boolean isNativeDune()
    {
        return nativeDune;
    }

    public boolean isWaterDwelling()
    {
        return waterDwelling;
    }

    public boolean useMagicMissile()
    {
        return magicMissile;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    public String getBaseColor()
    {
        if (baseColor != null)
        {
            return baseColor;
        }
        else
        {
            return "";
        }
    }

    public @RUntainted int getPoison()
    {
        return poison;
    }

    public int getSlows()
    {
        return slows;
    }

    /**
     * Get the non-terrainified part of the kill-value.
     *
     * TODO this is not model, but AI related (but also used in client for
     * sorting creatures -- the client uses the AI for recruit hints, too)
     */
    public @RUntainted int getKillValue()
    {
        int val = 10 * getPointValue();
        final int lskill = getSkill();
        if (lskill >= 4)
        {
            val += 2;
        }
        else if (lskill <= 2)
        {
            val += 1;
        }
        if (isFlier())
        {
            val += 4;
        }
        if (isRangestriker())
        {
            val += 5;
        }
        if (useMagicMissile())
        {
            val += 4;
        }
        if (isTitan())
        {
            val += 1000;
        }
        if (isPoison())
        {
            val += 4;
        }
        if (slows())
        {
            val += 3;
        }
        return val;
    }

    public int compareTo(CreatureType o)
    {
        if (o.getClass() != this.getClass())
        {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
        return this.getName().compareTo(o.getName());
    }
}
