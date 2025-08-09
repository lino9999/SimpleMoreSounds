package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ProgressionListener implements Listener {
    private final SoundManager soundManager;

    public ProgressionListener(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        soundManager.playSound(SoundKey.ADVANCEMENT_COMPLETE, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (event.getAmount() > 0) {
            soundManager.playSound(SoundKey.EXPERIENCE_GAIN, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getDurability() + event.getDamage() >=
                item.getType().getMaxDurability()) {
            soundManager.playSound(SoundKey.TOOL_BREAK, event.getPlayer());
        }
    }
}