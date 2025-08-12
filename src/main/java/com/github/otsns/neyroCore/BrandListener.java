package com.github.otsns.neyroCore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Listener that sends configured server-brand to players on join using plugin message (server->client).
 * Avoids low-level ProtocolLib modifications which caused IndexOutOfBounds in older code.
 */
public class BrandListener implements Listener {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public BrandListener(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        sendBrandToPlayer(p);
    }

    public void sendBrandToPlayer(Player p) {
        try {
            String raw = configManager.getServerBrand();
            if (raw == null) raw = "NeyroCore";
            String brand = ChatColor.translateAlternateColorCodes('&', raw);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(brand);
            byte[] payload = out.toByteArray();

            // send plugin message (server -> client)
            p.sendPluginMessage(plugin, "minecraft:brand", payload);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to send brand to player " + p.getName(), t);
        }
    }
}
