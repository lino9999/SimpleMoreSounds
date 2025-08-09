package com.Lino.simpleMoreSounds.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<SoundKey, SoundSettings> soundSettingsMap = new EnumMap<>(SoundKey.class);

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        soundSettingsMap.clear();
        FileConfiguration config = plugin.getConfig();

        for (SoundKey key : SoundKey.values()) {
            String path = key.getConfigKey();
            boolean enabled = config.getBoolean(path + ".enabled", true);
            String sound = config.getString(path + ".sound", "BLOCK_NOTE_BLOCK_PLING");
            float volume = (float) config.getDouble(path + ".volume", 1.0);
            float pitch = (float) config.getDouble(path + ".pitch", 1.0);

            soundSettingsMap.put(key, new SoundSettings(enabled, sound, volume, pitch));
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    public SoundSettings getSoundSettings(SoundKey key) {
        return soundSettingsMap.get(key);
    }
}