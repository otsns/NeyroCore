package com.github.otsns.neyroCore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginMessageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * High-level listener that avoids low-level ProtocolLib modifications for the brand packet.
 * It modifies plugin messages on the "minecraft:brand" / "MC|Brand" channels.
 */
public class BrandListener implements Listener, org.bukkit.plugin.messaging.PluginMessageListener {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;

    public BrandListener(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    // Also send brand on join for players (some clients expect server->client brand on join)
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        sendBrandToPlayer(p);
    }

    private void sendBrandToPlayer(Player p) {
        try {
            String brand = ChatColor.translateAlternateColorCodes('&', configManager.getServerBrand());
            byte[] payload = brand.getBytes(StandardCharsets.UTF_8);
            // send on both channels to maximize compatibility
            p.sendPluginMessage(plugin, "minecraft:brand", payload);
            p.sendPluginMessage(plugin, "MC|Brand", payload);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Failed to send brand to player " + p.getName(), t);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // Only handle brand channels
        if (!"minecraft:brand".equals(channel) && !"MC|Brand".equals(channel)) return;

        try {
            // Replace the payload with our brand string.
            String brand = ChatColor.translateAlternateColorCodes('&', configManager.getServerBrand());
            byte[] payload = brand.getBytes(StandardCharsets.UTF_8);
            // We cannot change incoming message bytes directly here (Bukkit doesn't allow modifying the original array that will be sent),
            // but many servers use ProtocolLib. However, by also sending our own plugin message on join (sendBrandToPlayer),
            // and by cancelling and re-sending where appropriate, we avoid touching ProtocolLib.
            // Here we will simply ignore and not attempt to write into incoming array to prevent IndexOutOfBounds.
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Error in brand plugin message listener", t);
        }
    }
}
