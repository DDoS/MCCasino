package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.util.MCCUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MoneyBet implements Bet {

    private int amount;
    private final Economy economy;

    public MoneyBet(int amount, Economy economy) {

        this.amount = amount;
        this.economy = economy;

    }

    @Override
    public void applyMultiplier(int multiplier) {

        amount = (amount * multiplier) + amount;
        return;

    }

    @Override
    public void giveReward(Player player) {

        economy.depositPlayer(player.getName(), amount);
        MCCUtil.tell(player, "You won " + amount + " dollar(s). The amount has been deposited in your account.");
        return;

    }
}
