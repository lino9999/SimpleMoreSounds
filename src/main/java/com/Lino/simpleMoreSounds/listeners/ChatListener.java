package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import com.Lino.simpleMoreSounds.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {
    private final SoundManager soundManager;
    private final JavaPlugin plugin;

    public ChatListener(SoundManager soundManager, JavaPlugin plugin) {
        this.soundManager = soundManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            soundManager.playGlobalSound(SoundKey.CHAT);

            String message = event.getMessage();
            String senderName = event.getPlayer().getName();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().equals(senderName) &&
                        MessageUtils.isPlayerMentioned(message, player.getName())) {
                    soundManager.playSound(SoundKey.MENTION, player);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerReceiveMessage(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (MessageUtils.isPrivateMessage(message)) {
            soundManager.playSound(SoundKey.PRIVATE_MESSAGE, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommandForPrivateMessage(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        String[] args = command.split(" ");

        if (args.length >= 3) {
            String cmd = args[0];
            if (MessageUtils.isPrivateMessageCommand(cmd)) {
                String targetPlayerName = args[1];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer != null && targetPlayer.isOnline()) {
                    soundManager.playSound(SoundKey.PRIVATE_MESSAGE, targetPlayer);
                }
            }
        }

        soundManager.playSound(SoundKey.COMMAND, event.getPlayer());
    }
}