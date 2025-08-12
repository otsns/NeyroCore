package com.github.otsns.neyroCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class NeyroCore extends JavaPlugin {

    private ConfigManager configManager;
    private BrandListener brandListener;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.configManager.reload();

        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.brandListener = new BrandListener(this);

        // Register ProtocolLib packet listener
        try {
            protocolManager.addPacketListener(brandListener);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Brand packet listener already registered: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(brandListener, this); // keep join sender for fallback

        getLogger().info("NeyroCore enabled. Brand: " + ChatColor.stripColor(configManager.getServerBrand()));
    }

    @Override
    public void onDisable() {
        try {
            if (protocolManager != null && brandListener != null) {
                protocolManager.removePacketListener(brandListener);
            }
        } catch (Throwable ignored) {}

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
                    // update brand dynamically: we can resend to online players
                    for (Player p : getServer().getOnlinePlayers()) {
                        brandListener.sendBrandFallback(p); // send fallback packet via plugin message or packet
                    }
                    sender.sendMessage(ChatColor.GREEN + "NeyroCore reloaded");
                    getLogger().info("Configuration reloaded via command by " + (sender instanceof Player ? ((Player) sender).getName() : sender.getName()));
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
