package me.DDoS.MCCasino.slotmachine;

import java.util.List;

/**
 *
 * @author DDoS
 */
public class Reward {
    
    private final List<Integer> results;
    private final int multiplier;
    
    public Reward(List<Integer> results, int multiplier) {
        
        this.results = results;
        this.multiplier = multiplier;
        
    }
    
    private boolean checkResults(List<Integer> results) {       

        int i = 0;
        
        for (int result : results) {
            
            int i2 = this.results.get(i);
            
            if (i2 == -1) {
                
                i++;
                continue;
                
            }
            
            if (i2 != result) {
                
                return false;
                
            }
            
            i++;
            
        }
        
        return true;
        
    }
    
    public int get(List<Integer> results) {
        
        if (checkResults(results)) {
            
            return multiplier;
            
        }
        
        return 0;
        
    }
}
