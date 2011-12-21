package me.DDoS.MCCasino.slotmachine;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
public class MCCReelValue {
    
    private ItemStack item;
    private int probability;
    
    public MCCReelValue(ItemStack item, int probability) {
        
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
