package com.Lino.simpleMoreSounds;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

public class SimpleMoreSounds extends JavaPlugin implements Listener {

    private enum SoundKey {
        CHAT("chat_sound"),
        ITEM_DROP("item_drop_sound"),
        DEATH("death_sound"),
        FIRST_JOIN("first_join_sound"),
        JOIN("join_sound"),
        QUIT("quit_sound"),
        COMMAND("command_sound"),
        HOTBAR_SWITCH("hotbar_switch_sound"),
        INVENTORY_CLOSE("inventory_close_sound");

        private final String configKey;

        SoundKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigKey() {
            return configKey;
        }
    }

    private static class SoundSettings {
        final boolean enabled;
        final Sound sound;
        final String soundString;
        final float volume;
        final float pitch;

        SoundSettings(boolean enabled, String soundString, float volume, float pitch) {
            this.enabled = enabled;
            this.soundString = soundString;
            this.volume = Math.max(0.0f, Math.min(1.0f, volume)); // Clamp volume between 0 and 1
            this.pitch = Math.max(0.5f, Math.min(2.0f, pitch));   // Clamp pitch between 0.5 and 2

            // Try to parse as enum, fallback to string
            Sound parsedSound = null;
            try {
                parsedSound = Sound.valueOf(soundString.toUpperCase().replace('.', '_'));
            } catch (IllegalArgumentException ignored) {
                // Will use string version
            }
            this.sound = parsedSound;
        }
    }

    private final Map<SoundKey, SoundSettings> soundSettingsMap = new EnumMap<>(SoundKey.class);
    private BukkitTask configReloadTask;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            loadSoundSettings();
            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("SimpleMoreSounds plugin abilitato con successo!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Errore durante l'abilitazione del plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (configReloadTask != null && !configReloadTask.isCancelled()) {
            configReloadTask.cancel();
        }
        getLogger().info("SimpleMoreSounds plugin disabilitato.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("soundsreload".equals(command.getName())) {
            if (!sender.hasPermission("simplemoresounds.reload")) {
                sender.sendMessage("§cNon hai il permesso per questo comando!");
                return true;
            }

            // Async config reload to prevent blocking
            if (configReloadTask != null && !configReloadTask.isCancelled()) {
                sender.sendMessage("§cRicaricamento già in corso...");
                return true;
            }

            configReloadTask = Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    reloadConfig();
                    Bukkit.getScheduler().runTask(this, () -> {
                        loadSoundSettings();
                        sender.sendMessage("§aConfig ricaricata con successo!");
                        configReloadTask = null;
                    });
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Errore durante il ricaricamento della config", e);
                    Bukkit.getScheduler().runTask(this, () -> {
                        sender.sendMessage("§cErrore durante il ricaricamento della config!");
                        configReloadTask = null;
                    });
                }
            });
            return true;
        }
        return false;
    }

    private void loadSoundSettings() {
        soundSettingsMap.clear();
        for (SoundKey key : SoundKey.values()) {
            String path = key.getConfigKey();
            boolean enabled = getConfig().getBoolean(path + ".enabled", true);
            String sound = getConfig().getString(path + ".sound", "BLOCK_NOTE_BLOCK_PLING");
            float volume = (float) getConfig().getDouble(path + ".volume", 1.0);
            float pitch = (float) getConfig().getDouble(path + ".pitch", 1.0);

            soundSettingsMap.put(key, new SoundSettings(enabled, sound, volume, pitch));
        }
    }

    private void playConfiguredSound(SoundKey key, Player specificPlayer) {
        SoundSettings settings = soundSettingsMap.get(key);
        if (settings == null || !settings.enabled) return;

        try {
            if (specificPlayer != null) {
                playSound(specificPlayer, settings);
            } else {
                // Use a more efficient approach for broadcasting
                Bukkit.getOnlinePlayers().parallelStream().forEach(player -> playSound(player, settings));
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Errore durante la riproduzione del suono " + key, e);
        }
    }

    private void playSound(Player player, SoundSettings settings) {
        try {
            if (settings.sound != null) {
                // Use enum version for better performance
                player.playSound(player.getLocation(), settings.sound, settings.volume, settings.pitch);
            } else {
                // Fallback to string version
                player.playSound(player.getLocation(), settings.soundString, settings.volume, settings.pitch);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Errore riproduzione suono per " + player.getName(), e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Schedule on main thread since we're in async context
        Bukkit.getScheduler().runTask(this, () -> playConfiguredSound(SoundKey.CHAT, null));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        playConfiguredSound(SoundKey.ITEM_DROP, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        playConfiguredSound(SoundKey.DEATH, null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Schedule with slight delay to ensure player is fully loaded
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (!player.hasPlayedBefore()) {
                playConfiguredSound(SoundKey.FIRST_JOIN, null);
            } else {
                playConfiguredSound(SoundKey.JOIN, null);
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playConfiguredSound(SoundKey.QUIT, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        playConfiguredSound(SoundKey.COMMAND, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        playConfiguredSound(SoundKey.HOTBAR_SWITCH, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getPlayer() instanceof Player) {
            playConfiguredSound(SoundKey.INVENTORY_CLOSE, (Player) event.getPlayer());
        }
    }
}