package be.gsnauw.eastereggs.commands;

import be.gsnauw.eastereggs.EasterEggs;
import be.gsnauw.eastereggs.managers.ConfigManager;
import be.gsnauw.eastereggs.managers.EggManager;
import be.gsnauw.eastereggs.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EasterAdminCommand implements CommandExecutor, TabCompleter {

    ConfigManager mainConfig = EasterEggs.getInstance().getMainConfig();
    ConfigManager usersConfig = EasterEggs.getInstance().getUsersConfig();
    ConfigManager eggsConfig = EasterEggs.getInstance().getEggsConfig();
    EggManager eggManager = EggManager.getInstance();
    ChatUtil chat = EasterEggs.getInstance().getChatUtil();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            chat.playerCommand();
            return true;
        }

        if (args.length == 0) {
            if (!p.hasPermission("eastereggs.admin")) {
                p.sendMessage(chat.noPermission());
                return true;
            }
            sendHelp(p);
            return true;
        }

        if (args.length == 1) {
            if (!p.hasPermission("eastereggs.admin")) {
                p.sendMessage(chat.noPermission());
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    Location loc = p.getLocation();
                    eggManager.setEgg(loc);
                    String message = mainConfig.getString("messages.egg-added").replace("<eggid>", eggManager.getEgg(loc));
                    p.sendMessage(chat.prefix(message));
                }
                case "reload" -> {
                    mainConfig.reload();
                    usersConfig.reload();
                    eggsConfig.reload();
                    eggManager.spawnEggs();
                    p.sendMessage(chat.prefix(mainConfig.getString("messages.reload")));
                }
                case "help" -> sendHelp(p);
                default -> sendError(p);
            }
        }

        if (args.length == 2) {
            if (!p.hasPermission("eastereggs.admin")) {
                p.sendMessage(chat.noPermission());
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "tp" -> {
                    String id = args[1];
                    Location loc = eggManager.getEggLoc(id);
                    if (loc == null) {
                        String message = mainConfig.getString("messages.egg-not-found").replace("<eggid>", id);
                        p.sendMessage(chat.prefix(message));
                        return true;
                    }
                    loc.setY(loc.getBlockY() + 1);
                    p.teleport(loc);
                    String message = mainConfig.getString("messages.egg-teleport").replace("<eggid>", id);
                    p.sendMessage(chat.prefix(message));
                }
                case "remove" -> {
                    String id = args[1];
                    if (eggManager.checkEgg(id)) {
                        String message = mainConfig.getString("messages.egg-not-found").replace("<eggid>", id);
                        p.sendMessage(chat.prefix(message));
                        return true;
                    }
                    eggManager.removeEgg(id);
                    String message = mainConfig.getString("messages.egg-removed").replace("<eggid>", id);
                    p.sendMessage(chat.prefix(message));
                }
                default -> sendError(p);
            }
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(chat.format("&f<------<&e&lEasterEggs&f>------>"));
        p.sendMessage(chat.format("&e/easteregg add&f - Place an egg at your current location."));
        p.sendMessage(chat.format("&e/easteregg tp <egg>&f - Teleport to an eggs location."));
        p.sendMessage(chat.format("&e/easteregg remove <egg>&f - Remove and egg."));
        p.sendMessage(chat.format("&e/easteregg reload&f - Reload the plugin."));
        p.sendMessage(chat.format("&e/easteregg help&f - Show this message."));
    }

    private void sendError(Player p) {
        p.sendMessage(chat.prefix("&cGebruik: /easteregg <add | remove | reload | tp | help>"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("tp");
            completions.add("help");
            completions.add("reload");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("tp")) {
                completions.addAll(eggManager.getEggs());
            }
        }
        return completions;
    }
}
