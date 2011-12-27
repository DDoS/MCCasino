package me.DDoS.MCCasino.listener;

import me.DDoS.MCCasino.MCCasino;
import me.DDoS.MCCasino.slotmachine.MCCSlotMachine;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

/**
 *
 * @author DDoS
 */
public class MCCWorldListener extends WorldListener {

    private MCCasino plugin;

    public MCCWorldListener(MCCasino plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {

        for (MCCSlotMachine machine : plugin.getMachines()) {

            machine.passChunkUnload(event.getChunk());

        }
    }
}
