package me.DDoS.MCCasino.util;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author DDoS
 */
public class SerializableLocation implements Serializable {
    
    private static final long serialVersionUID = -1330836964180902739L;
    //
    private final int x;
    private final int y;
    private final int z;
    private final String worldName;
    
    public SerializableLocation(int x, int y, int z, String worldName) {
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    
    }
    
    public Location getLocation() {
        
        World world = Bukkit.getWorld(worldName);
        
        if (world != null) {
            
            return new Location(world, x, y, z);
        
        } else {
        
            return null;
        
        }
    }
}
