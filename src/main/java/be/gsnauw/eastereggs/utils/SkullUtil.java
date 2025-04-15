package be.gsnauw.eastereggs.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import java.lang.reflect.Field;
import java.util.UUID;

public class SkullUtil {
    @Getter
    private static final SkullUtil instance = new SkullUtil();

    public void placeCustomHead(Location location, String base64Texture) {
        Block block = location.getBlock();
        block.setType(Material.PLAYER_HEAD);

        Skull skull = (Skull) block.getState();

        // Zorg ervoor dat de naam niet null is, zelfs als het een tijdelijke naam is
        GameProfile profile = new GameProfile(UUID.randomUUID(), "EasterEgg");

        // Voeg de base64 texture toe aan het profiel
        profile.getProperties().put("textures", new Property("textures", base64Texture));

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        skull.update();
    }
}