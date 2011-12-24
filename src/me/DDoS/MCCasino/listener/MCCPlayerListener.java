package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.permissions.MCCPermissions;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
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

    public MCCPlayerListener(MCCasino plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {

            return;

        }

        if (!checkForSign(event.getClickedBlock())) {

            return;

        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLine(0).equalsIgnoreCase("[MCCasino]")) {

            return;

        }

        event.setCancelled(true);

        if (sign.getLine(1).equalsIgnoreCase("Slot Machine")
                && MCCasino.permissions.hasPermission(event.getPlayer(), MCCPermissions.USE.getPermissionString())) {

            MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

            if (machine != null) {

                machine.run(event.getPlayer());
                return;

            } else {

                MCCUtil.tell(event.getPlayer(), "This slot machine doesn't exist.");
                return;

            }
        }

        if (sign.getLine(1).equalsIgnoreCase("Reel")
                && MCCasino.permissions.hasPermission(event.getPlayer(), MCCPermissions.SETUP.getPermissionString())) {

            MCCSlotMachine machine = plugin.getMachine(sign.getLine(2));

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

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        for (MCCSlotMachine machine : plugin.getMachines()) {

            if (machine.hasItem(event.getItem())) {

                event.setCancelled(true);
                return;

            }
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
