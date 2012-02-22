package me.DDoS.MCCasino.slotmachine;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
public class ReelValue {
    
    private final ItemStack item;
    private final int probability;
    
    public ReelValue(ItemStack item, int probability) {
        
        this.item = item;
        this.probability = probability;
        
    }
    
    
    public ItemStack getItem() {
        
        return item;
        
    }
    
    public int getProb() {
        
        return probability;
        
    }
}
