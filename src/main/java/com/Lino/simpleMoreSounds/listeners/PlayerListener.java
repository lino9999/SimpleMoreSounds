package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {
    private final SoundManager soundManager;
    private final JavaPlugin plugin;

    public PlayerListener(SoundManager soundManager, JavaPlugin plugin) {
        this.soundManager = soundManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.hasPlayedBefore()) {
                soundManager.playGlobalSound(SoundKey.FIRST_JOIN);
            } else {
                soundManager.playGlobalSound(SoundKey.JOIN);
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        soundManager.playGlobalSound(SoundKey.QUIT);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        soundManager.playSound(SoundKey.ITEM_DROP, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        soundManager.playSound(SoundKey.HOTBAR_SWITCH, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            soundManager.playSound(SoundKey.PLAYER_RESPAWN, event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            soundManager.playSound(SoundKey.TELEPORT, event.getPlayer());
        }
    }
}