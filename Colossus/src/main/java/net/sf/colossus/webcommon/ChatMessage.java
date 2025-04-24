/**
 *
 */
package net.sf.colossus.webcommon;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


public class ChatMessage
{
    @RUntainted String chatId;
    @RUntainted long when;
    @RUntainted String sender;
    @RUntainted String message;

    public ChatMessage(@RUntainted String chatId, @RUntainted long when, @RUntainted String sender, @RUntainted String message)
    {
        this.chatId = chatId;
        this.when = when;
        this.sender = sender;
        this.message = message;
    }

    public @RUntainted String getChatId()
    {
        return this.chatId;
    }

    public @RUntainted long getWhen()
    {
        return this.when;
    }

    public @RUntainted String getSender()
    {
        return this.sender;
    }

    public @RUntainted String getMessage()
    {
        return this.message;
    }

}
