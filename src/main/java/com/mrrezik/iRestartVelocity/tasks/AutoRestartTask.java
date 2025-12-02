package com.mrrezik.iRestartVelocity.tasks;

import com.mrrezik.iRestartVelocity.IRestartVelocity;
import com.mrrezik.iRestartVelocity.utils.Utils; // ИСПРАВЛЕН ИМПОРТ
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AutoRestartTask implements Runnable {

    private final IRestartVelocity plugin;
    private final ProxyServer server;

    public AutoRestartTask(IRestartVelocity plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public void run() {
        String timeStr = plugin.getConfig().getString("settings.time", "00:00");
        String zoneStr = plugin.getConfig().getString("settings.timezone", "UTC");

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(zoneStr);
        } catch (Exception e) {
            zoneId = ZoneId.systemDefault();
        }

        ZonedDateTime now = ZonedDateTime.now(zoneId);

        if (timeStr == null) timeStr = "00:00";
        String[] parts = timeStr.split(":");
        if (parts.length != 2) return;

        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        ZonedDateTime target = now.withHour(h).withMinute(m).withSecond(0).withNano(0);

        if (now.isAfter(target)) {
            target = target.plusDays(1);
        }

        long secondsDiff = ChronoUnit.SECONDS.between(now, target);

        if (now.getHour() == h && now.getMinute() == m && now.getSecond() == 0) {
            executeRestart();
            return;
        }

        // --- УВЕДОМЛЕНИЯ ---
        if (secondsDiff > 0 && secondsDiff <= 600) {
            List<Integer> warnings = plugin.getConfig().getIntList("warnings.times");

            if (warnings.contains((int) secondsDiff)) {
                sendAlerts((int) secondsDiff);
            }

            // Actionbar
            if (plugin.getConfig().getBoolean("settings.actionbar-timer", false) && secondsDiff <= 60) {
                var msg = Utils.color("&cРестарт через: &e" + secondsDiff + " сек");
                for (Player p : server.getAllPlayers()) {
                    p.sendActionBar(msg);
                }
            }
        }
    }

    private void sendAlerts(int seconds) {
        if (plugin.getConfig().getBoolean("warnings.title.enabled", false)) {
            String titleRaw = plugin.getConfig().getString("warnings.title.text", "").replace("%time%", String.valueOf(seconds));
            String subRaw = plugin.getConfig().getString("warnings.title.subtext", "").replace("%time%", String.valueOf(seconds));

            var titleComp = Utils.color(titleRaw);
            var subComp = Utils.color(subRaw);

            int fadeIn = plugin.getConfig().getInt("warnings.title.fade-in", 10) * 50;
            int stay = plugin.getConfig().getInt("warnings.title.stay", 40) * 50;
            int fadeOut = plugin.getConfig().getInt("warnings.title.fade-out", 10) * 50;

            Title title = Title.title(titleComp, subComp, Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut)));

            for (Player p : server.getAllPlayers()) {
                p.showTitle(title);
            }
        }

        if (plugin.getConfig().getBoolean("warnings.chat.enabled", false)) {
            List<String> lines = plugin.getConfig().getStringList("warnings.chat.message");
            for (Player p : server.getAllPlayers()) {
                for (String line : lines) {
                    p.sendMessage(Utils.color(line.replace("%time%", String.valueOf(seconds))));
                }
            }
        }
    }

    public void executeRestart() {
        String type = plugin.getConfig().getString("settings.type", "RESTART");

        List<String> preCmds = plugin.getConfig().getStringList("actions.pre-restart-commands");
        for (String cmd : preCmds) {
            server.getCommandManager().executeAsync(server.getConsoleCommandSource(), cmd);
        }

        if ("RELOAD".equalsIgnoreCase(type)) {
            String reloadCmd = plugin.getConfig().getString("actions.reload-cmd", "irestart reload");
            server.getCommandManager().executeAsync(server.getConsoleCommandSource(), reloadCmd);
        } else {
            if (plugin.getConfig().getBoolean("actions.kick.enabled", false)) {
                String kickMsgRaw = plugin.getConfig().getString("actions.kick.message", "Server Restarting...")
                        .replace("%time%", plugin.getConfig().getString("settings.time", "now"));
                var kickMsg = Utils.color(kickMsgRaw);

                for (Player p : server.getAllPlayers()) {
                    p.disconnect(kickMsg);
                }
            }
            server.shutdown();
        }
    }
}