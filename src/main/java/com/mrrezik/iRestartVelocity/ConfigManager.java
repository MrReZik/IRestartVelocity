package com.mrrezik.iRestartVelocity;

import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final IRestartVelocity plugin; // Исправленный тип
    private Map<String, Object> configData;
    private final File configFile;

    public ConfigManager(IRestartVelocity plugin) { // Исправленный конструктор
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataDirectory().toFile(), "config.yml");
    }

    public void loadConfig() {
        if (!plugin.getDataDirectory().toFile().exists()) {
            plugin.getDataDirectory().toFile().mkdirs();
        }

        if (!configFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().error("Failed to create default config!", e);
            }
        }

        try (InputStream input = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            configData = yaml.load(input);
        } catch (IOException e) {
            plugin.getLogger().error("Failed to load config.yml!", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path, T def) {
        if (configData == null) return def;
        String[] parts = path.split("\\.");
        Map<String, Object> current = configData;

        for (int i = 0; i < parts.length - 1; i++) {
            Object obj = current.get(parts[i]);
            if (obj instanceof Map) {
                current = (Map<String, Object>) obj;
            } else {
                return def;
            }
        }

        Object result = current.get(parts[parts.length - 1]);
        return result != null ? (T) result : def;
    }

    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        if (configData == null) return;
        String[] parts = path.split("\\.");
        Map<String, Object> current = configData;

        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new java.util.HashMap<>());
        }
        current.put(parts[parts.length - 1], value);
    }

    public void reload() {
        loadConfig();
    }

    public void save() {
        try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
            new Yaml().dump(configData, writer);
        } catch (IOException e) {
            plugin.getLogger().error("Failed to save config!", e);
        }
    }

    public String getString(String path, String def) { return get(path, def); }
    public int getInt(String path, int def) { return get(path, def); }
    public boolean getBoolean(String path, boolean def) { return get(path, def); }
    public List<String> getStringList(String path) { return get(path, List.of()); }
    public List<Integer> getIntList(String path) { return get(path, List.of()); }
}