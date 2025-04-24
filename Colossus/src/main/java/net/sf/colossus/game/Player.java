package net.sf.colossus.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.colossus.common.Constants;
import net.sf.colossus.server.PlayerServerSide;
import net.sf.colossus.server.VariantSupport;
import net.sf.colossus.variant.MasterHex;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * A player in a game.
 *
 * This class holds all information describing a player in a game, such
 * as the current legions and the score. Instances of this class are always bound to
 * an instance of {@link Game}.
 *
 * TODO there is an excessive amount of methods around the markersAvailable list.
 */
public class Player
{
    /**
     * The game this information belongs to.
     */
    private final Game game;

    /**
     * A name for this player for UI purposes and as identifier.
     */
    private @RUntainted String name;

    /**
     * The current legions owned by this player.
     */
    private final @RUntainted List<@RUntainted Legion> legions = new ArrayList<@RUntainted Legion>();

    /**
     * The number of the player in the game.
     *
     * TODO clarify if this is just an arbitrary number (in which case we might want
     * to get rid of it) or the actual turn sequence
     */
    private final int number;

    /**
     * Set to true if the player is dead.
     *
     * TODO check if that isn't equivalent to not having legions anymore
     */
    private boolean dead;

    /**
     *  Only needed during loading of a game. Pulled up to game anyway,
     *  getNumLivingLegions needs it during loading.
     */
    private boolean deadBeforeSave = false;

    /**
     * The starting tower of the player.
     *
     * TODO this should be kind-of final: once a tower has been assigned, it shouldn't
     *      change anymore -- but assigning the towers has probably to happen a while
     *      after all players are created. We could at least at an assertion into the
     *      setter that it is allowed to change the value only if it was not set before.
     */
    private @RUntainted MasterHex startingTower;

    /**
     * The label of the color we use.
     *
     * TODO this should really be an object representing a markerset
     * TODO similar to {@link #startingTower} this should be set only once but probably
     *      can't be set in the constructor.
     */
    private @RUntainted PlayerColor color;

    /**
     * The type of player: local human, AI or network.
     *
     * TODO make typesafe version
     * TODO shouldn't this be final? It should be possible to set that in the constructor.
     *      Unless we have to allow changes e.g. for humans dropping out of the game (in
     *      which case the todo should be read as "add some documentation regarding that ;-) ).
     */
    private @RUntainted String type;

    /**
     * A string representing all players eliminated by this player.
     *
     * The format is just a sequence of the short, two-character versions
     * of the colors, e.g. "BkRd".
     *
     * TODO this should really be a List<Player>
     */
    private @RUntainted String playersEliminated = "";

    private int mulligansLeft;

    private @RUntainted int score;

    /**
     * Sorted set of available legion markers for this player.
     */
    private final @RUntainted SortedSet<@RUntainted String> markersAvailable = new TreeSet<@RUntainted String>(
        new MarkerComparator(getShortColor()));

    public Player(Game game, @RUntainted String playerName, int number)
    {
        assert game != null : "No game without Game";
        assert playerName != null : "Player needs a name";
        assert number >= 0 : "Player number must not be negative";
        // TODO check for max on number once game has the players stored in it
        this.game = game;
        this.name = playerName;
        this.number = number;
        this.dead = false;
        this.mulligansLeft = 1;
    }

    public Game getGame()
    {
        return game;
    }

    /**
     * TODO should be List<Legion>, but currently subclasses still use more specific types
     * TODO should be unmodifiable, but at least {@link PlayerServerSide#die(Player)} still
     *      removes items
     */
    public @RUntainted List<? extends @RUntainted Legion> getLegions()
    {
        return this.legions;
    }

    public int getNumber()
    {
        return number;
    }

    public @RUntainted String getName()
    {
        return name;
    }

    public void setName(@RUntainted String name)
    {
        this.name = name;
    }

    public boolean isDead()
    {
        return dead;
    }

    // TODO it would probably be safer not to have this method and instead set the
    // state only in the constructor or during die()
    public void setDead(boolean dead)
    {
        this.dead = dead;
    }

    /** During loading of a game, this player was already dead in the game
     *  before saving. No client needs to be created for this player, and
     *  all legion activities/reveals are skipped.
     * @return True if player was dead
     */
    public boolean getDeadBeforeSave()
    {
        return this.deadBeforeSave;
    }

    public void setDeadBeforeSave(boolean val)
    {
        this.deadBeforeSave = val;
    }

    public void setType(@RUntainted String type)
    {
        this.type = type;
    }

    public @RUntainted String getType()
    {
        return type;
    }

    public boolean isHuman()
    {
        return isLocalHuman() || isNetwork();
    }

    public boolean isLocalHuman()
    {
        return getType().endsWith(Constants.human);
    }

    public boolean isNetwork()
    {
        return getType().endsWith(Constants.network);
    }

    public boolean isNone()
    {
        return getType().endsWith(Constants.none);
    }

    public @RUntainted boolean isAI()
    {
        return type.endsWith(Constants.ai);
    }

    public void setStartingTower(@RUntainted MasterHex startingTower)
    {
        this.startingTower = startingTower;
    }

    public @RUntainted MasterHex getStartingTower()
    {
        return startingTower;
    }

    public void setColor(@RUntainted PlayerColor color)
    {
        this.color = color;
    }

    public @RUntainted PlayerColor getColor()
    {
        return color;
    }

    public @RUntainted String getShortColor()
    {
        if (color == null)
        {
            return null;
        }
        else
        {
            return color.getShortName();
        }
    }

    public @RUntainted String getPlayersElim()
    {
        return playersEliminated;
    }

    public void setPlayersElim(@RUntainted String playersEliminated)
    {
        this.playersEliminated = playersEliminated;
    }

    public void addPlayerElim(Player player)
    {
        playersEliminated = playersEliminated + player.getShortColor();
    }

    public @RUntainted Legion getLegionByMarkerId(String markerId)
    {
        for (Legion legion : getLegions())
        {
            if (legion.getMarkerId().equals(markerId))
            {
                return legion;
            }
        }
        return null;
    }

    public boolean hasLegion(String markerId)
    {
        for (Legion legion : getLegions())
        {
            if (legion.getMarkerId().equals(markerId))
            {
                return true;
            }
        }
        return false;
    }

    public Legion getTitanLegion()
    {
        for (Legion legion : getLegions())
        {
            if (legion.hasTitan())
            {
                return legion;
            }
        }
        return null;
    }

    public void addLegion(@RUntainted Legion legion)
    {
        legions.add(legion);
    }

    public void removeLegion(Legion legion)
    {
        legions.remove(legion);
    }

    public void removeAllLegions()
    {
        legions.clear();
    }

    public void addMarkerAvailable(@RUntainted String markerId)
    {
        markersAvailable.add(markerId);
    }

    public void removeMarkerAvailable(String markerId)
    {
        markersAvailable.remove(markerId);
    }

    public void clearMarkersAvailable()
    {
        markersAvailable.clear();
    }

    public Set<String> getMarkersUsed()
    {
        SortedSet<String> used = new TreeSet<String>();
        for (Legion l : legions)
        {
            used.add(l.getMarkerId());
        }
        return Collections.unmodifiableSortedSet(used);
    }

    public @RUntainted Set<@RUntainted String> getMarkersAvailable()
    {
        return Collections.unmodifiableSortedSet(markersAvailable);
    }

    public int getNumMarkersAvailable()
    {
        return markersAvailable.size();
    }

    public String getFirstAvailableMarker()
    {
        synchronized (markersAvailable)
        {
            if (markersAvailable.isEmpty())
            {
                return null;
            }
            return markersAvailable.first();
        }
    }

    public boolean isMarkerAvailable(String markerId)
    {
        return markersAvailable.contains(markerId);
    }

    /** Removes the selected marker from the list of those available.
     *  Returns the markerId if it was present, or null if it was not. */
    public String selectMarkerId(String markerId)
    {
        if (markersAvailable.remove(markerId))
        {
            return markerId;
        }
        else
        {
            return null;
        }
    }

    public int getNumCreatures()
    {
        int count = 0;
        for (Legion legion : getLegions())
        {
            count += legion.getHeight();
        }
        return count;
    }

    public int getCreaturePoints()
    {
        int count = 0;
        for (Legion legion : getLegions())
        {
            count += legion.getHeight();
        }
        return count;
    }

    /**
     * Overridden for debug/logging purposes.
     */
    @Override
    public String toString()
    {
        return getName();
    }

    public void setMulligansLeft(int mulligansLeft)
    {
        this.mulligansLeft = mulligansLeft;
    }

    public int getMulligansLeft()
    {
        return mulligansLeft;
    }

    public void setScore(@RUntainted int score)
    {
        this.score = score;
    }

    public @RUntainted int getScore()
    {
        return score;
    }

    public @RUntainted int getTitanPower()
    {
        return 6 + getScore()
            / getGame().getVariant().getTitanImprovementValue();
    }

    public boolean canTitanTeleport()
    {
        return getScore() >= getGame().getVariant().getTitanTeleportValue();
    }

    /**
     * Return the total value of all of this player's creatures.
     */
    public int getTotalPointValue()
    {
        int total = 0;
        for (Legion legion : getLegions())
        {
            total += legion.getPointValue();
        }
        return total;
    }

    public boolean hasTeleported()
    {
        for (Legion info : getLegions())
        {
            if (info.hasTeleported())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the player has already moved.
     *
     * @return true iff at least one legion of the player has been moved
     */
    public boolean hasMoved()
    {
        for (Legion legion : getLegions())
        {
            if (legion.hasMoved())
            {
                return true;
            }
        }
        return false;
    }

    public int getNumLegions()
    {
        return getLegions().size();
    }

    /**
     * Return the full basename for the titan of this player.
     */
    public @RUntainted String getTitanBasename()
    {
        try
        {
            return "Titan-" + getTitanPower() + "-" + getColor().getName();
        }
        catch (Exception ex)
        {
            return Constants.titan;
        }
    }

    /**
     * Return the full basename for an angel of this player.
     */
    public @RUntainted String getAngelBasename()
    {
        try
        {
            int power = VariantSupport.getCurrentVariant()
                .getCreatureByName("Angel").getPower();
            return "Angel-" + power + "-" + getColor().getName();
        }
        catch (Exception ex)
        {
            return Constants.angel;
        }
    }

    /**
     * wasted luck per strike Number (sn), for probability based Battle Rolls.
     * Like, Centaur for sn 4 should make 1.5 hits, first time makes 1 hit,
     * saves 0.5 to next time.
     * Stored per player per strike-number base.
     */
    private final double[] accumulatedWastedLuck = new double[] { 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, 0.0 };

    public boolean applyAccumulatedWastedLuck(int sn, double wastedLuck,
        StringBuffer eawlString)
    {
        accumulatedWastedLuck[sn] += wastedLuck;
        eawlString.append(String.format("%5.2f",
            Double.valueOf(accumulatedWastedLuck[sn])));
        if (accumulatedWastedLuck[sn] >= (1 - 0.0000000000000001))
        {
            accumulatedWastedLuck[sn] -= 1;
            return true;
        }
        return false;
    }

    // PlayerServerSide overrides this to use a better random source
    public @RUntainted int makeBattleRoll()
    {
        return Dice.rollDie();
    }

    // PlayerServerSide overrides this to use a better random source
    public int makeMovementRoll()
    {
        return Dice.rollDie();
    }

    public HashSet<Legion> getPendingSplitLegions()
    {
        HashSet<Legion> legions = new HashSet<Legion>();
        for (Legion l : getLegions())
        {
            if (l.getSplitRequestSent())
            {
                legions.add(l);
            }
        }
        return legions;
    }

    public int countPendingSplits()
    {
        return getPendingSplitLegions().size();
    }

    public HashSet<Legion> getPendingUndoSplitLegions()
    {
        HashSet<Legion> legions = new HashSet<Legion>();
        for (Legion l : getLegions())
        {
            if (l.getUndoSplitRequestSent())
            {
                legions.add(l);
            }
        }
        return legions;
    }

    public int countPendingUndoSplits()
    {
        return getPendingUndoSplitLegions().size();
    }

}
