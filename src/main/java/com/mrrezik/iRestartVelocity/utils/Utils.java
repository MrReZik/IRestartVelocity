package com.mrrezik.iRestartVelocity.utils;

import com.mrrezik.iRestartVelocity.IRestartVelocity; // Исправленный импорт
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static Component color(String message) {
        if (message == null || message.isEmpty()) return Component.empty();

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "&#" + group);
        }
        matcher.appendTail(buffer);

        return SERIALIZER.deserialize(buffer.toString());
    }

    public static void sendMessage(CommandSource sender, String path) {
        // Здесь используем IRestartVelocity.getInstance()
        Object val = IRestartVelocity.getInstance().getConfig().get(path, null);

        if (val instanceof List<?>) {
            List<?> list = (List<?>) val;
            for (Object obj : list) {
                if (obj instanceof String) {
                    sender.sendMessage(color((String) obj));
                }
            }
        } else if (val != null) {
            String prefix = IRestartVelocity.getInstance().getConfig().getString("messages.prefix", "");
            sender.sendMessage(color(prefix + val.toString()));
        }
    }

    public static void sendRaw(CommandSource sender, String msg) {
        sender.sendMessage(color(msg));
    }
}