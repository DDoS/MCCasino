package me.DDoS.MCCasino.util;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

/**
 *
 * @author DDoS
 */
public class SerializableLocation implements Serializable {
    
    private static final long serialVersionUID = -1330836964180902739L;
    //
    private final double x;
    private final double y;
    private final double z;
    private final String worldName;
    
    public SerializableLocation(double x, double y, double z, String worldName) {
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    
    }
    
    public Location getLocation(Server server) {
        
        World world = server.getWorld(worldName);
        
        if (world != null) {
            
            return new Location(world, x, y, z);
        
        } else {
        
            return null;
        
        }
    }
}
