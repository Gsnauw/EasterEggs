package be.gsnauw.eastereggs.managers;

import be.gsnauw.eastereggs.EasterEggs;
import be.gsnauw.eastereggs.utils.SkullUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class EggManager {
    @Getter
    private static final EggManager instance = new EggManager();

    ConfigManager mainConfig = EasterEggs.getInstance().getMainConfig();
    ConfigManager eggsConfig = EasterEggs.getInstance().getEggsConfig();
    ConfigManager usersConfig = EasterEggs.getInstance().getUsersConfig();
    SkullUtil skullUtil = SkullUtil.getInstance();

    public void setEgg(Location loc) {
        int nextId = getNextId();
        String eggId = "easteregg" + nextId;

        eggsConfig.set("eggs." + eggId + ".world", loc.getWorld().getName());
        eggsConfig.set("eggs." + eggId + ".x", loc.getBlockX());
        eggsConfig.set("eggs." + eggId + ".y", loc.getBlockY());
        eggsConfig.set("eggs." + eggId + ".z", loc.getBlockZ());

        List<String> defaultRewards = new ArrayList<>();
        defaultRewards.add("ITEM:DIAMOND");
        defaultRewards.add("ECO:10");

        eggsConfig.set("eggs." + eggId + ".rewards", defaultRewards);
        skullUtil.placeCustomHead(loc, mainConfig.getString("skull-value"));
        eggsConfig.save();
    }

    public String getEgg(Location loc) {
        if (!eggsConfig.getConfig().contains("eggs")) return null;

        for (String key : Objects.requireNonNull(eggsConfig.getConfig().getConfigurationSection("eggs")).getKeys(false)) {
            String path = "eggs." + key;
            String world = eggsConfig.getString(path + ".world");
            int x = eggsConfig.getInt(path + ".x");
            int y = eggsConfig.getInt(path + ".y");
            int z = eggsConfig.getInt(path + ".z");

            if (loc.getWorld().getName().equals(world)
                    && loc.getBlockX() == x
                    && loc.getBlockY() == y
                    && loc.getBlockZ() == z) {
                return key;
            }
        }
        return null;
    }

    public Location getEggLoc(String eggId) {
        if (!eggsConfig.getConfig().contains("eggs." + eggId)) return null;

        String worldName = eggsConfig.getString("eggs." + eggId + ".world");
        int x = eggsConfig.getInt("eggs." + eggId + ".x");
        int y = eggsConfig.getInt("eggs." + eggId + ".y");
        int z = eggsConfig.getInt("eggs." + eggId + ".z");

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world, x, y, z);
    }

    public void removeEgg(String eggId) {
        if (checkEgg(eggId)) {
            return;
        }
        Location loc = getEggLoc(eggId);
        loc.getBlock().setType(Material.AIR);
        eggsConfig.set("eggs." + eggId, null);
        eggsConfig.save();
    }

    public boolean checkEgg(String eggId) {
        ConfigurationSection eggsSection = eggsConfig.getConfig().getConfigurationSection("eggs");
        if (eggsSection == null) {
            //Section does not exist
            return true;
        }
        Set<String> eggIds = eggsSection.getKeys(false);
        return !eggIds.contains(eggId);
    }

    public List<String> getEggs() {
        ConfigurationSection eggsSection = eggsConfig.getConfig().getConfigurationSection("eggs");
        if (eggsSection == null) {
            //Section does not exist
            return null;
        }
        Set<String> eggIds = eggsSection.getKeys(false);
        return new ArrayList<>(eggIds);
    }

    public void spawnEggs() {
        if (getEggs() == null) return;
        for (String eggId : getEggs()) {
            Location loc = getEggLoc(eggId);
            Block current = loc.getBlock();
            if (current.getType().equals(Material.PLAYER_HEAD)) {
                skullUtil.placeCustomHead(loc, mainConfig.getString("skull-value"));
            }
        }
    }

    public List<String> collectedEggs(Player p) {
        UUID uuid = p.getUniqueId();
        ConfigurationSection users = usersConfig.getConfig().getConfigurationSection("users");
        List<String> eggs = new ArrayList<>();
        if (users == null) return eggs;
        if (users.contains(String.valueOf(uuid))) {
            eggs = usersConfig.getStringList("users." + uuid);
        }
        return eggs;
    }

    private int getNextId() {
        Set<String> keys = eggsConfig.getConfig().getConfigurationSection("eggs") != null
                ? Objects.requireNonNull(eggsConfig.getConfig().getConfigurationSection("eggs")).getKeys(false)
                : Set.of();
        int max = 0;
        for (String key : keys) {
            if (key.startsWith("easteregg")) {
                try {
                    int num = Integer.parseInt(key.replace("easteregg", ""));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return max + 1;
    }
}