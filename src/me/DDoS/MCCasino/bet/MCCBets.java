package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.bet.MCCBet;
import java.util.List;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.MCCasino;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
public class MCCBets {

    private MCCBetTypes betType;
    private int cost;
    private boolean limitOn;
    private List<ItemStack> limits;

    public MCCBets(MCCBetTypes betType, int cost, boolean limitOn, List<ItemStack> limits) {

        this.betType = betType;
        this.cost = cost;
        this.limitOn = limitOn;
        this.limits = limits;

    }

    public MCCBet getBet(ItemStack bet, Player player) {

        if (betType == MCCBetTypes.MONEY) {

            EconomyResponse result = MCCasino.economy.withdrawPlayer(player.getName(), cost);

            if (result.transactionSuccess()) {

                MCCUtil.tell(player, "Bet accepted. " + cost + " dollar(s) have been withdrawed from your account.");
                return new MCCMoneyBet(cost);

            }

            MCCUtil.tell(player, "Transaction failed.");
            return null;

        }

        if (bet.getType().equals(Material.AIR)) {

            MCCUtil.tell(player, "You can't bet nothing!");
            return null;

        }

        if (bet.getType().equals(Material.DIAMOND_SWORD) || bet.getType().equals(Material.IRON_SWORD) || bet.getType().equals(Material.STONE_SWORD)
                || bet.getType().equals(Material.GOLD_SWORD) || bet.getType().equals(Material.WOOD_SWORD)) {

            MCCUtil.tell(player, "Weapons are not accepted.");
            return null;

        }

        if (limitOn) {

            boolean notEnough = false;
            int smallest = Integer.MAX_VALUE;

            for (ItemStack limit : limits) {

                if (bet.getType() != limit.getType()) {

                    continue;

                }

                int amount = limit.getAmount();

                if (bet.getAmount() < amount) {

                    notEnough = true;

                    if (amount < smallest) {

                        smallest = amount;

                    }

                    continue;

                }

                player.getInventory().remove(limit);
                player.updateInventory();
                return new MCCItemBet(limit);

            }

            if (notEnough) {

                MCCUtil.tell(player, "You need to increase your bet of '" + bet.getType().toString().toLowerCase()
                        + "' to " + smallest + " items");


            } else {

                MCCUtil.tell(player, "This machine does not accept item '" + bet.getType().toString().toLowerCase() + "' as a bet.");

            }
            
            return null;
            
        }

        player.getInventory().remove(bet);
        return new MCCItemBet(bet);

    }
}