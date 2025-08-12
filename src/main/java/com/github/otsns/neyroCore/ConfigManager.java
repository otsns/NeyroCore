package com.github.otsns.neyroCore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private boolean enabled;
    private String serverBrand;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        enabled = config.getBoolean("enabled", true);
        serverBrand = config.getString("server-brand", "NeyroCore");
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getServerBrand() {
        return serverBrand;
    }
}
