package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 *
 * @author DDoS
 */
public class MCCBlockListener extends BlockListener {

    private MCCasino plugin;

    public MCCBlockListener(MCCasino instance) {

        plugin = instance;

    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {

        if (checkForSign(event.getBlock())) {

            Sign sign = (Sign) event.getBlock().getState();

            if (sign.getLine(0).equalsIgnoreCase("[MCCasino]") && sign.getLine(1).equalsIgnoreCase("Reel")) {

                MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

                if (machine != null) {
                    
                    machine.removeReelLocation(sign.getBlock().getLocation());
                    MCCUtil.tell(event.getPlayer(), "Reel removed.");
                    return;
                
                }
            }
        }
    }
    
    private boolean checkForSign(Block block) {

        switch (block.getType()) {

            case WALL_SIGN:
                return true;

            case SIGN_POST:
                return true;

            default:
                return false;

        }
    }
}