package me.DDoS.MCCasino.bet;

import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public interface MCCBetProvider {

    public MCCBet getBet(Player player);
    
}