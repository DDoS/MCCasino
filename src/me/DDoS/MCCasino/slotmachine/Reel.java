package me.DDoS.MCCasino.slotmachine;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
/**
 *
 * @author DDos
 */
public class Reel {
    
    private static final Random random = new SecureRandom();
    //
    private final List<ReelValue> values;
    
    public Reel(List<ReelValue> values) {
        
        this.values = values;
        
    }
    
    public ItemStack getRandomItem() {
        // CLEANED THIS FUNCTION & IMPROVED ODDS CALCULATION
        int totalWeights = 0;
        
        for (ReelValue value : values) {
            totalWeights+= value.getProb();
        }
        
        int i = 0;
        final int rand = random.nextInt(totalWeights);
        for (ReelValue values : values) {
            i += value.getProb();
            if (rand < i)
                return value.getItem();
        }        
    }
}
