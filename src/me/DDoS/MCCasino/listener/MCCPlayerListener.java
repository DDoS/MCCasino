package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.permissions.MCCPermissions;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import java.util.Map.Entry;
import me.DDoS.MCCasino.MCCasino;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 *
 * @author DDoS
 */
public class MCCPlayerListener extends PlayerListener {

    private MCCasino plugin;

    public MCCPlayerListener(MCCasino instance) {

        plugin = instance;

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if (checkForSign(event.getClickedBlock())) {

                Sign sign = (Sign) event.getClickedBlock().getState();

                if (sign.getLine(0).equalsIgnoreCase("[MCCasino]")) {

                    if (sign.getLine(1).equalsIgnoreCase("Slot Machine")
                            && MCCasino.permissions.hasPermission(event.getPlayer(), MCCPermissions.USE.getPermissionString())) {

                        MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

                        if (machine != null) {

                            event.setCancelled(true);
                            machine.run(event.getPlayer());
                            return;

                        }

                        event.setCancelled(true);
                        MCCUtil.tell(event.getPlayer(), "This slot machine doesn't exist.");
                        return;

                    }

                    if (sign.getLine(1).equalsIgnoreCase("Reel")
                            && MCCasino.permissions.hasPermission(event.getPlayer(), MCCPermissions.SETUP.getPermissionString())) {

                        MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

                        if (machine != null) {

                            if (!machine.checkReels()) {

                                boolean success = machine.addReelLocation(sign.getBlock().getLocation());
                                event.setCancelled(true);
                                if (success) {
                                    MCCUtil.tell(event.getPlayer(), "Reel sign added to machine.");
                                }
                                if (!success) {
                                    MCCUtil.tell(event.getPlayer(), "This sign has already been added.");
                                }
                                return;

                            }

                            event.setCancelled(true);
                            MCCUtil.tell(event.getPlayer(), "This machine already has all of it signs.");
                            return;

                        }

                        event.setCancelled(true);
                        MCCUtil.tell(event.getPlayer(), "This slot machine doesn't exist.");
                        return;

                    }
                }
            }
        }
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        for (Entry<String, MCCSlotMachine> entry : plugin.getMachines()) {

            entry.getValue().checkItem(event.getItem());
            
        }
    }

    private boolean checkForSign(Block block) {

        switch (block.getType()) {

            case WALL_SIGN:
                return true;

            default:
                return false;

        }
    }
}