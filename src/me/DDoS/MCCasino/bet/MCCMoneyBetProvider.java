package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.util.MCCUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MCCMoneyBetProvider implements MCCBetProvider {

    private int cost;
    private Economy economy;

    public MCCMoneyBetProvider(int cost, Economy economy) {

        this.cost = cost;
        this.economy = economy;
        
    }

    @Override
    public MCCBet getBet(Player player) {

        EconomyResponse result = economy.withdrawPlayer(player.getName(), cost);

        if (result.transactionSuccess()) {

            MCCUtil.tell(player, "Bet accepted. " + cost + " dollar(s) have been withdrawned from your account.");
            return new MCCMoneyBet(cost, economy);

        }

        MCCUtil.tell(player, "Transaction failed: " + result.errorMessage);
        return null;

    }
}
