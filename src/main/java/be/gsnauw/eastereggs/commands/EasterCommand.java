package be.gsnauw.eastereggs.commands;

import be.gsnauw.eastereggs.EasterEggs;
import be.gsnauw.eastereggs.managers.ConfigManager;
import be.gsnauw.eastereggs.managers.EggManager;
import be.gsnauw.eastereggs.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EasterCommand implements CommandExecutor {

    ConfigManager mainConfig = EasterEggs.getInstance().getMainConfig();
    EggManager eggManager = EggManager.getInstance();
    ChatUtil chat = EasterEggs.getInstance().getChatUtil();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            chat.playerCommand();
            return true;
        }
        if (!p.hasPermission("eastereggs.use")) {
            p.sendMessage(chat.noPermission());
            return true;
        }

        int foundAmount = eggManager.collectedEggs(p).size();
        int totalAmount = eggManager.getEggs().size();
        String orgMessage = mainConfig.getString("messages.check-up");
        String replace = orgMessage.replace("<aantal1>", String.valueOf(foundAmount));
        String message = replace.replace("<aantal2>", String.valueOf(totalAmount));
        p.sendMessage(chat.prefix(message));
        return true;
    }
}
