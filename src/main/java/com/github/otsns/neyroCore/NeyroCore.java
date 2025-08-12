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

        // Unregister channels first to avoid duplicate-registration errors on reloads
        try {
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "minecraft:brand");
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "minecraft:brand");
        } catch (Exception ignored) {}

        // Register outgoing channel (server -> client) to send brand on join
        try {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "minecraft:brand");
        } catch (IllegalArgumentException e) {
            getLogger().warning("Outgoing channel minecraft:brand already registered: " + e.getMessage());
        }

        // Register event listener for PlayerJoin to send brand
        getServer().getPluginManager().registerEvents(brandListener, this);

        getLogger().info("NeyroCore enabled. Brand: " + ChatColor.stripColor(configManager.getServerBrand()));
    }

    @Override
    public void onDisable() {
        // Unregister channels/listeners
        try {
            Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "minecraft:brand");
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "minecraft:brand");
        } catch (Exception ignored) {}

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
                    getLogger().info("Configuration reloaded via command by " + (sender instanceof Player ? ((Player)sender).getName() : sender.getName()));
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
