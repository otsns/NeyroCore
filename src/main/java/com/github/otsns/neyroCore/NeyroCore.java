package com.github.otsns.neyroCore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerSendBrandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NeyroCore extends JavaPlugin implements Listener {
    private boolean enabled;
    private String serverBrand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NeyroCore enabled!");
    }

    private void reloadConfigValues() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        serverBrand = getConfig().getString("server-brand", "NeyroCore");
    }

    @EventHandler
    public void onBrandSend(AsyncPlayerSendBrandEvent event) {
        if (enabled) {
            event.setBrand(serverBrand);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("core") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfigValues();
            sender.sendMessage(ChatColor.GREEN + "NeyroCore config reloaded!");
            return true;
        }
        return false;
    }
}
