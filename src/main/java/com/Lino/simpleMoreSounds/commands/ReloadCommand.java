package com.Lino.simpleMoreSounds.commands;

import com.Lino.simpleMoreSounds.config.ConfigManager;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.logging.Level;

public class ReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final SoundManager soundManager;
    private BukkitTask reloadTask;

    public ReloadCommand(JavaPlugin plugin, ConfigManager configManager, SoundManager soundManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.soundManager = soundManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("simplemoresounds.reload")) {
            sender.sendMessage("§cNo access to this cmd!");
            return true;
        }

        if (reloadTask != null && !reloadTask.isCancelled()) {
            sender.sendMessage("§cReloading already in progress...");
            return true;
        }

        reloadTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    configManager.reloadConfig();
                    sender.sendMessage("§aConfig reloaded successfully!");
                    reloadTask = null;
                });
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error reloading config", e);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage("§cError reloading config!");
                    reloadTask = null;
                });
            }
        });
        return true;
    }
}