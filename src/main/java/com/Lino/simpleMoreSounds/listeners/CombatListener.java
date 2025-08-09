package com.Lino.simpleMoreSounds.listeners;

import com.Lino.simpleMoreSounds.config.SoundKey;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {
    private final SoundManager soundManager;

    public CombatListener(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        soundManager.playGlobalSound(SoundKey.DEATH);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player) {
            Player attacker = (Player) damager;

            // Critical hit detection
            if (attacker.getFallDistance() > 0 && !attacker.isOnGround() &&
                    attacker.getAttackCooldown() > 0.9f) {
                soundManager.playSound(SoundKey.CRITICAL_HIT, attacker);
            }

            // Kill detection
            if (victim instanceof LivingEntity) {
                LivingEntity livingVictim = (LivingEntity) victim;
                if (event.getFinalDamage() >= livingVictim.getHealth()) {
                    if (victim instanceof Player) {
                        soundManager.playSound(SoundKey.PLAYER_KILL, attacker);
                    } else if (victim instanceof Monster || victim instanceof Animals) {
                        soundManager.playSound(SoundKey.MOB_KILL, attacker);
                    }
                }
            }
        }

        // Arrow hit detection
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                soundManager.playSound(SoundKey.ARROW_SHOOT, (Player) arrow.getShooter());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            soundManager.playSound(SoundKey.ARROW_SHOOT, (Player) event.getEntity());
        }
    }
}