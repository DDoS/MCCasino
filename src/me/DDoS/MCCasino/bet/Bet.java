package me.DDoS.MCCasino.bet;

import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public interface Bet {

    public void applyMultiplier(int multiplier);

    public void giveReward(Player player);
    
}
