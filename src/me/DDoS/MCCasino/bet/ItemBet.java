package me.DDoS.MCCasino.bet;

import me.DDoS.MCCasino.util.MCCUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDos
 */
public class ItemBet implements Bet {
    
    private final ItemStack item;
    private ItemStack[] wonItems;

    public ItemBet(ItemStack item) {

        this.item = item;
        
    }

    @Override
    public void applyMultiplier(int multiplier) {

        if (((item.getAmount() * multiplier)) <= 64) {

            wonItems = new ItemStack[1];
            wonItems[0] = new ItemStack(item.getTypeId(), item.getAmount() * multiplier);
            return;

        }

        int totalItems = (item.getAmount() * multiplier);
        int numberOfFullStacks = totalItems / 64;
        int oddStack = totalItems % 64;
        int totalNumberOfStack = (oddStack > 0) ? (numberOfFullStacks + 1) : numberOfFullStacks;
        int ID = item.getTypeId();

        wonItems = new ItemStack[totalNumberOfStack];

        for (int i = 0; i < numberOfFullStacks; i++) {

            wonItems[i] = new ItemStack(ID, 64);

        }

        if (oddStack > 0) {

            wonItems[totalNumberOfStack - 1] = new ItemStack(ID, oddStack);

        }    
    }
    
    @Override
    public int getAmount() {
        
        int total = 0;
        
        for (ItemStack i : wonItems) {

            total += i.getAmount();
            
        }
        
        return total;
        
    }
    
    @Override
    public Material getMaterial() {
        
        return wonItems[0].getType();
        
    }
    
    @Override
    public void giveReward(Player player) {

        MCCUtil.tell(player, "You received " + getAmount() + " of item '" + wonItems[0].getType().toString().toLowerCase() + "'.");
        player.getInventory().addItem(wonItems);
        player.updateInventory(); 
        
    }
}
