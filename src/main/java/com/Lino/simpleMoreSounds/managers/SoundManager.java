package com.Lino.simpleMoreSounds.managers;

import com.Lino.simpleMoreSounds.config.ConfigManager;
import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.config.SoundSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class SoundManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public SoundManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void playSound(SoundKey key, Player specificPlayer) {
        SoundSettings settings = configManager.getSoundSettings(key);
        if (settings == null || !settings.isEnabled()) return;

        try {
            if (specificPlayer != null) {
                playSoundToPlayer(specificPlayer, settings);
            } else {
                playGlobalSound(settings);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error while playing sound " + key, e);
        }
    }

    public void playGlobalSound(SoundKey key) {
        playSound(key, null);
    }

    private void playSoundToPlayer(Player player, SoundSettings settings) {
        try {
            if (settings.getSound() != null) {
                player.playSound(player.getLocation(), settings.getSound(),
                        settings.getVolume(), settings.getPitch());
            } else {
                player.playSound(player.getLocation(), settings.getSoundString(),
                        settings.getVolume(), settings.getPitch());
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING,
                    "Sound playback error for " + player.getName(), e);
        }
    }

    private void playGlobalSound(SoundSettings settings) {
        Bukkit.getOnlinePlayers().parallelStream()
                .forEach(player -> playSoundToPlayer(player, settings));
    }
}