package me.DDoS.MCCasino;

import me.DDoS.MCCasino.util.MCCSerializableLocation;
import me.DDoS.MCCasino.bet.MCCBetProvider;
import me.DDoS.MCCasino.slotmachine.MCCReel;
import me.DDoS.MCCasino.slotmachine.MCCReelValue;
import me.DDoS.MCCasino.slotmachine.MCCReward;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.DDoS.MCCasino.bet.MCCItemBetProvider;
import me.DDoS.MCCasino.bet.MCCMoneyBetProvider;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author DDoS
 */
@SuppressWarnings("unchecked")
public class MCCLoader {

    private Map<String, List<MCCSerializableLocation>> machines = new HashMap<String, List<MCCSerializableLocation>>();

    public void loadSlotMachines(FileConfiguration config, MCCasino plugin) {

        loadSlotMachinesFile();

        try {

            config.load("plugins/MCCasino/config.yml");

        } catch (IOException ex) {

            MCCasino.log.info("[MCCasino] Couldn't load the config: " + ex.getMessage());

        } catch (InvalidConfigurationException ex) {

            MCCasino.log.info("[MCCasino] Couldn't load the config: " + ex.getMessage());

        }

        Set<String> machineNames = config.getConfigurationSection("Machines").getKeys(false);

        for (String machineName : machineNames) {

            List<Location> signs = getMachineSigns(machineName, plugin.getServer());
            List<MCCReel> reels = loadReels(config, machineName);
            List<MCCReward> rewards = loadRewards(config, machineName);
            MCCBetProvider betHandler = loadBetProvider(config, machineName);

            boolean active = true;

            if (signs == null || signs.size() < reels.size()) {

                active = false;

            }

            MCCasino.log.info("[MCCasino] Loaded slot machine: " + machineName);
            plugin.addMachine(machineName, new MCCSlotMachine(signs, reels, rewards, betHandler, active, plugin));

        }
    }

    public List<MCCReel> loadReels(FileConfiguration config, String machineName) {

        int numOfReels = config.getConfigurationSection("Machines." + machineName + ".reels").getKeys(false).size();
        List<MCCReel> reels = new ArrayList<MCCReel>();

        for (int i2 = 0; i2 < numOfReels; i2++) {

            List<String> reelValues = config.getList("Machines." + machineName + ".reels." + (i2 + 1));
            List<MCCReelValue> rvs = new ArrayList<MCCReelValue>();

            for (String reelValue : reelValues) {

                String[] values = reelValue.split("-");
                rvs.add(new MCCReelValue(new ItemStack(Integer.parseInt(values[0]), 1), Integer.parseInt(values[1])));

            }

            if (!rvs.isEmpty()) {

                reels.add(new MCCReel(rvs));

            }
        }

        return reels;

    }

    private List<MCCReward> loadRewards(FileConfiguration config, String machineName) {

        List<String> rewards = config.getList("Machines." + machineName + ".rewards");
        List<MCCReward> rewardsList = new ArrayList<MCCReward>();

        for (String reward : rewards) {

            String[] rewardSplitted = reward.split(":");
            String[] resultsString = rewardSplitted[0].split(",");
            int multiplier = Integer.parseInt(rewardSplitted[1]);
            List<Integer> results = new ArrayList<Integer>();

            for (String resultString : resultsString) {

                int ID = (resultString.equalsIgnoreCase("x")) ? -1 : Integer.parseInt(resultString);
                results.add(ID);

            }

            rewardsList.add(new MCCReward(results, multiplier));

        }

        return rewardsList;

    }

    private MCCBetProvider loadBetProvider(FileConfiguration config, String machineName) {

        if (config.getBoolean("Machines." + machineName + ".economy.use_economy") && MCCasino.economy != null) {

            return new MCCMoneyBetProvider(config.getInt("Machines." + machineName + ".economy.cost"));

        } else {

            List<ItemStack> limitedItems = new ArrayList<ItemStack>();
            
            if (config.getBoolean("Machines." + machineName + ".bet_limits.enabled")) {

                List<String> limits = config.getList("Machines." + machineName + ".bet_limits.allowed");

                for (String limit : limits) {

                    String[] s1 = limit.split("-");
                    limitedItems.add(new ItemStack(Integer.parseInt(s1[0]), Integer.parseInt(s1[1])));

                }
            }

            return new MCCItemBetProvider(limitedItems);

        }
    }

    public void loadSlotMachinesFile() {

        checkSlotMachineFile();

        try {

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/MCCasino/slotMachines.dat"));
            Object dispensersLoad = ois.readObject();
            machines = (HashMap<String, List<MCCSerializableLocation>>) dispensersLoad;

        } catch (Exception e) {

            MCCasino.log.info("[MCCasino] Couldn't load the slot machine file.");
            machines = new HashMap<String, List<MCCSerializableLocation>>();

        }
    }

    private List<Location> getMachineSigns(String machineName, Server server) {

        List<MCCSerializableLocation> sLocs = machines.get(machineName);

        if (sLocs == null) {

            return new ArrayList<Location>();

        }

        List<Location> locs = new ArrayList<Location>();

        for (MCCSerializableLocation sLoc : sLocs) {

            Location location = sLoc.getLocation(server);
            locs.add(location);

        }

        return locs;

    }

    public void checkSlotMachineFile() {

        File dipensersFile = new File("plugins/MCCasino/slotMachines.dat");

        if (!dipensersFile.exists()) {

            try {

                dipensersFile.createNewFile();

            } catch (Exception e2) {

                MCCasino.log.info("[MCCasino] Error when creating slot machine file.");
            }
        }
    }

    public void saveMachines(MCCasino plugin) {

        Map<String, List<MCCSerializableLocation>> signs = new HashMap<String, List<MCCSerializableLocation>>();

        for (Entry<String, MCCSlotMachine> entry : plugin.getMachines()) {

            List<Location> locs = entry.getValue().getReels();
            List<MCCSerializableLocation> fLocs = new ArrayList<MCCSerializableLocation>();

            for (Location loc : locs) {

                fLocs.add(new MCCSerializableLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));

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

            MCCasino.log.info("[MCCasino] Error when saving slot machines.");

        }
    }
}