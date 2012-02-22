package me.DDoS.MCCasino.bet;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public interface Bet {

    public void applyMultiplier(int multiplier);

    public void giveReward(Player player);
 
    public int getAmount();
 
    public Material getMaterial();
    
}
