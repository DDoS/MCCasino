package me.DDoS.MCCasino;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import me.DDoS.MCCasino.listener.MCCListener;
import me.DDoS.MCCasino.permission.Permission;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.slotmachine.SlotMachine;
import me.DDoS.MCCasino.permission.Permissions;
import me.DDoS.MCCasino.permission.PermissionsHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author DDoS
 */
public class MCCasino extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    //
    private final Map<String, SlotMachine> machines = new HashMap<String, SlotMachine>();
    //
    private Permissions permissions;

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new MCCListener(this), this);

        permissions = new PermissionsHandler(this).getPermissions();
        new Loader(this, getConfig()).loadSlotMachines();

        log.info("[MCCasino] Plugin enabled, v" + getDescription().getVersion() + ", by DDoS.");

    }

    @Override
    public void onDisable() {

        for (SlotMachine slotMachine : machines.values()) {

            slotMachine.clearItems();

        }

        new Loader(this).saveMachines();
        log.info("[MCCasino] Plugin disabled, v" + getDescription().getVersion() + ", by DDoS.");

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {

            sender.sendMessage("This command can only be used in-game.");
            return true;

        }

        Player player = (Player) sender;

        if (!permissions.hasPermission(player, Permission.SETUP.getNodeString())) {

            player.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
            return true;

        }

        if (args.length > 0) {

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

    public SlotMachine getMachine(String name) {

        return machines.get(name);

    }

    public Set<Entry<String, SlotMachine>> getMachineEntries() {

        return machines.entrySet();

    }

    public Collection<SlotMachine> getMachines() {

        return machines.values();

    }

    public void addMachine(String name, SlotMachine machine) {

        if (!machines.containsKey(name)) {

            machines.put(name, machine);

        }
    }

    public boolean hasEconomy() {

        return getServer().getPluginManager().getPlugin("Vault") != null;

    }
    
    public Permissions getPermissions() {
        
        return permissions;
        
    }
}
