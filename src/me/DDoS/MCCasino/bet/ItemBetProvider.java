package me.DDoS.MCCasino.bet;

import java.util.List;
import me.DDoS.MCCasino.util.MCCUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
public class ItemBetProvider implements BetProvider {

    private List<ItemStack> limits;

    public ItemBetProvider(List<ItemStack> limits) {

        this.limits = limits;

    }

    @Override
    public Bet getBet(Player player) {

        ItemStack bet = player.getItemInHand();

        if (bet.getType().equals(Material.AIR)) {

            MCCUtil.tell(player, "You can't bet nothing!");
            return null;

        }

        if (bet.getType().equals(Material.DIAMOND_SWORD) || bet.getType().equals(Material.IRON_SWORD) || bet.getType().equals(Material.STONE_SWORD)
                || bet.getType().equals(Material.GOLD_SWORD) || bet.getType().equals(Material.WOOD_SWORD)) {

            return null;

        }

        if (limits.isEmpty()) {

            player.getInventory().removeItem(bet);
            return new ItemBet(bet);

        }

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

            player.getInventory().removeItem(limit);
            player.updateInventory();
            return new ItemBet(limit);

        }

        if (notEnough) {

            MCCUtil.tell(player, "You need to increase your bet of '" + bet.getType().toString().toLowerCase()
                    + "' to " + smallest + " items");


        } else {

            MCCUtil.tell(player, "This machine does not accept item '" + bet.getType().toString().toLowerCase() + "' as a bet.");

        }

        return null;
        
    }
}
