package com.github.otsns.neyroCore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NeyroCore extends JavaPlugin {
    private ConfigManager configManager;
    private ProtocolManager protocolManager;
    private PacketListener packetListener;

    @Override
    public void onEnable() {
        // Инициализация конфига
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Регистрация обработчика пакетов
        protocolManager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketListener(this, configManager);
        packetListener.register();

        // Регистрация команды
        getCommand("core").setExecutor((sender, command, label, args) -> {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                configManager.reloadConfig();
                sender.sendMessage("§aКонфигурация перезагружена!");
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDisable() {
        if (packetListener != null) {
            packetListener.unregister();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
