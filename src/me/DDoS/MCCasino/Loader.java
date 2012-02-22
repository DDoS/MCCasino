package me.DDoS.MCCasino;

import me.DDoS.MCCasino.util.SerializableLocation;
import me.DDoS.MCCasino.bet.BetProvider;
import me.DDoS.MCCasino.slotmachine.Reel;
import me.DDoS.MCCasino.slotmachine.ReelValue;
import me.DDoS.MCCasino.slotmachine.Reward;
import me.DDoS.MCCasino.slotmachine.SlotMachine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.DDoS.MCCasino.bet.ItemBetProvider;
import me.DDoS.MCCasino.bet.MoneyBetProvider;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
@SuppressWarnings("unchecked")
public class Loader {

    private Map<String, List<SerializableLocation>> machines;
    private MCCasino plugin;
    private FileConfiguration config;
    
    public Loader(MCCasino plugin, FileConfiguration config) {
        
        this.plugin = plugin;
        this.config = config;
        
    }
    
    public Loader(MCCasino plugin) {
        
        this.plugin = plugin;
        
    }

    public void loadSlotMachines() {

        loadSlotMachinesFile();

        try {

            config.load("plugins/MCCasino/config.yml");

        } catch (Exception e) {

            MCCasino.log.info("[MCCasino] Couldn't load the config.");
            MCCasino.log.info("[MCCasino] Error: " + e.getMessage());
            
        }

        Set<String> machineNames = config.getConfigurationSection("Machines").getKeys(false);

        for (String machineName : machineNames) {

            List<Location> signs = getMachineSigns(machineName);
            List<Reel> reels = loadReels(machineName);
            List<Reward> rewards = loadRewards(machineName);
            BetProvider betHandler = loadBetProvider(machineName);

            boolean active = true;

            if (signs == null || signs.size() < reels.size()) {

                active = false;

            }

            MCCasino.log.info("[MCCasino] Loaded slot machine: " + machineName);
            plugin.addMachine(machineName, new SlotMachine(signs, reels, rewards, betHandler, active, plugin));

        }
    }

    public List<Reel> loadReels(String machineName) {

        int numOfReels = config.getConfigurationSection("Machines." + machineName + ".reels").getKeys(false).size();
        List<Reel> reels = new ArrayList<Reel>();

        for (int i2 = 0; i2 < numOfReels; i2++) {

            List<String> reelValues = config.getStringList("Machines." + machineName + ".reels." + (i2 + 1));
            List<ReelValue> rvs = new ArrayList<ReelValue>();

            for (String reelValue : reelValues) {

                String[] values = reelValue.split("-");
                rvs.add(new ReelValue(new ItemStack(Integer.parseInt(values[0]), 1), Integer.parseInt(values[1])));

            }

            if (!rvs.isEmpty()) {

                reels.add(new Reel(rvs));

            }
        }

        return reels;

    }

    private List<Reward> loadRewards(String machineName) {

        List<String> rewards = config.getStringList("Machines." + machineName + ".rewards");
        List<Reward> rewardsList = new ArrayList<Reward>();

        for (String reward : rewards) {

            String[] rewardSplitted = reward.split(":");
            String[] resultsString = rewardSplitted[0].split(",");
            int multiplier = Integer.parseInt(rewardSplitted[1]);
            List<Integer> results = new ArrayList<Integer>();

            for (String resultString : resultsString) {

                int ID = (resultString.equalsIgnoreCase("x")) ? -1 : Integer.parseInt(resultString);
                results.add(ID);

            }

            rewardsList.add(new Reward(results, multiplier));

        }

        return rewardsList;

    }

    private BetProvider loadBetProvider(String machineName) {

        if (config.getBoolean("Machines." + machineName + ".economy.use_economy") && plugin.hasEconomy()) {
            
            return new MoneyBetProvider(config.getInt("Machines." + machineName + ".economy.cost"),
                    plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider());

        } else {

            List<ItemStack> limitedItems = new ArrayList<ItemStack>();
            
            if (config.getBoolean("Machines." + machineName + ".bet_limits.enabled")) {

                List<String> limits = config.getStringList("Machines." + machineName + ".bet_limits.allowed");

                for (String limit : limits) {

                    String[] s1 = limit.split("-");
                    limitedItems.add(new ItemStack(Integer.parseInt(s1[0]), Integer.parseInt(s1[1])));

                }
            }

            return new ItemBetProvider(limitedItems);

        }
    }

    private void loadSlotMachinesFile() {

        checkSlotMachineFile();

        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/MCCasino/slotMachines.dat"));
            Object dispensersLoad = ois.readObject();
            machines = (HashMap<String, List<SerializableLocation>>) dispensersLoad;

        } catch (Exception e) {

            MCCasino.log.info("[MCCasino] Couldn't load the slot machine file.");
            MCCasino.log.info("[MCCasino] Error: " + e.getMessage());
            machines = new HashMap<String, List<SerializableLocation>>();

        }
    }

    private List<Location> getMachineSigns(String machineName) {

        List<SerializableLocation> sLocs = machines.get(machineName);

        if (sLocs == null) {

            return new ArrayList<Location>();

        }

        List<Location> locs = new ArrayList<Location>();

        for (SerializableLocation sLoc : sLocs) {

            Location location = sLoc.getLocation(plugin.getServer());
            locs.add(location);

        }

        return locs;

    }

    private void checkSlotMachineFile() {

        File dipensersFile = new File("plugins/MCCasino/slotMachines.dat");

        if (!dipensersFile.exists()) {

            try {

                dipensersFile.createNewFile();

            } catch (Exception e) {

                MCCasino.log.info("[MCCasino] Couldn't create the slot machine file.");
                MCCasino.log.info("[MCCasino] Error: " + e.getMessage());
                
            }
        }
    }

    public void saveMachines() {

        Map<String, List<SerializableLocation>> signs = new HashMap<String, List<SerializableLocation>>();

        for (Entry<String, SlotMachine> entry : plugin.getMachineEntries()) {

            List<Location> locs = entry.getValue().getReels();
            List<SerializableLocation> fLocs = new ArrayList<SerializableLocation>();

            for (Location loc : locs) {

                fLocs.add(new SerializableLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));

            }

            signs.put(entry.getKey(), fLocs);

        }

        try {

            ObjectOutputStream hashfile = new ObjectOutputStream(new FileOutputStream("plugins/MCCasino/slotMachines.dat"));
            hashfile.writeObject(signs);
            hashfile.flush();
            hashfile.close();
            MCCasino.log.info("[MCCasino] Machines saved.");

        } catch (Exception e) {

            MCCasino.log.info("[MCCasino] Couldn't save the slot machine file.");
            MCCasino.log.info("[MCCasino] Error: " + e.getMessage());

        }
    }
}
