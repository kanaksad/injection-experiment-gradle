package net.sf.colossus.webcommon;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  Interface for what WebServer (Public Game Server) sends to WebClient
 *
 *  @author Clemens Katzer
 */
public interface IWebClient
{
    public static final String alreadyLoggedIn = "alreadyLoggedIn";
    public static final String grantAdmin = "grantAdmin";
    public static final String tooManyUsers = "tooManyUsers";
    public static final String connectionClosed = "connectionClosed";
    public static final String forcedLogout = "forcedLogout";
    public static final String didEnroll = "didEnroll";
    public static final String didUnenroll = "didUnenroll";
    public static final String gameInfo = "gameInfo";
    public static final String userInfo = "userInfo";
    public static final String tellOwnInfo = "tellOwnInfo";
    public static final String gameStarted = "gameStarted";
    public static final String gameStartsNow = "gameStartsNow";
    public static final String gameStartsSoon = "gameStartsSoon";
    public static final String gameCancelled = "gameCancelled";
    public static final String chatDeliver = "chatDeliver";
    public static final String generalMessage = "generalMessage";
    public static final String systemMessage = "systemMessage";
    public static final String requestAttention = "requestAttention";
    public static final String watchGameInfo = "watchGameInfo";
    public static final String pingRequest = "pingRequest";

    public void grantAdminStatus();

    public void didEnroll(@RUntainted String gameId, @RUntainted String username);

    public void didUnenroll(@RUntainted String gameId, @RUntainted String username);

    public void userInfo(@RUntainted int loggedin, @RUntainted int enrolled, @RUntainted int playing, @RUntainted int dead,
        @RUntainted long ago, @RUntainted String text);

    public void gameInfo(@RUntainted GameInfo gi);

    public void gameStartsNow(@RUntainted String gameId, @RUntainted int port, @RUntainted String hostingHost,
        @RUntainted int inactivityCheckInterval, @RUntainted int inactivityWarningInterval,
        @RUntainted int inactivityTimeout);

    public void gameStartsSoon(@RUntainted String gameId, @RUntainted String startUser);

    public void gameCancelled(@RUntainted String gameId, @RUntainted String byUser);

    public void chatDeliver(@RUntainted String chatId, @RUntainted long when, @RUntainted String sender,
        @RUntainted String message, @RUntainted boolean resent);

    public void connectionReset(boolean forcedLogout);

    public @RUntainted int getClientVersion();

    public void deliverGeneralMessage(@RUntainted long when, @RUntainted boolean error, @RUntainted String title,
        @RUntainted String message);

    public void systemMessage(@RUntainted long when, @RUntainted String message);

    public void requestAttention(@RUntainted long when, @RUntainted String byUser, @RUntainted boolean byAdmin,
        @RUntainted String message, @RUntainted int beepCount, @RUntainted long beepInterval, @RUntainted boolean windows);

    public void watchGameInfo(@RUntainted String gameId, @RUntainted String host, @RUntainted int port);

    public void tellOwnInfo(@RUntainted String email);
}
