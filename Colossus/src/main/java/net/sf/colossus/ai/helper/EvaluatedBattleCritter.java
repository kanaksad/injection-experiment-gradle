package net.sf.colossus.ai.helper;


import net.sf.colossus.game.BattleCritter;
import net.sf.colossus.variant.BattleHex;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *
 * @author Romain Dolbeau
 */
public class EvaluatedBattleCritter implements BattleCritter
{
    private final BattleCritter parent;
    private @RUntainted BattleHex startingHex;
    private @RUntainted BattleHex currentHex;

    EvaluatedBattleCritter(BattleCritter parent)
    {
        this.parent = parent;
        startingHex = parent.getStartingHex();
        currentHex = parent.getCurrentHex();
    }

    public CreatureType getType()
    {
        return parent.getType();
    }

    public @RUntainted BattleHex getCurrentHex()
    {
        return currentHex;
    }

    public @RUntainted String getDescription()
    {
        return parent.getDescription();
    }

    public @RUntainted int getHits()
    {
        return parent.getHits();
    }

    public int getPoison()
    {
        return parent.getPoison();
    }

    public int getPoisonDamage()
    {
        return parent.getPoisonDamage();
    }

    public void addPoisonDamage(int damage)
    {
        parent.addPoisonDamage(damage);
    }

    public void setPoisonDamage(int damage)
    {
        parent.setPoisonDamage(damage);
    }

    public int getSlowed()
    {
        return parent.getSlowed();
    }

    public void setSlowed(int slowValue)
    {
        parent.setSlowed(slowValue);
    }

    public void addSlowed(int slowValue)
    {
        parent.addSlowed(slowValue);
    }

    public int getSlows()
    {
        return parent.getSlows();
    }

    public @RUntainted int getPointValue()
    {
        return parent.getPointValue();
    }

    public @RUntainted int getPower()
    {
        return parent.getPower();
    }

    public @RUntainted int getSkill()
    {
        return parent.getSkill();
    }

    public @RUntainted BattleHex getStartingHex()
    {
        return startingHex;
    }

    public @RUntainted int getTag()
    {
        return parent.getTag();
    }

    public int getTitanPower()
    {
        return parent.getTitanPower();
    }

    public boolean hasMoved()
    {
        return !currentHex.equals(startingHex);
    }

    public boolean hasStruck()
    {
        return parent.hasStruck();
    }

    public void moveToHex(@RUntainted BattleHex hex)
    {
        startingHex = currentHex;
        currentHex = hex;
    }

    public boolean isDead()
    {
        return parent.isDead();
    }

    public boolean isDefender()
    {
        return parent.isDefender();
    }

    public boolean isRangestriker()
    {
        return parent.isRangestriker();
    }

    public boolean isLord()
    {
        return parent.isLord();
    }

    public boolean isDemiLord()
    {
        return parent.isDemiLord();
    }

    public boolean isTitan()
    {
        return parent.isTitan();
    }

    public void setDead(boolean dead)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCurrentHex(@RUntainted BattleHex hex)
    {
        currentHex = hex;
    }

    public void setHits(int hits)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMoved(boolean moved)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStruck(boolean struck)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean useMagicMissile()
    {
        return parent.useMagicMissile();
    }

    public boolean wouldDieFrom(int hits)
    {
        return parent.wouldDieFrom(hits);
    }
}
