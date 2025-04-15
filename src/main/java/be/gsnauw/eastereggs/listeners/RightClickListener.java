package be.gsnauw.eastereggs.listeners;

import be.gsnauw.eastereggs.EasterEggs;
import be.gsnauw.eastereggs.managers.ConfigManager;
import be.gsnauw.eastereggs.managers.EggManager;
import be.gsnauw.eastereggs.managers.RewardsManager;
import be.gsnauw.eastereggs.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RightClickListener implements Listener {
    ConfigManager mainConfig = EasterEggs.getInstance().getMainConfig();
    ConfigManager usersConfig = EasterEggs.getInstance().getUsersConfig();
    EggManager eggManager = EggManager.getInstance();
    RewardsManager rewardsManager = RewardsManager.getInstance();
    ChatUtil chat = EasterEggs.getInstance().getChatUtil();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.OFF_HAND) return;
        Location clickedLoc = Objects.requireNonNull(e.getClickedBlock()).getLocation();
        Block clickedBlock = e.getClickedBlock();
        Material clickedMaterial = clickedBlock.getType();
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String eggId = eggManager.getEgg(clickedLoc);

        if (eggId == null) {
            return;
        }
        if (!clickedMaterial.equals(Material.PLAYER_HEAD)) {
            p.sendMessage(chat.format("&cEr is een probleem met deze easteregg. Meld dit bij staff. ID: " + eggId));
            return;
        }
        List<String> userEggs = eggManager.collectedEggs(p);
        if (userEggs.contains(eggId)) {
            p.sendMessage(chat.prefix(mainConfig.getString("messages.egg-already-claimed")));
            return;
        }

        userEggs.add(eggId);
        usersConfig.set("users." + uuid, userEggs);
        usersConfig.save();
        rewardsManager.giveRewards(p, eggId);
    }
}