package net.sf.colossus.webcommon;


import java.util.List;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  Interface for what WebClient sends to WebServer
 *
 *  @author Clemens Katzer
 */
public interface IWebServer
{
    public static final String Login = "login";
    public static final String Logout = "logout";
    public static final String RegisterUser = "register";
    public static final String ConfirmRegistration = "confirm";
    public static final String ChangePassword = "changepassword";
    public static final String Propose = "propose";
    public static final String Enroll = "enroll";
    public static final String Unenroll = "unenroll";
    public static final String Start = "start";
    public static final String Resume = "resume";
    public static final String DeleteSuspendedGame = "deleteSuspendedGame";
    public static final String StartAtPlayer = "startAtPlayer";
    public static final String StartedByPlayer = "startedByPlayer";
    public static final String LocallyGameOver = "locallyGameOver";
    public static final String Cancel = "cancel";
    public static final String ChatSubmit = "chatsubmit";
    public static final String ShutdownServer = "shutdownserver";
    public static final String Echo = "echo";
    public static final String RereadLoginMessage = "rereadLoginMessage";
    public static final String RequestUserAttention = "requestUserAttention";
    public static final String PingResponse = "pingResponse";
    public static final String WatchGame = "watchGame";
    public static final String ConfirmCommand = "confirmCommand";
    public static final String MessageToAdmin = "messageToAdmin";

    public static final String DumpInfo = "dumpInfo";

    public static String generalChatName = "#general";

    // For historical reasons this is the same as for normal Colossus
    // network traffic, but this is not mandatory.
    public static final String WebProtocolSeparator = " ~ ";

    // login() is treated specially

    // register is treated specially, too:
    // public String registerUser(String username, String password, String email);

    // does not really return a GI object over socket, but method needs
    // that return value (internally) when used in server side.
    public GameInfo proposeGame(@RUntainted String initiator, @RUntainted String variant,
        @RUntainted String viewmode, @RUntainted long startAt, @RUntainted int duration, @RUntainted String summary,
        @RUntainted String expire, @RUntainted List<@RUntainted String> gameOptions, @RUntainted List<@RUntainted String> teleportOptions,
        @RUntainted int min, @RUntainted int target, @RUntainted int max);

    public void enrollUserToGame(@RUntainted String gameId, @RUntainted String username);

    // the next 3 would not really need to send the user over network,
    // since at server side the socketthread would know it; but the
    // interface to between socket and server needs it...
    // so we handle it "properly" also on client side, even if
    // functionally unnecessary.
    public void unenrollUserFromGame(@RUntainted String gameId, @RUntainted String username);

    public void cancelGame(@RUntainted String gameId, @RUntainted String byUser);

    /**
     *  A game was started by a WebClient user locally on his computer
     *  and is ready to accept the other players as remote client;
     *  so we notify them and tell them host and port to where to connect.
     */
    public void startGameOnPlayerHost(@RUntainted String gameId, @RUntainted String hostingPlayer,
        @RUntainted String playerHost, @RUntainted int port);

    public void startGame(@RUntainted String gameId, User user);

    public void resumeGame(@RUntainted String gameId, @RUntainted String loadGame, User user);

    public void deleteSuspendedGame(@RUntainted String gameId, User user);

    // Game started on players computer, tell WebServer that
    // he can inform all WebClient that game started successfully
    public void informStartedByPlayer(@RUntainted String gameId);

    public void informLocallyGameOver(@RUntainted String gameId);

    public void chatSubmit(@RUntainted String chatId, @RUntainted String sender, @RUntainted String message);

    public void requestUserAttention(@RUntainted long when, @RUntainted String sender,
        @RUntainted boolean isAdmin, @RUntainted String recipient, @RUntainted String message, @RUntainted int beepCount,
        @RUntainted long beepInterval, @RUntainted boolean windows);

    public void watchGame(@RUntainted String gameId, @RUntainted String username);

    // public void submitAnyText(String text);

    public void rereadLoginMessage();

    public void shutdownServer();

    public void dumpInfo();

    public @RUntainted String changeProperties(String username, String oldPW,
        @RUntainted String newPW, @RUntainted String email, @RUntainted Boolean isAdminObj);

    public void logout();

    public void messageToAdmin(@RUntainted long when, @RUntainted String username, @RUntainted String mail,
        List<@RUntainted String> message);
}
