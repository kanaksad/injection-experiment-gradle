package net.sf.colossus.client;


import java.util.Collection;

import net.sf.colossus.server.IServer;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * Generic type of connection to the server. Right now we have only
 * Socket-based connection (SocketClientThread); but the ClientThread
 * should become unaware of the type of connection, only do the
 * messageString-to-method-call and vice-versa translation,
 * and the connection specific parts (read/write to socket or via e.g.
 * a queue) to the ServerConnection class(es).
 */
public interface IServerConnection
{
    public void setClient(Client client);

    public String getReasonFail();

    public @RUntainted String getVariantNameForInit();

    public Collection<@RUntainted String> getPreliminaryPlayerNames();

    public void startThread();

    public void updatePlayerName(@RUntainted String playerName);

    public IServer getIServer();

    public boolean isAlreadyDown();

    public void stopSocketClientThread(boolean sendDispose);

    public void enforcedConnectionException();

    public void requestSyncDelta(@RUntainted int lastRcvdMsgNr, @RUntainted int syncCounter);

    public @RUntainted int abandonAndGetMessageCounter();

    public int getDisposedQueueLen();

}
