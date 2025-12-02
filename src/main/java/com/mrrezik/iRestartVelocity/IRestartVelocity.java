package com.mrrezik.iRestartVelocity;

import com.google.inject.Inject;
import com.mrrezik.iRestartVelocity.commands.MainCommand; // Убедитесь, что этот импорт есть
import com.mrrezik.iRestartVelocity.tasks.AutoRestartTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "irestart",
        name = "iRestart",
        version = "1.0-SNAPSHOT",
        description = "Automatic restart scheduler for Velocity",
        authors = {"MrReZik"}
)
public class IRestartVelocity {

    private static IRestartVelocity instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final ConfigManager configManager;
    private ScheduledTask restartTask;

    @Inject
    public IRestartVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.configManager = new ConfigManager(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        configManager.loadConfig();

        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("irestart").build(),
                new MainCommand(this)
        );

        startTimer();
        logger.info("iRestart v1.0 loaded on Velocity!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (restartTask != null) {
            restartTask.cancel();
        }
    }

    public void startTimer() {
        if (restartTask != null) {
            restartTask.cancel();
        }
        restartTask = server.getScheduler()
                .buildTask(this, new AutoRestartTask(this))
                .repeat(1, TimeUnit.SECONDS)
                .schedule();
    }

    public static IRestartVelocity getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ConfigManager getConfig() {
        return configManager;
    }
}