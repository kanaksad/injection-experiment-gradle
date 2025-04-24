/**
 *
 */
package net.sf.colossus.webcommon;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *
 */
public interface IPortProvider
{
    public abstract @RUntainted int getFreePort(GameInfo gi);
}
