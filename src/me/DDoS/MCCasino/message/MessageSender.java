package me.DDoS.MCCasino.message;

import me.DDoS.MCCasino.MCCasino;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author DDoS
 */
public class MessageSender {

    private final String mainString;
    private final MessageProperty msgProp;
    private final boolean excludeWinner;
    private final int radius;

    public MessageSender(String mainString, MessageProperty msgProp, boolean excludeWinner, int radius) {

        this.mainString = colorize(mainString);
        this.msgProp = msgProp;
        this.excludeWinner = excludeWinner;
        this.radius = radius;

    }

    public void sendAlert(Player player, int amount, Material item) {

        final String msg = getString(player.getDisplayName(), amount, item);

        switch (msgProp) {

            case PLAYER:

                player.sendMessage(msg);
                break;

            case PLAYERS_NEARBY:

                for (Player p : player.getWorld().getPlayers()) {

                    if (p.getLocation().distanceSquared(player.getLocation()) <= radius) {

                        if (excludeWinner && p.equals(player)) {

                            continue;

                        }

                        p.sendMessage(msg);

                    }
                }

                break;

            case WORLD:

                for (Player p : player.getWorld().getPlayers()) {

                    if (excludeWinner && p.equals(player)) {

                        continue;

                    }

                    p.sendMessage(msg);

                }

                break;

            case SERVER:

                Bukkit.broadcastMessage(msg);
                break;

            case CONSOLE_ONLY:

                break;

        }

        MCCasino.log.info(msg);

    }

    private String getString(String playerName, int amount, Material item) {

        String finalString;

        finalString = mainString.replaceAll("\\Q$name$\\E", colorize(playerName));
        finalString = finalString.replaceAll("\\Q$amount$\\E", amount + "");

        if (item != null) {

            finalString = finalString.replaceAll("\\Q$item$\\E", item.name().toLowerCase());

        }

        return finalString;

    }

    private String colorize(String txt) {

        return txt.replaceAll("\\Q$\\E([0-9[a-fA-F]])\\Q$\\E", "\u00A7$1");

    }
}
