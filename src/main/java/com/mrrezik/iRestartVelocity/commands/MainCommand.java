package com.mrrezik.iRestartVelocity.commands;

import com.mrrezik.iRestartVelocity.IRestartVelocity; // Ссылка на новый главный класс
import com.mrrezik.iRestartVelocity.tasks.AutoRestartTask;
import com.mrrezik.iRestartVelocity.utils.Utils;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainCommand implements SimpleCommand {

    private final IRestartVelocity plugin;

    public MainCommand(IRestartVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!sender.hasPermission("irestart.admin")) {
            Utils.sendMessage(sender, "messages.no-perm");
            return;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            List<String> help = plugin.getConfig().getStringList("messages.help");
            for (String s : help) Utils.sendRaw(sender, s);
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfig().reload();
            plugin.startTimer();
            Utils.sendMessage(sender, "messages.reloaded");
            return;
        }

        if (args[0].equalsIgnoreCase("now")) {
            new AutoRestartTask(plugin).executeRestart();
            return;
        }

        if (args[0].equalsIgnoreCase("type")) {
            if (args.length < 2) {
                Utils.sendRaw(sender, "&cУкажите тип: RESTART или RELOAD");
                return;
            }
            plugin.getConfig().set("settings.type", args[1].toUpperCase());
            plugin.getConfig().save();
            Utils.sendRaw(sender, "&aТип действия изменен на: " + args[1].toUpperCase());
            return;
        }

        if (args[0].equalsIgnoreCase("time") && args.length >= 3 && args[1].equalsIgnoreCase("set")) {
            if (!args[2].matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                Utils.sendRaw(sender, "&cНеверный формат! Используйте HH:mm");
                return;
            }
            plugin.getConfig().set("settings.time", args[2]);
            plugin.getConfig().save();
            plugin.startTimer();
            Utils.sendRaw(sender, "&aВремя рестарта установлено: " + args[2]);
            return;
        }

        if (args[0].equalsIgnoreCase("timezone")) {
            if (args.length < 2) {
                Utils.sendRaw(sender, "&cУкажите часовой пояс.");
                return;
            }
            try {
                ZoneId zone = ZoneId.of(args[1]);
                plugin.getConfig().set("settings.timezone", zone.toString());
                plugin.getConfig().save();
                plugin.startTimer();
                Utils.sendRaw(sender, "&aЧасовой пояс: " + zone);
            } catch (Exception e) {
                Utils.sendRaw(sender, "&cНеверный часовой пояс!");
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) return Arrays.asList("help", "reload", "type", "time", "timezone", "now");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("type")) return Arrays.asList("RESTART", "RELOAD");
            if (args[0].equalsIgnoreCase("time")) return Collections.singletonList("set");
            if (args[0].equalsIgnoreCase("timezone")) return plugin.getConfig().getStringList("timezones");
        }
        return new ArrayList<>();
    }
}