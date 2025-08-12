package com.github.otsns.neyroCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

public class ConfigManager {
    private final JavaPlugin plugin;
    private boolean enabled;
    private String serverBrand;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        enabled = plugin.getConfig().getBoolean("enabled", true);
        serverBrand = plugin.getConfig().getString("server-brand", "NeyroCore");
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void updateOnlinePlayersBrand() {
        if (!enabled || !Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) return;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                // Создаем пакет для обновления бренда
                PacketContainer brandPacket = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
                brandPacket.getModifier().writeDefaults();
                brandPacket.getStrings().write(0, "minecraft:brand");
                brandPacket.getByteArrays().write(0, serverBrand.getBytes(StandardCharsets.UTF_8));
                
                // Отправляем пакет игроку
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, brandPacket);
            } catch (Exception e) {
                plugin.getLogger().warning("Error updating brand for " + player.getName() + ": " + e.getMessage());
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getServerBrand() {
        return serverBrand;
    }
}
