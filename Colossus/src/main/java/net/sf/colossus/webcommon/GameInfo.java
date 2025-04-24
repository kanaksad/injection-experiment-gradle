package net.sf.colossus.webcommon;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.colossus.common.Constants;
import net.sf.colossus.common.Options;
import net.sf.colossus.util.Glob;
import net.sf.colossus.util.Split;
import net.sf.colossus.webclient.WebClient;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  One object of this this class represents a game for which players/users
 *  have enrolled to play it together.
 *  It starts in state "PROPOSED" as type either instantly or scheduled.
 *  Then its state will change along the sequence of states
 *  PROPOSED, DUE, ACTIVATED, STARTING, READY_TO_CONNECT, RUNNING, ENDING
 *  as denoted in the GameState enum.
 *
 *  The actual running/starting of the game will be handled by different
 *  classes, namely GameOnServer and (to be done) GameOnClient.
 *
 *  The same class is also used at client side, but only part of the data
 *  is used there (e.g. the user has only a name, not a socket).
 *
 *  @author Clemens Katzer
 */
public class GameInfo
{
    private static final Logger LOGGER = Logger.getLogger(GameInfo.class
        .getName());

    private static @RUntainted int nextFreeGameId = 1;

    private @RUntainted String gameId;

    private @RUntainted GameType type;
    private @RUntainted GameState state;
    /** temporary backup during startingAttempt */
    private @RUntainted GameState oldState;
    private User startingUser = null;

    private @RUntainted int portNr = -1;
    private @RUntainted String hostingHost = "";
    private @RUntainted IGameRunner gameRunner;

    private @RUntainted String initiator;
    private @RUntainted String variant;
    private @RUntainted String viewmode;
    private final boolean autosave = true;

    // those 3 should still be added at client side:
    private @RUntainted String eventExpiring;
    private @RUntainted boolean unlimitedMulligans;
    private @RUntainted boolean balancedTowers;
    private boolean autoSansLordBattles;
    private boolean inactivityTimeout;
    private boolean probabilityBasedBattleHits;

    private boolean noFirstTurnT2TTeleportOpt;
    private boolean noFirstTurnTeleportOpt;
    private boolean towerToTowerTeleportOnlyOpt;
    private boolean noTowerTeleportOpt;
    private boolean noTitanTeleportOpt;
    private boolean noFirstTurnWarlockRecruitOpt;

    private @RUntainted int min;
    private @RUntainted int target;
    private @RUntainted int max;
    private @RUntainted int onlineCount;

    private String resumeFromFilename;

    // next three only needed for scheduled games
    private @RUntainted long startTime = 0;
    private @RUntainted int duration = 0;
    private @RUntainted String summary = "";
    // private String infoText = "";

    private @RUntainted int enrolledPlayers;

    private @RUntainted ArrayList<User> players = null;

    // used on server side, to create a game proposed by client

    private GameInfo(@RUntainted GameType type)
    {
        this.gameId = String.valueOf(getNextFreeGameId());
        this.type = type;

        this.state = GameState.PROPOSED;

        this.enrolledPlayers = 0;
        this.players = new ArrayList<User>();
    }

    /** Server calls this to set it high enough that existing directories
     *  in games work directory are not overwritten
     *
     *  @param id Next games should have higher number than given id
     */
    public static void setNextFreeGameId(@RUntainted int id)
    {
        nextFreeGameId = id;
    }

    private static @RUntainted int getNextFreeGameId()
    {
        return nextFreeGameId++;
    }

    public static boolean wouldBeInstantGame(long startTime)
    {
        return makeTypeFromStarttime(startTime).equals(GameType.INSTANT);
    }

    private static @RUntainted GameType makeTypeFromStarttime(long startTime)
    {
        return (startTime == -1 ? GameType.INSTANT : GameType.SCHEDULED);
    }

    public GameInfo(@RUntainted String initiator, @RUntainted String variant, @RUntainted String viewmode,
        @RUntainted long startTime, @RUntainted int duration, @RUntainted String summary, @RUntainted String expire,
        @RUntainted List<@RUntainted String> gameOptions, @RUntainted List<@RUntainted String> teleportOptions, @RUntainted int min,
        @RUntainted int target, @RUntainted int max)
    {
        this(makeTypeFromStarttime(startTime));

        parseExtraOptions(gameOptions);
        parseExtraOptions(teleportOptions);

        this.initiator = initiator;
        this.variant = variant;
        this.viewmode = viewmode;
        this.eventExpiring = expire;
        this.min = min;
        this.target = target;
        this.max = max;
        this.onlineCount = 0;

        this.startTime = startTime;
        this.duration = duration;
        this.summary = summary;

        this.enrolledPlayers = 0;
        this.players = new ArrayList<User>();
        this.resumeFromFilename = null;

        LOGGER.log(Level.FINEST,
            "A new potential game was created!! - variant " + variant
                + " viewmode " + viewmode);
    }

    /*
     * go through list containing the extraOptions, set the corresponding
     * boolean values to true
     */
    private void parseExtraOptions(@RUntainted List<@RUntainted String> extraOptions)
    {
        if (extraOptions.size() == 0)
        {
            LOGGER.finest("Extra options list is totally empty! '");
            return;
        }
        if (extraOptions.size() == 1 && extraOptions.get(0).equals(""))
        {
            LOGGER.finest("Extra options is one empty element!");
            return;
        }

        LOGGER.finest("Extra options size: " + extraOptions.size());

        for (Iterator<@RUntainted String> iterator = extraOptions.iterator(); iterator
            .hasNext();)
        {
            String string = iterator.next();
            LOGGER.finest("Handling option string '" + string + "'");
            if (string.equals(Options.sansLordAutoBattle))
            {
                this.autoSansLordBattles = true;
            }
            else if (string.equals(Options.inactivityTimeout))
            {
                this.inactivityTimeout = true;
            }
            else if (string.equals(Options.unlimitedMulligans))
            {
                this.unlimitedMulligans = true;
            }
            else if (string.equals(Options.balancedTowers))
            {
                this.balancedTowers = true;
            }
            else if (string.equals(Options.pbBattleHits))
            {
                this.probabilityBasedBattleHits = true;
            }
            else if (string.equals(Options.noFirstTurnT2TTeleport))
            {
                this.noFirstTurnT2TTeleportOpt = true;
            }
            else if (string.equals(Options.noFirstTurnTeleport))
            {
                this.noFirstTurnTeleportOpt = true;
            }
            else if (string.equals(Options.towerToTowerTeleportOnly))
            {
                this.towerToTowerTeleportOnlyOpt = true;
            }
            else if (string.equals(Options.noTowerTeleport))
            {
                this.noTowerTeleportOpt = true;
            }
            else if (string.equals(Options.noTitanTeleport))
            {
                this.noTitanTeleportOpt = true;
            }
            else if (string.equals(Options.noFirstTurnWarlockRecruit))
            {
                this.noFirstTurnWarlockRecruitOpt = true;
            }
            else
            {
                LOGGER.severe("Unexpected option string '" + string
                    + "' when parsing extraOptions !");
            }
        }
    }

    // ================= now the stuff for the client side ===============

    // used on client side, to restore a proposed game sent by server
    public GameInfo(@RUntainted String gameId, boolean onServer)
    {
        this.gameId = gameId;
        if (onServer)
        {
            int intGameId = Integer.parseInt(gameId);
            if (nextFreeGameId <= intGameId)
            {
                nextFreeGameId = intGameId + 1;
            }
        }
        this.players = new ArrayList<User>();
    }

    public static @RUntainted GameInfo fromString(@RUntainted String[] tokens,
        @RUntainted HashMap<String, @RUntainted GameInfo> games, @RUntainted boolean fromFile)
    {
        GameInfo gi;

        // tokens[0] is the command
        String gameId = tokens[1];
        String key = gameId;

        if (games.containsKey(gameId))
        {
            // use the object webclient has created earlier
            LOGGER.finest("Found already, updating");
            gi = games.get(key);
        }
        else
        {
            gi = new GameInfo(gameId, fromFile);
            LOGGER.finest("Creating a new GameInfo");
            games.put(key, gi);
        }

        int j = 2;
        gi.type = GameType.valueOf(tokens[j++]);
        gi.state = GameState.valueOf(tokens[j++]);
        gi.initiator = tokens[j++];
        gi.variant = tokens[j++];
        gi.viewmode = tokens[j++];
        gi.startTime = Long.parseLong(tokens[j++]);
        gi.duration = Integer.parseInt(tokens[j++]);
        gi.summary = tokens[j++];
        gi.eventExpiring = tokens[j++];
        String token8 = tokens[j++];
        String token9 = tokens[j++];

        if (token8.equalsIgnoreCase("true")
            || token8.equalsIgnoreCase("false"))
        {
            gi.unlimitedMulligans = Boolean.valueOf(token8).booleanValue();
            gi.balancedTowers = Boolean.valueOf(token9).booleanValue();
        }
        else
        {
            if (token8.length() > 0)
            {
                List<@RUntainted String> extraOptions = Split.split(Glob.sep, token8);
                LOGGER.finest("Game extra options string is '" + token8 + "'");
                gi.parseExtraOptions(extraOptions);
            }
            else
            {
                LOGGER.finest("Empty 'game' extra-options string - ok!");
            }
            if (token9.length() > 0)
            {
                LOGGER.finest("Teleport extra options string is '" + token9
                    + "'");
                List<@RUntainted String> teleportOptions = Split.split(Glob.sep, token9);
                gi.parseExtraOptions(teleportOptions);
            }
            else
            {
                LOGGER.finest("Empty 'teleport' extra-options string - ok!");
            }
        }

        gi.min = Integer.parseInt(tokens[j++]);
        gi.target = Integer.parseInt(tokens[j++]);
        gi.max = Integer.parseInt(tokens[j++]);
        gi.onlineCount = Integer.parseInt(tokens[j++]);

        int lastIndex = j;
        gi.enrolledPlayers = Integer.parseInt(tokens[lastIndex]);

        ArrayList<User> players = new ArrayList<User>();
        int i = 1;
        while (i <= gi.enrolledPlayers)
        {
            String name = tokens[lastIndex + i];
            User user = new User(name);
            players.add(user);
            i++;
        }

        gi.players = players;
        return gi;
    }

    public @RUntainted String toStringCheckClientVersion(@RUntainted String username,
        int clientVersion, @RUntainted String sep)
    {
        String giString;
        if (clientVersion >= WebClient.WC_VERSION_DELETE_SUSPENDED_GAME)
        {
            LOGGER
                .info("Sending GameInfo (can handle deleted game) to client "
                    + username);
            giString = toString(sep);
        }
        else if (clientVersion >= WebClient.WC_VERSION_SUPPORTS_EXTRA_OPTIONS)
        {
            LOGGER.info("Sending GameInfo (new style) to client " + username);
            boolean noSuspend = clientVersion <= WebClient.WC_VERSION_RESUME;
            boolean noDelete = clientVersion < WebClient.WC_VERSION_DELETE_SUSPENDED_GAME;
            giString = toStringFixState(sep, noSuspend, noDelete);
        }
        else
        {
            LOGGER.info("Sending LegacyGameInfo to client " + username);
            giString = toStringLegacy(sep);
        }
        return giString;
    }

    public @RUntainted String toStringLegacy(@RUntainted String sep)
    {
        StringBuilder playerList = new StringBuilder();
        Iterator<User> it = players.iterator();
        while (it.hasNext())
        {
            playerList.append(sep);
            User user = it.next();
            playerList.append(user.getName());
        }

        String summary2 = summary;
        int count = 0;
        count = (this.autoSansLordBattles ? 1 : 0)
            + (this.inactivityTimeout ? 1 : 0)
            + (this.probabilityBasedBattleHits ? 1 : 0);

        if (count > 0)
        {
            summary2 = "NOTE! " + count + " extra options are set! | "
                + summary;
        }

        GameState modifiedState = state;
        if (modifiedState.equals(GameState.DELETED)
            || modifiedState.equals(GameState.SUSPENDED))
        {
            modifiedState = GameState.ENDING;
        }

        String message = gameId + sep + type.toString() + sep
            + modifiedState.toString() + sep + initiator + sep + variant + sep
            + viewmode + sep + startTime + sep + duration + sep + summary2
            + sep + eventExpiring + sep + unlimitedMulligans + sep
            + balancedTowers + sep + min + sep + target + sep + max + sep
            + onlineCount + sep + enrolledPlayers + playerList.toString();

        return message;
    }

    public @RUntainted String toString(@RUntainted String sep)
    {
        StringBuilder playerList = new StringBuilder();
        Iterator<User> it = players.iterator();
        while (it.hasNext())
        {
            playerList.append(sep);
            User user = it.next();
            playerList.append(user.getName());
        }

        String message = gameId + sep + type.toString() + sep
            + state.toString() + sep + initiator + sep + variant + sep
            + viewmode + sep + startTime + sep + duration + sep + summary
            + sep + eventExpiring + sep + getExtraOptionsAsString() + sep
            + getTeleportOptionsAsString() + sep + min + sep + target + sep
            + max + sep
            + onlineCount + sep + enrolledPlayers + playerList.toString();

        return message;
    }

    /**
     * If webclients that cannot handle it receive a DELETED or
     * SUSPENDED state, they throw exception and disconnect :-(
     * So we give them ENDING instead...
     * @param sep
     * @param noSuspend TODO
     * @param noDelete TODO
     * @return
     */
    public @RUntainted String toStringFixState(@RUntainted String sep, boolean noSuspend,
        boolean noDelete)
    {
        StringBuilder playerList = new StringBuilder();
        Iterator<User> it = players.iterator();
        while (it.hasNext())
        {
            playerList.append(sep);
            User user = it.next();
            playerList.append(user.getName());
        }

        GameState modifiedState = state;
        if (noDelete && state.equals(GameState.DELETED))
        {
            modifiedState = GameState.ENDING;
        }
        if (noSuspend && state.equals(GameState.SUSPENDED))
        {
            modifiedState = GameState.ENDING;
        }
        String message = gameId + sep + type.toString() + sep
            + modifiedState.toString() + sep + initiator + sep + variant + sep
            + viewmode + sep + startTime + sep + duration + sep + summary
            + sep + eventExpiring + sep + getExtraOptionsAsString() + sep
            + getTeleportOptionsAsString() + sep + min + sep + target + sep
            + max + sep + onlineCount + sep + enrolledPlayers
            + playerList.toString();

        return message;
    }

    public @RUntainted String getExtraOptionsAsString()
    {
        return Glob.glob(getExtraOptions());
    }

    public @RUntainted String getTeleportOptionsAsString()
    {
        return Glob.glob(getTeleportOptions());
    }

    public List<String> getExtraOptions()
    {
        List<String> extraOptions = new ArrayList<String>();

        if (this.autoSansLordBattles)
        {
            extraOptions.add(Options.sansLordAutoBattle);
        }

        if (this.inactivityTimeout)
        {
            extraOptions.add(Options.inactivityTimeout);
        }

        if (this.unlimitedMulligans)
        {
            extraOptions.add(Options.unlimitedMulligans);
        }

        if (this.balancedTowers)
        {
            extraOptions.add(Options.balancedTowers);
        }

        if (this.probabilityBasedBattleHits)
        {
            extraOptions.add(Options.pbBattleHits);
        }

        return extraOptions;
    }

    public List<String> getTeleportOptions()
    {
        List<String> teleportOptions = new ArrayList<String>();
        if (this.noFirstTurnT2TTeleportOpt)
        {
            teleportOptions.add(Options.noFirstTurnT2TTeleport);
        }
        if (this.noFirstTurnTeleportOpt)
        {
            teleportOptions.add(Options.noFirstTurnTeleport);
        }
        if (this.towerToTowerTeleportOnlyOpt)
        {
            teleportOptions.add(Options.towerToTowerTeleportOnly);
        }
        if (this.noTowerTeleportOpt)
        {
            teleportOptions.add(Options.noTowerTeleport);
        }
        if (this.noTitanTeleportOpt)
        {
            teleportOptions.add(Options.noTitanTeleport);
        }
        if (this.noFirstTurnWarlockRecruitOpt)
        {
            teleportOptions.add(Options.noFirstTurnWarlockRecruit);
        }
        return teleportOptions;
    }

    public void setState(@RUntainted GameState state)
    {
        this.state = state;
    }

    public @RUntainted GameState getGameState()
    {
        return this.state;
    }

    public boolean isScheduledGame()
    {
        boolean isSch = (this.type == GameType.SCHEDULED);
        return isSch;
    }

    public @RUntainted String getStateString()
    {
        return this.state.toString();
    }

    public @RUntainted String getGameId()
    {
        return this.gameId;
    }

    public void setGameId(@RUntainted String val)
    {
        this.gameId = val;
    }

    public void setGameRunner(@RUntainted IGameRunner gr)
    {
        this.gameRunner = gr;
    }

    public @RUntainted IGameRunner getGameRunner()
    {
        return gameRunner;
    }

    public @RUntainted int getPort()
    {
        return this.portNr;
    }

    public void setPort(@RUntainted int nr)
    {
        this.portNr = nr;
    }

    public void setHostingHost(@RUntainted String host)
    {
        hostingHost = host;
    }

    public @RUntainted String getHostingHost()
    {
        return hostingHost;
    }

    public String getInitiator()
    {
        return initiator;
    }

    public void setInitiator(@RUntainted String val)
    {
        initiator = val;
    }

    public Long getStartTime()
    {
        return Long.valueOf(startTime);
    }

    public void setStartTime(@RUntainted String val)
    {
        startTime = Long.parseLong(val);
    }

    public Integer getDuration()
    {
        return Integer.valueOf(duration);
    }

    public void setDuration(@RUntainted String val)
    {
        duration = Integer.parseInt(val);
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(@RUntainted String val)
    {
        summary = val;
    }

    public @RUntainted String getVariant()
    {
        return variant;
    }

    public void setVariant(@RUntainted String val)
    {
        variant = val;
    }

    public @RUntainted String getViewmode()
    {
        return viewmode;
    }

    public boolean getAutosave()
    {
        return autosave;
    }

    public void setViewmode(@RUntainted String val)
    {
        viewmode = val;
    }

    public String getEventExpiring()
    {
        return eventExpiring;
    }

    public void setEventExpiring(@RUntainted String val)
    {
        eventExpiring = val;
    }

    public boolean getUnlimitedMulligans()
    {
        return unlimitedMulligans;
    }

    public void setUnlimitedMulligans(@RUntainted boolean val)
    {
        unlimitedMulligans = val;
    }

    public boolean getBalancedTowers()
    {
        return balancedTowers;
    }

    public void setBalancedTowers(@RUntainted boolean val)
    {
        balancedTowers = val;
    }

    public boolean getAutoSansLordBattles()
    {
        return autoSansLordBattles;
    }

    public void setAutoSansLordBattles(boolean val)
    {
        autoSansLordBattles = val;
    }

    public boolean getInactivityTimeout()
    {
        return inactivityTimeout;
    }

    public void setInactivityTimeout(boolean val)
    {
        inactivityTimeout = val;
    }

    public boolean getProbabilityBasedBattleHits()
    {
        return probabilityBasedBattleHits;
    }

    public void setProbabilityBasedBattleHits(boolean val)
    {
        probabilityBasedBattleHits = val;
    }

    public String getGameOptionsFlagsString()
    {
        String s = (this.unlimitedMulligans ? "U" : "-")
            + (this.balancedTowers ? "B" : "-")
            + (this.autoSansLordBattles ? "N" : "-")
            + (this.probabilityBasedBattleHits ? "P" : "-")
            + (this.inactivityTimeout ? "I" : "-");
        return s;
    }

    public String GetOptionsTooltipText()
    {
        String ttText = (this.unlimitedMulligans ? Options.unlimitedMulligans
            : "-")
            + ", "
            + (this.balancedTowers ? Options.balancedTowers : "-")
            + ", "
            + (this.autoSansLordBattles ? Options.sansLordAutoBattle : "-")
            + ", "
            + (this.probabilityBasedBattleHits ? Options.pbBattleHits : "-")
            + ", "
            + (this.inactivityTimeout ? Options.inactivityTimeout : "-");
        return ttText;
    }

    public String getTeleportOptionsFlagsString()
    {
        String s = (this.noFirstTurnT2TTeleportOpt ? "2" : "-")
            + (this.noFirstTurnTeleportOpt ? "1" : "-")
            + (this.towerToTowerTeleportOnlyOpt ? "T" : "-")
            + (this.noTowerTeleportOpt ? "w" : "-")
            + (this.noTitanTeleportOpt ? "t" : "-")
            + (this.noFirstTurnWarlockRecruitOpt ? "W" : "-");
        return s;
    }

    public String GetTeleportOptionsTooltipText()
    {
        String ttText = (this.noFirstTurnT2TTeleportOpt ? Options.noFirstTurnT2TTeleport
            : "-")
            + ", "
            + (this.noFirstTurnTeleportOpt ? Options.noFirstTurnTeleport : "-")
            + ", "
            + (this.towerToTowerTeleportOnlyOpt ? Options.towerToTowerTeleportOnly
                : "-")
            + ", "
            + (this.noTowerTeleportOpt ? Options.noTowerTeleport : "-")
            + ", "
            + (this.noTitanTeleportOpt ? Options.noTitanTeleport : "-")
            + ", "
            + (this.noFirstTurnWarlockRecruitOpt ? Options.noFirstTurnWarlockRecruit
                : "-");
        return ttText;
    }

    /**
     * Have enough players enrolled (at least "min")
     * @return true or false whether enough (at least 'min') players are
     *         already enrolled to this game
     */
    public boolean hasEnoughPlayers()
    {
        return enrolledPlayers >= min;
    }

    /**
     * Have enough players enrolled (at least "min")
     * @return true or false whether all enrolled players are online
     */
    public boolean allEnrolledOnline()
    {
        return onlineCount >= enrolledPlayers;
    }

    /**
     * Has the scheduled time come?
     * @return true if the game can be started according to schedule
     */
    public boolean isDue()
    {
        long now = new Date().getTime();
        return now > startTime;
    }

    public Integer getMin()
    {
        return Integer.valueOf(min);
    }

    public void setMin(@RUntainted Integer val)
    {
        min = val.intValue();
    }

    public Integer getTargetInteger()
    {
        return Integer.valueOf(target);
    }

    public int getTarget()
    {
        return target;
    }

    public void setTarget(@RUntainted Integer val)
    {
        target = val.intValue();
    }

    public Integer getMax()
    {
        return Integer.valueOf(max);
    }

    public void setMax(@RUntainted Integer val)
    {
        max = val.intValue();
    }

    public Integer getEnrolledCount()
    {
        return Integer.valueOf(enrolledPlayers);
    }

    public boolean enoughPlayersEnrolled()
    {
        return enrolledPlayers >= min;
    }

    public int getOnlineCount()
    {
        return onlineCount;
    }

    public void setOnlineCount(@RUntainted int count)
    {
        onlineCount = count;
    }

    public void setEnrolledCount(@RUntainted Integer val)
    {
        enrolledPlayers = val.intValue();
    }

    public void setResumeFromFilename(String filename)
    {
        this.resumeFromFilename = filename;
    }

    public String getResumeFromFilename()
    {
        return this.resumeFromFilename;
    }

    public ArrayList<User> getPlayers()
    {
        return this.players;
    }

    public @RUntainted String getPlayerListAsString()
    {
        if (players == null)
        {
            LOGGER.warning("Tried to get player list as string for gameId "
                + gameId + ", but player list == null");
            return "<none>";
        }

        StringBuilder playerList = new StringBuilder("");
        if (players.size() == 0)
        {
            return "<none>";
        }

        for (User u : players)
        {
            if (playerList.length() != 0)
            {
                playerList.append(", ");
            }
            playerList.append(u.getName());
        }

        return playerList.substring(0);
    }

    public boolean isFirstInEnrolledList(String name)
    {
        if (players.size() > 0 && players.get(0).getName().equals(name))
        {
            return true;
        }
        return false;
    }

    public boolean reEnrollIfNecessary(User newUser)
    {
        String newName = newUser.getName();
        boolean found = removeIfEnrolled(newName);
        if (found)
        {
            players.add(newUser);
            enrolledPlayers = players.size();
            LOGGER.finest("Re-Enrolled user " + newName + " to game "
                + getGameId());
            LOGGER.finest("Players now: " + getPlayerListAsString());
        }
        else
        {
            LOGGER.finest("User " + newName + " not in game " + getGameId()
                + " - nothing to do.");
        }

        return found;
    }

    /**
     * TODO remove overlap with isEnrolled
     *
     * If user with name "newName" is found, remove it from game, so that
     * it can be safely enrolled again.
     * E.g. after user reconnected or accidental double click to enroll button
     * @param newName
     * @return Whether user was found
     */
    public boolean removeIfEnrolled(String newName)
    {
        Iterator<User> it = players.iterator();
        boolean found = false;
        while (!found && it.hasNext())
        {
            User user = it.next();
            String name = user.getName();
            if (newName.equalsIgnoreCase(name))
            {
                it.remove();
                enrolledPlayers = players.size();
                found = true;
            }
        }
        return found;
    }

    public boolean isEnrolled(String searchName)
    {
        boolean found = false;
        for (User u : players)
        {
            if (searchName.equalsIgnoreCase(u.getName()))
            {
                found = true;
                break;
            }
        }
        return found;
    }

    public void setPlayerList(@RUntainted ArrayList<User> playerlist)
    {
        players = playerlist;
    }

    public boolean updateOnlineCount(@RUntainted int newCount)
    {
        if (newCount != onlineCount)
        {
            onlineCount = newCount;
            return true;
        }
        return false;
    }

    /*
     * return reason why fail, or null if ok
     */
    public @RUntainted String enroll(User user)
    {
        String reason = null;
        // Just in case, to avoid duplicates (e.g. bounce/double click)
        if (variant.equals("DinoTitan"))
        {
            IWebClient client = user.getWebserverClient();
            if (client.getClientVersion() < WebClient.WC_VERSION_DINO_OK)
            {
                reason = "Client does not support this variant, please upgrade!";
                return reason;
            }
        }

        removeIfEnrolled(user.getName());
        if (enrolledPlayers < max)
        {
            synchronized (players)
            {
                players.add(user);
                enrolledPlayers++;
            }
        }
        else
        {
            reason = "Game is full";
        }

        return reason;
    }

    public String unenroll(User user)
    {
        String reason = null;

        synchronized (players)
        {
            int index = players.indexOf(user);
            if (index != -1)
            {
                players.remove(index);
                enrolledPlayers--;
                if (players.size() != enrolledPlayers)
                {
                    LOGGER.log(Level.SEVERE,
                        "players.size() != enrolledPlayers!!");
                }
            }
            else
            {
                reason = "Player " + user.getName()
                    + " to unenroll not found in game " + gameId;
            }
        }

        return reason;
    }

    public void storeToOptionsObject(Options gameOptions,
        String localPlayerName, boolean noAIs)
    {
        if (this.portNr == -1)
        {
            this.portNr = Constants.defaultPort;
        }
        // XXX get port from gameinfo
        gameOptions.setOption(Options.serveAtPort, this.portNr);

        // XXX get host from gameinfo
        // XXX set host in gameinfo, send to server/client

        gameOptions.setOption(Options.variant, getVariant());
        gameOptions.setOption(Options.viewMode, getViewmode());
        gameOptions.setOption(Options.autosave, getAutosave());
        gameOptions.setOption(Options.eventExpiring, getEventExpiring());
        gameOptions.setOption(Options.unlimitedMulligans,
            getUnlimitedMulligans());
        gameOptions.setOption(Options.balancedTowers, getBalancedTowers());
        gameOptions.setOption(Options.sansLordAutoBattle,
            getAutoSansLordBattles());
        gameOptions.setOption(Options.pbBattleHits,
            getProbabilityBasedBattleHits());
        gameOptions.setOption(Options.inactivityTimeout,
            getInactivityTimeout());

        gameOptions.setOption(Options.noFirstTurnTeleport,
            this.noFirstTurnTeleportOpt);
        gameOptions.setOption(Options.noFirstTurnTeleport,
            this.noFirstTurnTeleportOpt);
        gameOptions.setOption(Options.towerToTowerTeleportOnly,
            this.towerToTowerTeleportOnlyOpt);
        gameOptions
            .setOption(Options.noTowerTeleport, this.noTowerTeleportOpt);
        gameOptions
            .setOption(Options.noTitanTeleport, this.noTitanTeleportOpt);
        gameOptions.setOption(Options.noFirstTurnWarlockRecruit,
            this.noFirstTurnWarlockRecruitOpt);

        // gameOptions.setOption(Options.autoQuit, true);
        String name;
        String type;
        Iterator<User> it = getPlayers().iterator();

        int numPlayers = getTarget();
        if (noAIs)
        {
            numPlayers = getPlayers().size();
        }

        for (int i = 0; i < numPlayers; i++)
        {
            if (it.hasNext())
            {
                User u = it.next();

                name = u.getName();
                // TODO is ignorecase necessary here?
                // Added just in case when fixing another case where it matters ...
                if (localPlayerName != null
                    && name.equalsIgnoreCase(localPlayerName))
                {
                    type = Constants.human;
                    // use user real name;
                }
                else
                {
                    type = Constants.network;
                    name = Constants.byClient;

                }
            }
            else
            {
                type = Constants.anyAI;
                name = Constants.byColor;
            }
            gameOptions.setOption(Options.playerName + i, name);
            gameOptions.setOption(Options.playerType + i, type);
        }

        gameOptions.setOption(Options.autoStop, true);

        return;
    }

    public boolean relevantForSaving()
    {
        return state.equals(GameState.PROPOSED) || state.equals(GameState.DUE)
            || state.equals(GameState.SUSPENDED);
    }

    public boolean isStartable()
    {
        return state.equals(GameState.PROPOSED) || state.equals(GameState.DUE)
            || state.equals(GameState.SUSPENDED);
    }

    public boolean isRunning()
    {
        return state.equals(GameState.RUNNING);
    }

    public boolean wasAlreadyStarted()
    {
        return !(state.equals(GameState.PROPOSED) || state
            .equals(GameState.DUE));
    }

    /** if game is proposed or due, re-enroll a player that just logs in;
     * otherwise not
     * @return true if game is either proposed or due
     */
    public boolean isProposedOrDue()
    {
        return state.equals(GameState.PROPOSED) || state.equals(GameState.DUE);
    }

    public void markStarting(User starter)
    {
        this.startingUser = starter;
        this.oldState = state;
        this.state = GameState.ACTIVATED;
    }

    public boolean isStarting()
    {
        return startingUser != null;
    }

    public void cancelStarting()
    {
        this.state = oldState;
        this.startingUser = null;
    }

    public User getStartingUser()
    {
        return startingUser;
    }

    public boolean wantsDetailedLogging()
    {
        return isEnrolled("Sir Volander") || isEnrolled("dlmartin");
    }

    /**
     *  Enum for the possible TYPES of a game
     *  (scheduled or instant, perhaps later also template?)
     */
    public static enum GameType
    {
        SCHEDULED, INSTANT;
        // TEMPLATE ?
    }

    /**
     *  Enum for the possible states of a game:
     */
    public static enum GameState
    {
        PROPOSED, DUE, ACTIVATED, STARTING, READY_TO_CONNECT, RUNNING, ENDING, SUSPENDED, DELETED;
    }

}
