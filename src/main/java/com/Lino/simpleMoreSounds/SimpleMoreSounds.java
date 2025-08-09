package com.Lino.simpleMoreSounds;

import com.Lino.simpleMoreSounds.commands.ReloadCommand;
import com.Lino.simpleMoreSounds.config.ConfigManager;
import com.Lino.simpleMoreSounds.managers.ListenerManager;
import com.Lino.simpleMoreSounds.managers.SoundManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class SimpleMoreSounds extends JavaPlugin {

    private static SimpleMoreSounds instance;
    private ConfigManager configManager;
    private SoundManager soundManager;
    private ListenerManager listenerManager;

    @Override
    public void onEnable() {
        try {
            instance = this;

            // Inizializza i manager
            this.configManager = new ConfigManager(this);
            this.soundManager = new SoundManager(this, configManager);
            this.listenerManager = new ListenerManager(this, soundManager);

            // Registra comandi
            getCommand("soundsreload").setExecutor(new ReloadCommand(this, configManager, soundManager));

            // Registra listener
            listenerManager.registerAll();

            getLogger().info("SimpleMoreSounds plugin successfully enabled!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error enabling plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (listenerManager != null) {
            listenerManager.unregisterAll();
        }
        getLogger().info("SimpleMoreSounds plugin disabled.");
    }

    public static SimpleMoreSounds getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}