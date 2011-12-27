package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 *
 * @author DDoS
 */
public class MCCBlockListener extends BlockListener {

    private MCCasino plugin;

    public MCCBlockListener(MCCasino plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getType() != Material.WALL_SIGN) {

            return;

        }

        Sign sign = (Sign) event.getBlock().getState();

        if (!sign.getLine(0).equalsIgnoreCase("[MCCasino]") || !sign.getLine(1).equalsIgnoreCase("Reel")) {

            return;

        }

        MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

        if (machine == null) {

            return;

        }

        if (machine.removeReelLocation(sign.getBlock().getLocation())) {

            MCCUtil.tell(event.getPlayer(), "Reel removed.");

        }
    }
}
    