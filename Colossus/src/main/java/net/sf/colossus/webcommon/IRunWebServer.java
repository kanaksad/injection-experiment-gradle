package net.sf.colossus.webcommon;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *  Interface for GameInfo, what it needs to communicate with
 *  WebServer regarding (so far only) ending a game.
 *  The functionality is needed only on Server side, but
 *  GameInfo also goes to Client ( = main Colossus.jar) side
 *  and I don't want to deliver all Web server stuff inside
 *  the main jar.
 *
 *  TODO Align with IGameRunner and RunGameInSameJVM/RunGameInOwnJVM
 *
 *  @author Clemens Katzer
 */
public interface IRunWebServer
{
    public void tellEnrolledGameStartsSoon(@RUntainted GameInfo gi);

    public void tellEnrolledGameStartsNow(@RUntainted GameInfo gi, @RUntainted String host, @RUntainted int port);

    public void gameStarted(@RUntainted GameInfo gi);

    public void allTellGameInfo(@RUntainted GameInfo gi);

    public void gameFailed(GameInfo gi, @RUntainted String reason);

    public void informAllEnrolledAbout(GameInfo gi, @RUntainted String message);

    public void unregisterGame(@RUntainted GameInfo gi, int port);

    public IPortProvider getPortProvider();
}
