package me.DDoS.MCCasino.slotmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
/**
 *
 * @author DDos
 */
public class MCCReel {
    
    private List<MCCReelValue> values;
    
    public MCCReel(List<MCCReelValue> values) {
        
        this.values = values;
        
    }
    
    public ItemStack getRandomItem() {
        
        final List<Integer> weightedNumbers = new ArrayList<Integer>();
        Random random = new Random();
        
        for (MCCReelValue value : values) {
            
            weightedNumbers.add((random.nextInt(49999) + 1) * value.getProb());
            
        }
        
        int highestNumber = Integer.MIN_VALUE;
        int winnerPos = 0;
        int i2 = 0;
        
        for (int weightedNumber : weightedNumbers) {
            
            if (weightedNumber > highestNumber) {
                
                highestNumber = weightedNumber;
                winnerPos = i2;
                
            }
            
            i2++;
            
        }
        
        return values.get(winnerPos).getItem();
        
    }
}
