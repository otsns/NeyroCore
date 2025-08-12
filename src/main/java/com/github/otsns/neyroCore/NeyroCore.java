package com.github.otsns.neyrocore;

import org.bukkit.plugin.java.JavaPlugin;

public class NeyroCorePlugin extends JavaPlugin {
    private BrandManager brandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.brandManager = new BrandManager(this);
        
        getServer().getPluginManager().registerEvents(
            new PacketListener(this, brandManager), 
            this
        );
        
        getLogger().info("NeyroCore activated! Custom brand: " + 
            getConfig().getString("brand-name"));
    }

    @Override
    public void onDisable() {
        getLogger().info("NeyroCore disabled");
    }
}
