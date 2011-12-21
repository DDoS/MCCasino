package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.bet.MCCBet;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MCCMoneyBet implements MCCBet {

    private int amount;

    public MCCMoneyBet(int amount) {

        this.amount = amount;

    }

    @Override
    public void applyMultiplier(int multiplier) {

        amount = (amount * multiplier) + amount;
        return;

    }

    @Override
    public void giveReward(Player player) {

        MCCasino.economy.depositPlayer(player.getName(), amount);
        MCCUtil.tell(player, "You won " + amount + " dollar(s). The amount has been deposited in your account.");
        return;

    }
}
