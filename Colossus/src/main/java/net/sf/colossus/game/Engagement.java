package net.sf.colossus.game;


import java.util.logging.Logger;

import net.sf.colossus.variant.MasterHex;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  Holds the basic data for an engagement.
 *
 *  TODO: use also on server side.
 *
 *  TODO: unify with EngagementResults.Engagement
 */
public class Engagement
{
    private static final Logger LOGGER = Logger.getLogger(Engagement.class
        .getName());

    /**
     *  If engagement is ongoing, the masterBoard hex, attacker and defender
     */
    private final @RUntainted Legion attacker;
    private final @RUntainted Legion defender;
    private final @RUntainted MasterHex location;

    public Engagement(@RUntainted MasterHex hex, @RUntainted Legion attacker, @RUntainted Legion defender)
    {
        this.location = hex;
        this.attacker = attacker;
        this.defender = defender;
        LOGGER.info("A new engagement: " + location + " attacker " + attacker
            + " defender " + defender);
    }

    public @RUntainted MasterHex getLocation()
    {
        return location;
    }

    public String getLocationLabel()
    {
        return location.getLabel();
    }

    public @RUntainted Legion getDefendingLegion()
    {
        return defender;
    }

    public @RUntainted Legion getAttackingLegion()
    {
        return attacker;
    }

    @Override
    public @RUntainted String toString()
    {
        return location + " attacker " + attacker + " defender " + defender;
    }

}
