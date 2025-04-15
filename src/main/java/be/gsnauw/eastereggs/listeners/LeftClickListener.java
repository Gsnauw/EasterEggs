package be.gsnauw.eastereggs.listeners;

import be.gsnauw.eastereggs.managers.EggManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LeftClickListener implements Listener {
    EggManager eggManager = EggManager.getInstance();

    @EventHandler
    public void BlockBreak(BlockBreakEvent e) {
        Material blockMaterial = e.getBlock().getType();
        Location blockLoc = e.getBlock().getLocation();
        String eggId = eggManager.getEgg(blockLoc);
        if (eggId == null) {
            return;
        }
        if (!blockMaterial.equals(Material.PLAYER_HEAD)) {
            return;
        }
        e.setCancelled(true);
    }
}
