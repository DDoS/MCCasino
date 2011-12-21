package me.DDoS.MCCasino.slotmachine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DDoS
 */
public class MCCReward {
    
    private List<Integer> results = new ArrayList<Integer>();
    private int multiplier;
    
    public MCCReward(List<Integer> results, int multiplier) {
        
        this.results = results;
        this.multiplier = multiplier;
        
    }
    
    private boolean checkResults(List<Integer> results) {       

        int i = 0;
        
        for (int result : results) {
            
            int i2 = this.results.get(i);
            
            if (this.results.get(i) == -1) {
                
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