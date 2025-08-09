package com.Lino.simpleMoreSounds.managers;

import com.Lino.simpleMoreSounds.listeners.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private final JavaPlugin plugin;
    private final SoundManager soundManager;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerManager(JavaPlugin plugin, SoundManager soundManager) {
        this.plugin = plugin;
        this.soundManager = soundManager;
        initializeListeners();
    }

    private void initializeListeners() {
        listeners.add(new ChatListener(soundManager, plugin));
        listeners.add(new CombatListener(soundManager));
        listeners.add(new InteractionListener(soundManager));
        listeners.add(new InventoryListener(soundManager));
        listeners.add(new PlayerListener(soundManager, plugin));
        listeners.add(new ProgressionListener(soundManager));
    }

    public void registerAll() {
        PluginManager pm = plugin.getServer().getPluginManager();
        listeners.forEach(listener -> pm.registerEvents(listener, plugin));
    }

    public void unregisterAll() {
        listeners.forEach(HandlerList::unregisterAll);
    }
}