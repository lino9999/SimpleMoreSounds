package com.Lino.simpleMoreSounds;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
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
        INVENTORY_CLOSE("inventory_close_sound"),
        PLAYER_HURT("player_hurt_sound"),
        CRITICAL_HIT("critical_hit_sound"),
        ARROW_SHOOT("arrow_shoot_sound"),
        MOB_KILL("mob_kill_sound"),
        PLAYER_KILL("player_kill_sound"),
        FURNACE_USE("furnace_use_sound"),
        CRAFTING_TABLE_USE("crafting_table_use_sound"),
        ANVIL_USE("anvil_use_sound"),
        ENCHANTING_TABLE_USE("enchanting_table_use_sound"),
        FISHING_CAST("fishing_cast_sound"),
        FISHING_CATCH("fishing_catch_sound"),
        TOOL_BREAK("tool_break_sound"),
        PLAYER_RESPAWN("player_respawn_sound"),
        TELEPORT("teleport_sound"),
        INVENTORY_FULL("inventory_full_sound"),
        SHULKER_OPEN("shulker_open_sound"),
        ADVANCEMENT_COMPLETE("advancement_complete_sound"),
        EXPERIENCE_GAIN("experience_gain_sound");

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
            this.volume = Math.max(0.0f, Math.min(1.0f, volume));
            this.pitch = Math.max(0.5f, Math.min(2.0f, pitch));

            Sound parsedSound = null;
            try {
                parsedSound = Sound.valueOf(soundString.toUpperCase().replace('.', '_'));
            } catch (IllegalArgumentException ignored) {
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
            getLogger().info("SimpleMoreSounds plugin successfully enabled!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error enabling plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (configReloadTask != null && !configReloadTask.isCancelled()) {
            configReloadTask.cancel();
        }
        getLogger().info("SimpleMoreSounds plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("soundsreload".equals(command.getName())) {
            if (!sender.hasPermission("simplemoresounds.reload")) {
                sender.sendMessage("§cNo access to this cmd!");
                return true;
            }

            if (configReloadTask != null && !configReloadTask.isCancelled()) {
                sender.sendMessage("§cReloading already in progress...");
                return true;
            }

            configReloadTask = Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    reloadConfig();
                    Bukkit.getScheduler().runTask(this, () -> {
                        loadSoundSettings();
                        sender.sendMessage("§aConfig reloaded successfully!");
                        configReloadTask = null;
                    });
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Error reloading config", e);
                    Bukkit.getScheduler().runTask(this, () -> {
                        sender.sendMessage("§cError reloading config!");
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
                Bukkit.getOnlinePlayers().parallelStream().forEach(player -> playSound(player, settings));
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error while playing sound " + key, e);
        }
    }

    private void playSound(Player player, SoundSettings settings) {
        try {
            if (settings.sound != null) {
                player.playSound(player.getLocation(), settings.sound, settings.volume, settings.pitch);
            } else {
                player.playSound(player.getLocation(), settings.soundString, settings.volume, settings.pitch);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Sound playback error for " + player.getName(), e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
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
        } else if (event.getInventory().getType() == InventoryType.SHULKER_BOX && event.getPlayer() instanceof Player) {
            playConfiguredSound(SoundKey.SHULKER_OPEN, (Player) event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            playConfiguredSound(SoundKey.PLAYER_HURT, (Player) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player) {
            Player attacker = (Player) damager;
            if (attacker.getFallDistance() > 0 && !attacker.isOnGround() && attacker.getAttackCooldown() > 0.9f) {
                playConfiguredSound(SoundKey.CRITICAL_HIT, attacker);
            }

            if (event.getFinalDamage() >= ((LivingEntity) victim).getHealth()) {
                if (victim instanceof Player) {
                    playConfiguredSound(SoundKey.PLAYER_KILL, attacker);
                } else if (victim instanceof Monster || victim instanceof Animals) {
                    playConfiguredSound(SoundKey.MOB_KILL, attacker);
                }
            }
        }

        if (damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player) {
            playConfiguredSound(SoundKey.ARROW_SHOOT, (Player) ((Arrow) damager).getShooter());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            playConfiguredSound(SoundKey.ARROW_SHOOT, (Player) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getDurability() + event.getDamage() >= item.getType().getMaxDurability()) {
            playConfiguredSound(SoundKey.TOOL_BREAK, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            playConfiguredSound(SoundKey.PLAYER_RESPAWN, event.getPlayer());
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            playConfiguredSound(SoundKey.TELEPORT, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material type = block.getType();
        Player player = event.getPlayer();

        switch (type) {
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                playConfiguredSound(SoundKey.FURNACE_USE, player);
                break;
            case CRAFTING_TABLE:
                playConfiguredSound(SoundKey.CRAFTING_TABLE_USE, player);
                break;
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
                playConfiguredSound(SoundKey.ANVIL_USE, player);
                break;
            case ENCHANTING_TABLE:
                playConfiguredSound(SoundKey.ENCHANTING_TABLE_USE, player);
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        switch (event.getState()) {
            case FISHING:
                playConfiguredSound(SoundKey.FISHING_CAST, event.getPlayer());
                break;
            case CAUGHT_FISH:
            case CAUGHT_ENTITY:
                playConfiguredSound(SoundKey.FISHING_CATCH, event.getPlayer());
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        playConfiguredSound(SoundKey.ADVANCEMENT_COMPLETE, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (event.getAmount() > 0) {
            playConfiguredSound(SoundKey.EXPERIENCE_GAIN, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAttemptPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getInventory().firstEmpty() == -1) {
            playConfiguredSound(SoundKey.INVENTORY_FULL, player);
        }
    }
}
