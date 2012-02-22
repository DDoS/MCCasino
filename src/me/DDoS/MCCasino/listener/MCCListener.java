package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.permissions.Permission;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.SlotMachine;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 *
 * @author DDoS
 */
public class MCCListener implements Listener {

    private MCCasino plugin;

    public MCCListener(MCCasino plugin) {

        this.plugin = plugin;

    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {

            return;

        }

        if (event.getClickedBlock().getType() != Material.WALL_SIGN) {

            return;

        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLine(0).equalsIgnoreCase("[MCCasino]")) {

            return;

        }

        event.setCancelled(true);

        if (sign.getLine(1).equalsIgnoreCase("Slot Machine")
                && MCCasino.permissions.hasPermission(event.getPlayer(), Permission.USE.getPermissionString())) {

            SlotMachine machine = plugin.getMachine(sign.getLine(2));

            if (machine != null) {

                machine.run(event.getPlayer());
                return;

            } else {

                MCCUtil.tell(event.getPlayer(), "This slot machine doesn't exist.");
                return;

            }
        }

        if (sign.getLine(1).equalsIgnoreCase("Reel")
                && MCCasino.permissions.hasPermission(event.getPlayer(), Permission.SETUP.getPermissionString())) {

            SlotMachine machine = plugin.getMachine(sign.getLine(2));

            if (machine == null) {

                MCCUtil.tell(event.getPlayer(), "This slot machine doesn't exist.");
                return;

            }

            if (!machine.hasAllOfItsReels()) {

                if (machine.addReelLocation(sign.getBlock().getLocation())) {

                    MCCUtil.tell(event.getPlayer(), "Reel sign added to machine.");

                } else {

                    MCCUtil.tell(event.getPlayer(), "This sign has already been added.");

                }

                return;

            }

            MCCUtil.tell(event.getPlayer(), "This machine already has all of it signs.");

        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        
        for (SlotMachine machine : plugin.getMachines()) {

            if (machine.hasItem(event.getItem())) {
                
                event.setCancelled(true);
                return;
                
            }
        }        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getType() != Material.WALL_SIGN) {

            return;

        }

        Sign sign = (Sign) event.getBlock().getState();

        if (!sign.getLine(0).equalsIgnoreCase("[MCCasino]") || !sign.getLine(1).equalsIgnoreCase("Reel")) {

            return;

        }

        SlotMachine machine = plugin.getMachine(sign.getLine(2));

        if (machine == null) {

            return;

        }

        if (machine.removeReelLocation(sign.getBlock().getLocation())) {

            MCCUtil.tell(event.getPlayer(), "Reel removed.");

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {

        for (SlotMachine machine : plugin.getMachines()) {

            machine.passChunkUnload(event.getChunk());

        }
    }
}
