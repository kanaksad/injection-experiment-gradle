package net.sf.colossus.game;


import net.sf.colossus.variant.BattleHex;
import net.sf.colossus.variant.CreatureType;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;


/**
 *
 * @author Romain Dolbeau
 */
public interface BattleCritter
{

    CreatureType getType();

    @RUntainted BattleHex getCurrentHex();

    @RUntainted String getDescription();

    @RUntainted int getHits();

    @RUntainted int getPointValue();

    @RUntainted int getPower();

    int getPoisonDamage();

    int getPoison();

    int getSlows();

    int getSlowed();

    @RUntainted int getSkill();

    @RUntainted BattleHex getStartingHex();

    @RUntainted int getTag();

    int getTitanPower();

    boolean hasMoved();

    boolean hasStruck();

    void moveToHex(@RUntainted BattleHex hex);

    boolean isDead();

    boolean isDefender();

    boolean isLord();

    boolean isDemiLord();

    boolean isRangestriker();

    boolean isTitan();

    void setDead(boolean dead);

    void setCurrentHex(@RUntainted BattleHex hex);

    void setHits(@RUntainted int hits);

    void setMoved(boolean moved);

    void setPoisonDamage(int damage);

    void setSlowed(int slowValue);

    void addPoisonDamage(int damage);

    void addSlowed(int slowValue);

    void setStruck(boolean struck);

    boolean useMagicMissile();

    boolean wouldDieFrom(int hits);

}
