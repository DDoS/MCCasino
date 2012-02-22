package me.DDoS.MCCasino.util;

import me.DDoS.MCCasino.slotmachine.SlotMachine;

/**
 *
 * @author DDoS
 */
public class DropCleaner implements Runnable {

    private final SlotMachine slotMachine;

    public DropCleaner(SlotMachine slotMachine) {
     
        this.slotMachine = slotMachine;
    
    }
    
    @Override
    public void run() {

        slotMachine.clearItems();
        slotMachine.setActive(true);

    }
}
