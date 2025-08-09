package com.Lino.simpleMoreSounds.utils;

import java.util.regex.Pattern;

public class MessageUtils {

    public static boolean isPlayerMentioned(String message, String playerName) {
        String lowerMessage = message.toLowerCase();
        String lowerPlayerName = playerName.toLowerCase();

        if (lowerMessage.contains("@" + lowerPlayerName)) {
            return true;
        }

        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(lowerPlayerName) + "\\b",
                Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).find();
    }

    public static boolean isPrivateMessage(String message) {
        String lowerMessage = message.toLowerCase();

        return lowerMessage.startsWith("whispers to you:") ||
                lowerMessage.startsWith("whispers:") ||
                lowerMessage.contains(" whispers to you:") ||
                lowerMessage.contains(" whispers:") ||
                lowerMessage.startsWith("[") && lowerMessage.contains("-> me]") ||
                lowerMessage.startsWith("[") && lowerMessage.contains("→ me]") ||
                lowerMessage.contains(" -> you:") ||
                lowerMessage.contains(" → you:") ||
                lowerMessage.contains("tells you:") ||
                lowerMessage.contains(" tells you:");
    }

    public static boolean isPrivateMessageCommand(String command) {
        String lowerCommand = command.toLowerCase();

        return lowerCommand.equals("/msg") ||
                lowerCommand.equals("/tell") ||
                lowerCommand.equals("/whisper") ||
                lowerCommand.equals("/w") ||
                lowerCommand.equals("/m") ||
                lowerCommand.equals("/pm") ||
                lowerCommand.equals("/message") ||
                lowerCommand.equals("/essentials:msg") ||
                lowerCommand.equals("/essentials:tell") ||
                lowerCommand.equals("/essentials:whisper");
    }
}