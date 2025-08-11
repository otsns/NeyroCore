package com.github.otsns.configurableBundles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager { // Класс должен иметь имя!
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public boolean isPluginEnabled() {
        return config.getBoolean("enabled", true);
    }

    public int getMaxBundleCapacity() {
        return config.getInt("max-bundle-capacity", 128);
    }
}
