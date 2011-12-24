package me.DDoS.MCCasino.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MCCUtil {
 
    public static void tell(Player player, String msg) {
        
        player.sendMessage(ChatColor.GOLD + "[MCCasino] " + ChatColor.GRAY + msg);
        
    }  
}
