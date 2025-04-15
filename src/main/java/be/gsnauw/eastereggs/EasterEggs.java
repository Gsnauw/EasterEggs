package be.gsnauw.eastereggs;

import be.gsnauw.eastereggs.commands.EasterAdminCommand;
import be.gsnauw.eastereggs.commands.EasterCommand;
import be.gsnauw.eastereggs.listeners.LeftClickListener;
import be.gsnauw.eastereggs.listeners.RightClickListener;
import be.gsnauw.eastereggs.managers.ConfigManager;
import be.gsnauw.eastereggs.managers.EggManager;
import be.gsnauw.eastereggs.utils.ChatUtil;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EasterEggs extends JavaPlugin {
    @Getter
    private ConfigManager mainConfig;
    @Getter
    private ConfigManager usersConfig;
    @Getter
    private ConfigManager eggsConfig;
    @Getter
    private ChatUtil chatUtil;
    @Getter
    private Economy econ;
    @Getter
    private static EasterEggs instance;

    @Override
    public void onEnable() {
        instance = this;
        mainConfig = new ConfigManager(this, "config.yml");
        usersConfig = new ConfigManager(this, "users.yml");
        eggsConfig = new ConfigManager(this, "eggs.yml");
        chatUtil = new ChatUtil(this);

        if (setupEconomy()) {
            chatUtil.warn("Vault Economy was not found. Eco reward disabled...");
        } else {
            chatUtil.info("Hooked into Vault Economy.");
        }

        Objects.requireNonNull(getCommand("eastereggs")).setExecutor(new EasterCommand());
        Objects.requireNonNull(getCommand("eastereggsadmin")).setExecutor(new EasterAdminCommand());
        getServer().getPluginManager().registerEvents(new RightClickListener(), this);
        getServer().getPluginManager().registerEvents(new LeftClickListener(), this);

        EggManager eggManager = EggManager.getInstance();
        eggManager.spawnEggs();
        chatUtil.info("The plugin has started, Hello World!");
    }

    @Override
    public void onDisable() {
        getLogger().info("The plugin has been disabled, goodbye!");
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return true;
        }
        econ = rsp.getProvider();
        return false;
    }
}
