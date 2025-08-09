package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {
    private final SoundManager soundManager;

    public InteractionListener(SoundManager soundManager) {
        this.soundManager = soundManager;
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
                soundManager.playSound(SoundKey.FURNACE_USE, player);
                break;
            case CRAFTING_TABLE:
                soundManager.playSound(SoundKey.CRAFTING_TABLE_USE, player);
                break;
            case ANVIL:
            case CHIPPED_ANVIL:
            case DAMAGED_ANVIL:
                soundManager.playSound(SoundKey.ANVIL_USE, player);
                break;
            case ENCHANTING_TABLE:
                soundManager.playSound(SoundKey.ENCHANTING_TABLE_USE, player);
                break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        switch (event.getState()) {
            case FISHING:
                soundManager.playSound(SoundKey.FISHING_CAST, event.getPlayer());
                break;
            case CAUGHT_FISH:
            case CAUGHT_ENTITY:
                soundManager.playSound(SoundKey.FISHING_CATCH, event.getPlayer());
                break;
        }
    }
}