package me.DDoS.MCCasino.bet;

import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public interface BetProvider {

    public Bet getBet(Player player);
    
}
