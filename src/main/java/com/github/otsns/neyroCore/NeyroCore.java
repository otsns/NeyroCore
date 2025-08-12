package com.github.otsns.neyroCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class NeyroCore extends JavaPlugin {

    private ConfigManager configManager;
    private ProtocolManager protocolManager;
    private PacketListener packetListener;

    @Override
    public void onEnable() {
        // Проверка наличия ProtocolLib
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            getLogger().severe("ProtocolLib is required but not found! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Инициализация конфигурации
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        try {
            // Инициализация ProtocolLib
            protocolManager = ProtocolLibrary.getProtocolManager();
            packetListener = new PacketListener(this, configManager);
            packetListener.register();
            getLogger().info("Packet listener registered successfully");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register packet listener", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Регистрация команды
        PluginCommand coreCommand = getCommand("core");
        if (coreCommand != null) {
            coreCommand.setExecutor(this);
            getLogger().info("Command '/core' registered successfully");
        } else {
            getLogger().severe("Command 'core' not found in plugin.yml! Check your configuration.");
        }

        getLogger().info("NeyroCore v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Server brand set to: " + configManager.getServerBrand());
    }

    @Override
    public void onDisable() {
        if (packetListener != null) {
            packetListener.unregister();
            getLogger().info("Packet listener unregistered");
        }
        getLogger().info("NeyroCore disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("core")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§eNeyroCore v" + getDescription().getVersion());
            sender.sendMessage("§eUsage: /core reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("neyrocore.reload")) {
                sender.sendMessage("§cYou don't have permission to reload the plugin!");
                return true;
            }

            try {
                configManager.reloadConfig();
                sender.sendMessage("§aConfiguration reloaded successfully!");
                
                // Обновляем бренд для всех онлайн-игроков
                configManager.updateOnlinePlayersBrand();
                sender.sendMessage("§aServer brand updated for all online players!");
                return true;
            } catch (Exception e) {
                sender.sendMessage("§cError reloading configuration: " + e.getMessage());
                getLogger().log(Level.SEVERE, "Error reloading configuration", e);
                return true;
            }
        }

        sender.sendMessage("§eUsage: /core reload");
        return true;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
