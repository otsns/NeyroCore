package com.github.otsns.neyrocore;

import org.bukkit.configuration.file.FileConfiguration;

public class BrandManager {
    private final NeyroCorePlugin plugin;
    private String customBrand;

    public BrandManager(NeyroCorePlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        customBrand = config.getString("brand-name", "NeyroCore");
    }

    public String getCustomBrand() {
        return customBrand;
    }
}