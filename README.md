# 🔊 SimpleMoreSounds - Immersive Audio Overhaul (1.21+)

> **Transform your server into a living, breathing world with rich audio feedback.**
> SimpleMoreSounds adds customizable sound effects to 25+ events, from chat mentions to critical hits.
> **Lightweight, Lag-free, and Fully Configurable.**

![Java](https://img.shields.io/badge/Java-21-orange) ![Spigot](https://img.shields.io/badge/API-1.21-yellow) ![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🎧 Why SimpleMoreSounds?
Minecraft can be too quiet. **SimpleMoreSounds** fills the silence by adding satisfying audio cues to player actions. Whether it's the click of a hotbar switch or the thunder of a PvP kill, every action feels more impactful.

It features **Smart Detection** for chat mentions and private messages (compatible with EssentialsX and Vanilla commands).

### ✨ Key Features

* **💬 Social & Chat Sounds**
    * **Mentions:** Plays a chime when a player's name is tagged in chat (Smart Regex detection).
    * **Private Messages:** Unique sounds for `/msg`, `/tell`, and `/whisper`.
    * **Typing:** Optional subtle sound when players send messages.

* **⚔️ Combat & Action**
    * **Critical Hits:** Satisfying crunch sound when landing a crit.
    * **Kill Confirmations:** Distinct sounds for Mob kills vs Player kills.
    * **Bow Shooting:** High-tension bow release sounds.

* **🌍 World Interaction**
    * **Crafting & blocks:** Sounds for opening Furnaces, Crafting Tables, Anvils, and Enchanting Tables.
    * **Fishing:** Audio cues for casting and catching fish.
    * **Tools:** Warning sound when a tool is about to break (or breaks).

* **🎒 UI & Inventory**
    * Hotbar switching clicks.
    * Inventory open/close sounds.
    * "Inventory Full" warning sound when picking up items.

---

<div align="center">
   <p>I've just launched https://www.hytaleservers.it/</p>
   <p>Are you working on a server? List it now for free and build your audience before launch.​</p>
</div>

---

## ⚙️ Configuration
Every single sound can be toggled, and you can adjust the **Volume** and **Pitch** to your liking in `config.yml`.

```yaml
# Example Configuration
critical_hit_sound:
  enabled: true
  sound: "BLOCK_ENCHANTMENT_TABLE_USE"
  volume: 1.0
  pitch: 1.2

chat_sound:
  enabled: true
  sound: "ENTITY_ITEM_PICKUP"
  volume: 0.5
  pitch: 1.0
