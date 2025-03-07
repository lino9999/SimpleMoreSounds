package com.Lino.simpleMoreSounds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.EnumMap;
import java.util.Map;

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
        boolean enabled;
        String sound;
        float volume;
        float pitch;
    }

    private final Map<SoundKey, SoundSettings> soundSettingsMap = new EnumMap<>(SoundKey.class);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSoundSettings();
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("soundsreload").setExecutor((CommandSender sender, Command cmd, String label, String[] args) -> {
            if (!sender.hasPermission("simplemoresounds.reload")) {
                sender.sendMessage("§cYou do not have permission for this command!");
                return true;
            }
            reloadConfig();
            loadSoundSettings();
            sender.sendMessage("§aConfig successfully reloaded!");
            return true;
        });
    }

    private void loadSoundSettings() {
        soundSettingsMap.clear();
        for (SoundKey key : SoundKey.values()) {
            SoundSettings settings = new SoundSettings();
            String path = key.getConfigKey();
            settings.enabled = getConfig().getBoolean(path + ".enabled", true);
            settings.sound = getConfig().getString(path + ".sound", "block.note_block.pling");
            settings.volume = (float) getConfig().getDouble(path + ".volume", 1.0);
            settings.pitch = (float) getConfig().getDouble(path + ".pitch", 1.0);
            soundSettingsMap.put(key, settings);
        }
    }

    private void playConfiguredSound(SoundKey key, Player specificPlayer) {
        SoundSettings settings = soundSettingsMap.get(key);
        if (settings == null || !settings.enabled) return;
        if (specificPlayer != null) {
            specificPlayer.playSound(specificPlayer.getLocation(), settings.sound, settings.volume, settings.pitch);
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), settings.sound, settings.volume, settings.pitch);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().runTask(this, () -> playConfiguredSound(SoundKey.CHAT, null));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        playConfiguredSound(SoundKey.ITEM_DROP, event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        playConfiguredSound(SoundKey.DEATH, null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            playConfiguredSound(SoundKey.FIRST_JOIN, null);
        } else {
            playConfiguredSound(SoundKey.JOIN, null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playConfiguredSound(SoundKey.QUIT, null);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        playConfiguredSound(SoundKey.COMMAND, event.getPlayer());
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        playConfiguredSound(SoundKey.HOTBAR_SWITCH, event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getPlayer() instanceof Player) {
            playConfiguredSound(SoundKey.INVENTORY_CLOSE, (Player) event.getPlayer());
        }
    }
}
