package me.DDoS.MCCasino.util;

import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;

/**
 *
 * @author DDoS
 */
public class MCCDropCleaner implements Runnable {

    private MCCSlotMachine slotMachine;

    public MCCDropCleaner(MCCSlotMachine slotMachine) {
     
        this.slotMachine = slotMachine;
    
    }
    
    @Override
    public void run() {

        slotMachine.clearItems();
        slotMachine.setActive(true);

    }
}