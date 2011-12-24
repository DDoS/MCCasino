package me.DDoS.MCCasino;

import java.util.Collection;
import me.DDoS.MCCasino.listener.MCCPlayerListener;
import me.DDoS.MCCasino.listener.MCCBlockListener;
import me.DDoS.MCCasino.permissions.MCCPermissions;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import me.DDoS.MCCasino.permissions.Permissions;
import me.DDoS.MCCasino.permissions.PermissionsHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author DDoS
 */
public class MCCasino extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    //
    private final Map<String, MCCSlotMachine> machines = new HashMap<String, MCCSlotMachine>();
    //
    private final MCCPlayerListener playerListener = new MCCPlayerListener(this);
    private final MCCBlockListener blockListener = new MCCBlockListener(this);
    //
    public static Permissions permissions;
    //
    public static Economy economy;

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Normal, this);

        permissions = new PermissionsHandler(this).getPermissions();
        linkVaultEconomy();
        new MCCLoader().loadSlotMachines(getConfig(), this);

        log.info("[MCCasino] Plugin enabled, v0.2, by DDoS.");

    }

    @Override
    public void onDisable() {

        for (MCCSlotMachine slotMachine : machines.values()) {

            slotMachine.clearItems();

        }

        new MCCLoader().saveMachines(this);
        log.info("[MCCasino] Plugin disabled, v0.2, by DDoS.");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {

            sender.sendMessage("This command can only be used in-game.");
            return true;

        }

        Player player = (Player) sender;

        if (!permissions.hasPermission(player, MCCPermissions.SETUP.getPermissionString())) {

            player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
            return true;

        }
        
        if (args.length != 1) {
            
            return false;
            
        }

        if (!machines.containsKey(args[0])) {

            MCCUtil.tell(player, "This machine does not exist, or is not loaded.");
            return true;

        }

        if (command.getName().equalsIgnoreCase("mccdel")) {

            machines.remove(args[0]);
            MCCUtil.tell(player, "Machine unloaded and sign locations deleted.");
            return true;

        }

        if (command.getName().equalsIgnoreCase("mcctest")) {

            machines.get(args[0]).testRun(player);
            MCCUtil.tell(player, "Test complete.");
            return true;

        }

        if (command.getName().equalsIgnoreCase("mccforceactive")) {

            machines.get(args[0]).setActive(true);
            MCCUtil.tell(player, "This slot machine is now active.");
            MCCUtil.tell(player, ChatColor.DARK_RED + "WARNING, running a normally inactive slot machine may cause errors!");
            return true;

        }

        return false;

    }

    public MCCSlotMachine getMachine(String name) {

        return machines.get(name);

    }

    public Set<Entry<String, MCCSlotMachine>> getMachineEntries() {

        return machines.entrySet();

    }
    
    public  Collection<MCCSlotMachine> getMachines() {

        return machines.values();

    }

    public void addMachine(String name, MCCSlotMachine machine) {

        if (!machines.containsKey(name)) {

            machines.put(name, machine);

        }
    }

    private void linkVaultEconomy() {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (economyProvider != null) {

            economy = economyProvider.getProvider();
            return;

        } else {

            log.info("[MCCasino] Couldn't connect to Vault. Eonomy not available.");

        }
    }
}
