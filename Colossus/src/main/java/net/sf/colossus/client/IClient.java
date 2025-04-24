package net.sf.colossus.client;


import java.util.List;
import java.util.Set;

import net.sf.colossus.game.BattlePhase;
import net.sf.colossus.game.EntrySide;
import net.sf.colossus.game.Legion;
import net.sf.colossus.game.Player;
import net.sf.colossus.game.PlayerColor;
import net.sf.colossus.variant.BattleHex;
import net.sf.colossus.variant.CreatureType;
import net.sf.colossus.variant.MasterHex;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  IClient is a remote interface for the server-accessible parts of Client.
 *
 *  @author David Ripton
 */
public interface IClient
{
    public void tellEngagement(@RUntainted MasterHex hex, @RUntainted Legion attacker, @RUntainted Legion defender);

    public void tellEngagementResults(@RUntainted Legion winner, String method,
        int points, int turns);

    public void tellMovementRoll(@RUntainted int roll, @RUntainted String reason);

    public void tellWhatsHappening(String message);

    public void syncOption(@RUntainted String optname, String value);

    public void updatePlayerInfo(List<@RUntainted String> infoStrings);

    public void updateChangedPlayerValues(@RUntainted String valuesString, @RUntainted String reason);

    public void setColor(PlayerColor color);

    public void updateCreatureCount(CreatureType type, @RUntainted int count, @RUntainted int deadCount);

    public void disposeClient();

    public void removeLegion(Legion legion);

    public void setLegionStatus(Legion legion, boolean moved,
        boolean teleported, EntrySide entrySide, CreatureType lastRecruit);

    public void addCreature(Legion legion, @RUntainted CreatureType type, @RUntainted String reason);

    public void removeCreature(@RUntainted Legion legion, @RUntainted CreatureType type, String reason);

    public void revealCreatures(Legion legion, final List<@RUntainted CreatureType> names,
        String reason);

    public void revealEngagedCreatures(Legion legion,
        final List<CreatureType> names, boolean isAttacker, @RUntainted String reason);

    public void removeDeadBattleChits();

    public void placeNewChit(@RUntainted String imageName, @RUntainted boolean inverted, @RUntainted int tag,
        @RUntainted BattleHex hex);

    public void initBoard();

    public void tellReplay(@RUntainted boolean val, int maxTurn);

    public void tellRedo(@RUntainted boolean val);

    public void confirmWhenCaughtUp();

    public void serverConfirmsConnection();

    public void relayedPeerRequest(@RUntainted String requestingClientName);

    public void peerRequestReceivedBy(@RUntainted String respondingPlayerName, @RUntainted int queueLen);

    public void peerRequestProcessedBy(@RUntainted String respondingPlayerName);

    public void setPlayerName(@RUntainted String newPlayerName);

    public void createSummonAngel(@RUntainted Legion legion);

    public void askAcquireAngel(Legion legion, List<CreatureType> recruits);

    public void askChooseStrikePenalty(@RUntainted List<@RUntainted String> choices);

    public void tellGameOver(@RUntainted String message, @RUntainted boolean disposeFollows, @RUntainted boolean suspended);

    public void tellPlayerElim(@RUntainted Player player, @RUntainted Player slayer);

    public void askConcede(@RUntainted Legion ally, @RUntainted Legion enemy);

    public void askFlee(@RUntainted Legion ally, @RUntainted Legion enemy);

    public void askNegotiate(Legion attacker, Legion defender);

    public void tellProposal(@RUntainted String proposalString);

    public void tellSlowResults(int targetTag, int slowValue);

    // TODO the last parameter could probably be a list of Creatures
    public void tellStrikeResults(int strikerTag, int targetTag,
        int strikeNumber, @RUntainted List<@RUntainted String> rolls, @RUntainted int damage, boolean killed,
        boolean wasCarry, @RUntainted int carryDamageLeft,
        Set<@RUntainted String> carryTargetDescriptions);

    public void initBattle(MasterHex masterHex, @RUntainted int battleTurnNumber,
        @RUntainted Player battleActivePlayer, BattlePhase battlePhase, @RUntainted Legion attacker,
        @RUntainted Legion defender);

    public void cleanupBattle();

    public void nextEngagement();

    public void doReinforce(@RUntainted Legion legion);

    public void didRecruit(Legion legion, @RUntainted CreatureType recruitName,
        CreatureType recruiterName, int numRecruiters);

    public void undidRecruit(Legion legion, CreatureType recruitName);

    public void setupTurnState(@RUntainted Player activePlayer, @RUntainted int turnNumber);

    public void setupSplit(Player activePlayer, int turnNumber);

    public void setupMove();

    public void setupFight();

    public void setupMuster();

    public void kickPhase();

    public void setupBattleSummon(@RUntainted Player battleActivePlayer,
        @RUntainted int battleTurnNumber);

    public void setupBattleRecruit(@RUntainted Player battleActivePlayer,
        @RUntainted int battleTurnNumber);

    public void setupBattleMove(@RUntainted Player battleActivePlayer, @RUntainted int battleTurnNumber);

    public void setupBattleFight(BattlePhase battlePhase,
        @RUntainted Player battleActivePlayer);

    // TODO the extra hex parameter is probably not needed anymore
    public void tellLegionLocation(Legion legion, @RUntainted MasterHex hex);

    public void tellBattleMove(int tag, BattleHex startingHex,
        @RUntainted BattleHex endingHex, boolean undo);

    public void didMove(@RUntainted Legion legion, @RUntainted MasterHex startingHex, @RUntainted MasterHex hex,
        EntrySide entrySide, boolean teleport, @RUntainted CreatureType teleportingLord,
        boolean splitLegionHasForcedMove);

    public void undidMove(Legion legion, MasterHex formerHex,
        @RUntainted MasterHex currentHex, boolean splitLegionHasForcedMove);

    public void didSummon(Legion receivingLegion, Legion donorLegion,
        @RUntainted CreatureType summon);

    public void undidSplit(@RUntainted Legion splitoff, Legion survivor, @RUntainted int turn);

    // TODO splitoffs is not actually used anymore, but it is still part
    // of the network protocol
    public void didSplit(@RUntainted MasterHex hex, @RUntainted Legion parent, @RUntainted Legion child,
        @RUntainted int childHeight, List<CreatureType> splitoffs, @RUntainted int turn);

    public void askPickColor(List<PlayerColor> colorsLeft);

    public void askPickFirstMarker();

    public void log(@RUntainted String message);

    public void nak(@RUntainted String reason, @RUntainted String errmsg);

    public void setBoardActive(boolean val);

    public void pingRequest(long requestTime);

    public void messageFromServer(@RUntainted String message);

    public void appendToConnectionLog(String s);

    public void tellSyncCompleted(@RUntainted int syncRequestNumber);

    public void requestExtraRollApproval(String requestorName, @RUntainted int requestId);

    public void askSuspendConfirmation(@RUntainted String requestorName, @RUntainted int timeout);

    public boolean canHandleChangedValuesOnlyStyle();

    public String getClientName();

}
