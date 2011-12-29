package me.DDoS.MCCasino.slotmachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.DDoS.MCCasino.bet.MCCBet;
import me.DDoS.MCCasino.bet.MCCBetProvider;
import me.DDoS.MCCasino.util.MCCDropCleaner;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.MCCasino;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author DDoS
 */
public class MCCSlotMachine {

    private List<MCCReel> reels;
    private List<Location> reelLocations;
    private List<MCCReward> rewards;
    private final List<Item> itemsToRemove = new ArrayList<Item>();
    private MCCBetProvider betProvider;
    private boolean active;
    private MCCasino plugin;

    public MCCSlotMachine(List<Location> reelLocations, List<MCCReel> reels, List<MCCReward> rewards, MCCBetProvider betHandler,
            boolean active, MCCasino plugin) {

        this.reels = reels;
        this.reelLocations = reelLocations;
        this.rewards = rewards;
        this.betProvider = betHandler;
        this.active = active;
        this.plugin = plugin;

    }

    private void checkReels() {

        if (!hasAllOfItsReels()) {

            active = false;

        } else {

            orderReels();
            active = true;

        }
    }

    public boolean hasAllOfItsReels() {

        return reels.size() <= reelLocations.size();

    }

    public boolean addReelLocation(Location loc) {

        if (!reelLocations.contains(loc)) {

            reelLocations.add(loc);
            checkReels();
            return true;

        }

        return false;

    }

    public boolean removeReelLocation(Location loc) {

        if (reelLocations.contains(loc)) {

            reelLocations.remove(loc);
            checkReels();
            return true;

        }

        return false;

    }

    public List<Location> getReels() {

        return reelLocations;

    }

    public void setActive(boolean active) {

        this.active = active;

    }

    public void clearItems() {

        for (Item item : itemsToRemove) {

            item.remove();

        }
        
        itemsToRemove.clear();
        
    }

    public void passChunkUnload(Chunk chunk) {

        if (itemsToRemove.isEmpty()) {

            return;

        }

        if (itemsInChunk(chunk)) {

            clearItems();
            
        }      
    }

    private boolean itemsInChunk(Chunk chunk) {

        for (Item item : itemsToRemove) {

            Location loc = item.getLocation();
            int x = loc.getBlockX() >> 4;
            int z = loc.getBlockZ() >> 4;

            if (x == chunk.getX() && z == chunk.getZ()) {

                return true;

            }
        }

        return false;

    }

    private List<Integer> spinReels() {

        int i = 0;
        List<Integer> results = new ArrayList<Integer>();

        for (MCCReel reel : reels) {

            Location loc1 = reelLocations.get(i);
            ItemStack item = reel.getRandomItem();

            byte data = getDataFromSign(loc1);
            Location loc2 = getOffsetLocation(data, loc1);

            Item droppedItem = loc2.getWorld().dropItem(loc2, item);

            Vector vect = getVelocity(data);
            droppedItem.setVelocity(vect);

            results.add(item.getTypeId());
            itemsToRemove.add(droppedItem);

            i++;

        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MCCDropCleaner(this), 100L);

        return results;

    }

    public void testRun(Player player) {

        if (!active) {

            MCCUtil.tell(player, "This machine is not active.");
            return;

        }

        active = false;
        List<Integer> results = spinReels();

        for (MCCReward reward : rewards) {

            int multiplier = reward.get(results);

            if (multiplier > 0) {

                MCCUtil.tell(player, "You won " + multiplier + " time(s) your bet.");
                return;

            }
        }

        MCCUtil.tell(player, "You lost!");

    }

    public void run(Player player) {

        if (!active) {

            MCCUtil.tell(player, "This machine is not active.");
            return;

        }

        MCCBet bet = betProvider.getBet(player);

        if (bet == null) {

            return;

        }

        active = false;
        List<Integer> results = spinReels();

        for (MCCReward reward : rewards) {

            int multiplier = reward.get(results);

            if (multiplier > 0) {

                bet.applyMultiplier(multiplier);
                MCCUtil.tell(player, "You won " + multiplier + " time(s) your bet.");
                bet.giveReward(player);
                return;

            }
        }

        MCCUtil.tell(player, "You lost!");

    }

    private byte getDataFromSign(Location loc) {

        if (checkForSign(loc)) {

            return ((Sign) loc.getBlock().getState()).getData().getData();

        }

        return 0x0;

    }

    private Vector getVelocity(byte d) {

        switch (d) {

            case 0x2://North, z goes down
                return new Vector(0, 0, -0.2);

            case 0x3://South, z goes up
                return new Vector(0, 0, 0.2);

            case 0x4://West, x goes down
                return new Vector(-0.2, 0, 0);

            case 0x5://East, x goes up
                return new Vector(0.2, 0, 0);

            default:
                return new Vector(0, 0, 0);

        }
    }

    private Location getOffsetLocation(byte d, Location loc) {

        Location loc2 = loc.clone();

        switch (d) {

            case 0x2://North, add to x, add to z
                return loc2.add(0.5, 0, 1);

            case 0x3://South, add to x
                return loc2.add(0.5, 0, 0);

            case 0x4://West, add to z, add to x
                return loc2.add(1, 0, 0.5);

            case 0x5://East, add to z
                return loc2.add(0, 0, 0.5);

            default:
                return loc2;

        }
    }

    private boolean checkForSign(Location loc) {

        switch (loc.getBlock().getType()) {

            case WALL_SIGN:
                return true;

            default:
                return false;

        }
    }

    private void orderReels() {

        final Location[] tl = new Location[reels.size()];

        for (Location rl : reelLocations) {

            if (!checkForSign(rl)) {

                return;

            }

            Sign sign = (Sign) rl.getBlock().getState();

            if (!isInt(sign.getLine(3))) {

                return;

            }

            int i = (Integer.parseInt(sign.getLine(3)) - 1);

            if (i < 0 || i > (tl.length - 1)) {

                return;

            }

            if (tl[i] != null) {

                return;

            }

            tl[i] = rl;

        }

        reelLocations = Arrays.asList(tl);

    }

    private boolean isInt(String s) {

        try {

            Integer.parseInt(s);
            return true;

        } catch (NumberFormatException ex) {

            return false;

        }
    }
}
