package com.github.otsns.neyroCore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Lightweight config manager to load values from config.yml
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private boolean enabled;
    private String serverBrand;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        try {
            plugin.reloadConfig();
            enabled = plugin.getConfig().getBoolean("enabled", true);
            serverBrand = plugin.getConfig().getString("server-brand", "NeyroCore");
            plugin.getLogger().info("Config loaded. enabled=" + enabled + ", server-brand=" + serverBrand);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load config", e);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getServerBrand() {
        return serverBrand;
    }
