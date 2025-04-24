package net.sf.colossus.ai;


import java.util.List;
import java.util.Set;

import net.sf.colossus.client.CritterMove;
import net.sf.colossus.client.LegionClientSide;
import net.sf.colossus.game.Caretaker;
import net.sf.colossus.game.EntrySide;
import net.sf.colossus.game.Legion;
import net.sf.colossus.game.PlayerColor;
import net.sf.colossus.game.SummonInfo;
import net.sf.colossus.variant.CreatureType;
import net.sf.colossus.variant.MasterHex;
import net.sf.colossus.variant.Variant;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * interface to allow for multiple AI implementations
 *
 * @author Bruce Sherrod
 * @author David Ripton
 */
public interface AI
{

    public void setVariant(Variant variant);

    /** make masterboard moves for current player in the Game */
    boolean masterMove();

    /** make splits for current player.  Return true if done */
    boolean split();

    /** continue making splits.  Return true if done. */
    boolean splitCallback(@RUntainted Legion parent, @RUntainted Legion child);

    /** make recruits for current player */
    void muster();

    /** pick one reinforcement for legion */
    void reinforce(@RUntainted Legion legion);

    /** choose whether legion should flee from enemy */
    @RUntainted boolean flee(@RUntainted Legion legion, @RUntainted Legion enemy);

    /** choose whether legion should concede to enemy */
    boolean concede(@RUntainted Legion legion, @RUntainted Legion enemy);

    /** make battle strikes for legion */
    boolean strike(@RUntainted Legion legion);

    /** a Battle start */
    void initBattle();

    /** return a list of battle moves for the active legion */
    List<CritterMove> battleMove();

    /** a Battle is finished */
    void cleanupBattle();

    /** Try another move for creatures whose moves failed. */
    void retryFailedBattleMoves(List<CritterMove> bestMoveOrder);

    /** pick an entry side */
    EntrySide pickEntrySide(MasterHex hex, Legion legion,
        Set<EntrySide> entrySides);

    /** pick an engagement to resolve */
    @RUntainted MasterHex pickEngagement();

    /** choose whether to acquire an angel or archangel */
    CreatureType acquireAngel(Legion legion, List<CreatureType> recruits);

    /** choose whether to summon an angel or archangel */
    SummonInfo summonAngel(@RUntainted Legion summoner, List<@RUntainted Legion> possibleDonors);

    /** pick a color of legion markers */
    PlayerColor pickColor(List<PlayerColor> colors,
        List<PlayerColor> favoriteColors);

    /** pick a legion marker */
    @RUntainted String pickMarker(@RUntainted Set<@RUntainted String> markerIds, String preferredShortColor);

    /** choose carry target */
    void handleCarries(@RUntainted int carryDamage, Set<@RUntainted String> carryTargets);

    /** pick an optional strike penalty */
    @RUntainted String pickStrikePenalty(@RUntainted List<@RUntainted String> choices);

    @RUntainted CreatureType getVariantRecruitHint(LegionClientSide legion, MasterHex hex,
        List<@RUntainted CreatureType> recruits);

    Caretaker getCaretaker();
}
