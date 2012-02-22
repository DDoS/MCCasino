package me.DDoS.MCCasino.slotmachine;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
public class ReelValue {
    
    private ItemStack item;
    private int probability;
    
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
