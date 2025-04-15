package be.gsnauw.eastereggs.managers;

import be.gsnauw.eastereggs.EasterEggs;
import be.gsnauw.eastereggs.utils.ChatUtil;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardsManager {
    @Getter
    private static final RewardsManager instance = new RewardsManager();
    ConfigManager mainConfig = EasterEggs.getInstance().getMainConfig();
    ConfigManager eggsConfig = EasterEggs.getInstance().getEggsConfig();
    Economy econ = EasterEggs.getInstance().getEcon();
    EggManager eggManager = EggManager.getInstance();
    ChatUtil chat = EasterEggs.getInstance().getChatUtil();

    public List<String> getRewards(String eggId) {
        List<String> rewards = new ArrayList<>();
        if (!eggManager.checkEgg(eggId)) {
           rewards = eggsConfig.getStringList("eggs." + eggId + ".rewards");
        }
        return rewards;
    }

    public void giveRewards(Player p, String eggId) {
        int foundAmount = eggManager.collectedEggs(p).size();
        int totalAmount = eggManager.getEggs().size();
        if (foundAmount == totalAmount) {
            giveFinalReward(p);
            return;
        }

        List<String> rewards = getRewards(eggId);
        for (String reward : rewards) {
            String[] splitted = reward.split(":");
            String type = splitted[0];
            String value = splitted[1];
            switch (type.toLowerCase()) {
                case "eco" -> {
                    if (EasterEggs.getInstance().setupEconomy()) {
                        chat.info("Skipping eco reward.");
                    }
                    econ.depositPlayer(p, Double.parseDouble(value));
                    String orgMessage = mainConfig.getString("messages.egg-reward");
                    String replace = orgMessage.replace("<amount>", value);
                    String message = replace.replace("<reward>", "Ducaten");
                    p.sendMessage(chat.prefix(message));
                }
                case "item" -> {
                    Material material = Material.valueOf(value);
                    int amount = 5;
                    p.getInventory().addItem(new ItemStack(material, amount));

                    String orgMessage = mainConfig.getString("messages.egg-reward");
                    String replace = orgMessage.replace("<amount>", String.valueOf(amount));
                    String message = replace.replace("<reward>", value);
                    p.sendMessage(chat.prefix(message));
                }
                default -> {
                    chat.warn("Easteregg without reward was found ID: " + eggId);
                    p.sendMessage(chat.prefix("&cDeze easteregg bevat geen rewards. Meld dit bij staff. ID: " + eggId));
                }
            }
        }
    }

    public void giveFinalReward(Player p) {
        List<String> rewards = eggsConfig.getStringList("finalreward");
        p.sendMessage(chat.prefix(mainConfig.getString("messages.egg-final-reward")));
        for (String reward : rewards) {
            String[] splitted = reward.split(":");
            String type = splitted[0];
            String value = splitted[1];
            switch (type.toLowerCase()) {
                case "eco" -> {
                    if (EasterEggs.getInstance().setupEconomy()) {
                        chat.info("Skipping eco reward.");
                    }
                    econ.depositPlayer(p, Double.parseDouble(value));
                    String orgMessage = mainConfig.getString("messages.egg-reward");
                    String replace = orgMessage.replace("<amount>", value);
                    String message = replace.replace("<reward>", "Ducaten");
                    p.sendMessage(chat.prefix(message));
                }
                case "item" -> {
                    Material material = Material.valueOf(value);
                    int amount = 20;
                    p.getInventory().addItem(new ItemStack(material, amount));

                    String orgMessage = mainConfig.getString("messages.egg-reward");
                    String replace = orgMessage.replace("<amount>", String.valueOf(amount));
                    String message = replace.replace("<reward>", value);
                    p.sendMessage(chat.prefix(message));
                }
                default -> {
                    chat.warn("Easteregg without reward was found ID: FINALREWARD");
                    p.sendMessage(chat.prefix("&cDeze easteregg bevat geen rewards. Meld dit bij staff. ID: FINALREWARD"));
                }
            }
        }
    }
}
