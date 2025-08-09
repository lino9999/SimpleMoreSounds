package com.Lino.simpleMoreSounds.config;

public enum SoundKey {
    CHAT("chat_sound"),
    ITEM_DROP("item_drop_sound"),
    DEATH("death_sound"),
    FIRST_JOIN("first_join_sound"),
    JOIN("join_sound"),
    QUIT("quit_sound"),
    COMMAND("command_sound"),
    HOTBAR_SWITCH("hotbar_switch_sound"),
    INVENTORY_CLOSE("inventory_close_sound"),
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
    EXPERIENCE_GAIN("experience_gain_sound"),
    MENTION("mention_sound"),
    PRIVATE_MESSAGE("private_message_sound");

    private final String configKey;

    SoundKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}