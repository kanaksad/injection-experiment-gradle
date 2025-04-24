package net.sf.colossus.gui;


import net.sf.colossus.game.Legion;
import net.sf.colossus.variant.BattleHex;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 * Anything that happens in the GUI and that has effect to Client or Server,
 * especially all things that client shall send to server.
 */
public interface GUICallbacks
{

    public void leaveCarryMode();

    public void applyCarries(BattleHex hex);

    public void acquireAngelCallback(Legion legion, CreatureType angelType);

    public void answerFlee(@RUntainted Legion ally, @RUntainted boolean answer);

    public void answerConcede(@RUntainted Legion legion, @RUntainted boolean answer);

    public void doBattleMove(@RUntainted int tag, @RUntainted BattleHex hex);

    public void undoBattleMove(BattleHex hex);

    public void strike(@RUntainted int tag, BattleHex hex);

    public void doneWithBattleMoves();

    public void doneWithStrikes();

    public void concede();
}
