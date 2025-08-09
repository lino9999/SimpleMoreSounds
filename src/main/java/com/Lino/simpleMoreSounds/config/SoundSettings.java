package com.Lino.simpleMoreSounds.config;

import org.bukkit.Sound;

public class SoundSettings {
    private final boolean enabled;
    private final Sound sound;
    private final String soundString;
    private final float volume;
    private final float pitch;

    public SoundSettings(boolean enabled, String soundString, float volume, float pitch) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public Sound getSound() {
        return sound;
    }

    public String getSoundString() {
        return soundString;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}