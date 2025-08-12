package com.github.otsns.neyroCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class NeyroCore extends JavaPlugin {

    private ConfigManager configManager;
    private BrandListener brandListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.configManager.reload();

        this.brandListener = new BrandListener(this, configManager);
        // register listener
        getServer().getPluginManager().registerEvents(brandListener, this);

        // register plugin channels for safety (incoming/outgoing)
        // older versions use "MC|Brand", newer use "minecraft:brand"
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", brandListener);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "MC|Brand", brandListener);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "minecraft:brand");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "MC|Brand");

        getLogger().info("NeyroCore enabled. Brand: " + ChatColor.stripColor(configManager.getServerBrand()));
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "minecraft:brand", brandListener);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "MC|Brand", brandListener);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "minecraft:brand");
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "MC|Brand");
        getLogger().info("NeyroCore disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("core")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("neyrocore.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission!");
                    return true;
                }
                try {
                    configManager.reload();
                    sender.sendMessage(ChatColor.GREEN + "NeyroCore reloaded");
                    getLogger().info("Configuration reloaded via command by " + sender.getName());
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Failed to reload config", e);
                    sender.sendMessage(ChatColor.RED + "Reload failed: " + e.getMessage());
                }
                return true;
            }
        }
        return false;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
