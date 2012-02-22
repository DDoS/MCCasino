package me.DDoS.MCCasino.slotmachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.DDoS.MCCasino.bet.Bet;
import me.DDoS.MCCasino.bet.BetProvider;
import me.DDoS.MCCasino.util.DropCleaner;
import me.DDoS.MCCasino.util.MCCUtil;
import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.message.MessageSender;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author DDoS
 */
public class SlotMachine {

    private final List<Reel> reels;
    private List<Location> reelLocations;
    private final List<Reward> rewards;
    private final List<Item> itemsToRemove = new ArrayList<Item>();
    private final BetProvider betProvider;
    private final MessageSender msgSender;
    private boolean active;
    private final MCCasino plugin;

    public SlotMachine(List<Location> reelLocations, List<Reel> reels, List<Reward> rewards, BetProvider betHandler,
            MessageSender msgSender, boolean active, MCCasino plugin) {

        this.reels = reels;
        this.reelLocations = reelLocations;
        this.rewards = rewards;
        this.betProvider = betHandler;
        this.msgSender = msgSender;
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

        if (areItemsInChunk(chunk)) {

            clearItems();
            
        }      
    }
    
    public boolean hasItem(Item item) {
        
        if (itemsToRemove.isEmpty()) {

            return false;

        }
        
        for (Item item2 : itemsToRemove) {
            
            if (item2.equals(item)) {
                
                return true;
                
            }        
        }
        
        return false;
        
    }

    private boolean areItemsInChunk(Chunk chunk) {

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

        for (Reel reel : reels) {

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

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DropCleaner(this), 100L);

        return results;

    }

    public void testRun(Player player) {

        if (!active) {

            MCCUtil.tell(player, "This machine is not active.");
            return;

        }

        active = false;
        List<Integer> results = spinReels();

        for (Reward reward : rewards) {

            int multiplier = reward.get(results);

            if (multiplier > 0) {

                MCCUtil.tell(player, "You won " + multiplier + " time(s) your bet.");
                msgSender.sendAlert(player, 0, Material.AIR);
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

        Bet bet = betProvider.getBet(player);

        if (bet == null) {

            return;

        }

        active = false;
        List<Integer> results = spinReels();

        for (Reward reward : rewards) {

            int multiplier = reward.get(results);

            if (multiplier > 0) {

                bet.applyMultiplier(multiplier);
                MCCUtil.tell(player, "You won " + multiplier + " time(s) your bet.");
                msgSender.sendAlert(player, bet.getAmount(), bet.getMaterial());
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
