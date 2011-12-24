package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.util.MCCUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MCCMoneyBetProvider implements MCCBetProvider {

    private int cost;

    public MCCMoneyBetProvider(int cost) {

        this.cost = cost;

    }

    @Override
    public MCCBet getBet(Player player) {

        EconomyResponse result = MCCasino.economy.withdrawPlayer(player.getName(), cost);

        if (result.transactionSuccess()) {

            MCCUtil.tell(player, "Bet accepted. " + cost + " dollar(s) have been withdrawed from your account.");
            return new MCCMoneyBet(cost);

        }

        MCCUtil.tell(player, "Transaction failed.");
        return null;

    }
}
