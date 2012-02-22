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
import me.DDoS.MCCasino.message.MessageProperty;
import me.DDoS.MCCasino.message.MessageSender;
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
    private final MCCasino plugin;
    private final FileConfiguration config;

    public Loader(MCCasino plugin, FileConfiguration config) {

        this.plugin = plugin;
        this.config = config;

    }

    public Loader(MCCasino plugin) {

        this.plugin = plugin;
        config = null;

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

            final List<Location> signs = getMachineSigns(machineName);
            final List<Reel> reels = loadReels(machineName);
            final List<Reward> rewards = loadRewards(machineName);
            final BetProvider betHandler = loadBetProvider(machineName);
            final MessageSender msgSender = loadMessageSender(machineName);

            boolean active = true;

            if (signs == null || signs.size() < reels.size()) {

                active = false;

            }

            MCCasino.log.info("[MCCasino] Loaded slot machine: " + machineName);
            plugin.addMachine(machineName, new SlotMachine(signs, reels, rewards, betHandler, msgSender, active, plugin));

        }
    }

    public List<Reel> loadReels(String machineName) {

        final int numOfReels = config.getConfigurationSection("Machines." + machineName + ".reels").getKeys(false).size();
        final List<Reel> reels = new ArrayList<Reel>();

        for (int i2 = 0; i2 < numOfReels; i2++) {

            final List<String> reelValues = config.getStringList("Machines." + machineName + ".reels." + (i2 + 1));
            final List<ReelValue> rvs = new ArrayList<ReelValue>();

            for (String reelValue : reelValues) {

                final String[] values = reelValue.split("-");
                rvs.add(new ReelValue(new ItemStack(Integer.parseInt(values[0]), 1), Integer.parseInt(values[1])));

            }

            if (!rvs.isEmpty()) {

                reels.add(new Reel(rvs));

            }
        }

        return reels;

    }

    private List<Reward> loadRewards(String machineName) {

        final List<String> rewards = config.getStringList("Machines." + machineName + ".rewards");
        final List<Reward> rewardsList = new ArrayList<Reward>();

        for (String reward : rewards) {

            final String[] rewardSplitted = reward.split(":");
            final String[] resultsString = rewardSplitted[0].split(",");
            final int multiplier = Integer.parseInt(rewardSplitted[1]);
            final List<Integer> results = new ArrayList<Integer>();

            for (String resultString : resultsString) {

                final int ID = (resultString.equalsIgnoreCase("x")) ? -1 : Integer.parseInt(resultString);
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

            final List<ItemStack> limitedItems = new ArrayList<ItemStack>();

            if (config.getBoolean("Machines." + machineName + ".bet_limits.enabled")) {

                final List<String> limits = config.getStringList("Machines." + machineName + ".bet_limits.allowed");

                for (String limit : limits) {

                    final String[] s1 = limit.split("-");
                    limitedItems.add(new ItemStack(Integer.parseInt(s1[0]), Integer.parseInt(s1[1])));

                }
            }

            return new ItemBetProvider(limitedItems);

        }
    }

    private MessageSender loadMessageSender(String machineName) {

        final String msg = config.getString("Machines." + machineName + ".messaging.message");

        MessageProperty msgProp = MessageProperty.CONSOLE_ONLY;

        try {

            msgProp = MessageProperty.valueOf(config.getString("Machines." + machineName + ".messaging.send_to").toUpperCase());

        } catch (IllegalArgumentException iae) {

            MCCasino.log.info("[MCCasino] Invalid value for 'messaging.send_to' for machine '" + machineName + "'.");

        }

        final boolean excludeWinner = config.getBoolean("Machines." + machineName + ".messaging.exclude_winner");
        final int radius = config.getInt("Machines." + machineName + ".messaging.radius");

        return new MessageSender(msg, msgProp, excludeWinner, radius);

    }

    private void loadSlotMachinesFile() {

        checkSlotMachineFile();

        try {

            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/MCCasino/slotMachines.dat"));
            final Object dispensersLoad = ois.readObject();
            machines = (HashMap<String, List<SerializableLocation>>) dispensersLoad;

        } catch (Exception e) {

            MCCasino.log.info("[MCCasino] Couldn't load the slot machine file.");
            MCCasino.log.info("[MCCasino] Error: " + e.getMessage());
            machines = new HashMap<String, List<SerializableLocation>>();

        }
    }

    private List<Location> getMachineSigns(String machineName) {

        final List<SerializableLocation> sLocs = machines.get(machineName);

        if (sLocs == null) {

            return new ArrayList<Location>();

        }

        final List<Location> locs = new ArrayList<Location>();

        for (SerializableLocation sLoc : sLocs) {

            final Location location = sLoc.getLocation(plugin.getServer());
            locs.add(location);

        }

        return locs;

    }

    private void checkSlotMachineFile() {

        final File dipensersFile = new File("plugins/MCCasino/slotMachines.dat");

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

        final Map<String, List<SerializableLocation>> signs = new HashMap<String, List<SerializableLocation>>();

        for (Entry<String, SlotMachine> entry : plugin.getMachineEntries()) {

            final List<Location> locs = entry.getValue().getReels();
            final List<SerializableLocation> fLocs = new ArrayList<SerializableLocation>();

            for (Location loc : locs) {

                fLocs.add(new SerializableLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));

            }

            signs.put(entry.getKey(), fLocs);

        }

        try {

            final ObjectOutputStream hashfile = new ObjectOutputStream(new FileOutputStream("plugins/MCCasino/slotMachines.dat"));
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
