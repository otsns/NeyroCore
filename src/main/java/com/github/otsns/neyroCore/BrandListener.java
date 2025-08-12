package com.github.otsns.neyroCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Packet-based brand modifier using ProtocolLib.
 * Works by intercepting CUSTOM_PAYLOAD packets and replacing brand payloads.
 * This implementation is defensive: it attempts multiple strategies to detect and replace the brand payload.
 */
public class BrandListener extends PacketAdapter implements org.bukkit.event.Listener {

    private final NeyroCore plugin;
    private final ProtocolManager protocolManager;

    public BrandListener(NeyroCore plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CUSTOM_PAYLOAD);
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        try {
            PacketContainer packet = event.getPacket();
            String channel = null;

            // Try several ways to read the channel name depending on ProtocolLib/Minecraft version
            try {
                // If MinecraftKey wrapper present
                if (!packet.getMinecraftKeys().isEmpty()) {
                    MinecraftKey key = packet.getMinecraftKeys().read(0);
                    if (key != null) channel = key.getKey();
                }
            } catch (Throwable ignored) {}

            if (channel == null) {
                try {
                    if (!packet.getStrings().isEmpty()) {
                        channel = packet.getStrings().read(0);
                    }
                } catch (Throwable ignored) {}
            }

            if (channel == null) {
                // nothing we can do
                return;
            }

            // normalize channel
            channel = channel.toLowerCase();
            if (!channel.equals("minecraft:brand") && !channel.equals("brand") && !channel.endsWith("brand")) {
                return;
            }

            String desired = plugin.getConfig().getString("server-brand", "NeyroCore");

            // Build replacement packet
            try {
                PacketContainer newPacket = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
                // Try to write MinecraftKey if supported
                try {
                    newPacket.getMinecraftKeys().write(0, new MinecraftKey("minecraft", "brand"));
                } catch (Throwable ignored) {}

                // Write as bytes (UTF-8) if possible
                try {
                    byte[] payload = desired.getBytes(StandardCharsets.UTF_8);
                    newPacket.getByteArrays().write(0, payload);
                } catch (Throwable t) {
                    // fallback: try strings
                    try {
                        newPacket.getStrings().write(0, desired);
                    } catch (Throwable t2) {
                        plugin.getLogger().log(Level.WARNING, "Failed to set brand payload by any method", t2);
                        return;
                    }
                }

                // Cancel original and send our packet
                event.setCancelled(true);
                Player player = event.getPlayer();
                protocolManager.sendServerPacket(player, newPacket, false);

            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Error while replacing brand packet: " + t.getMessage(), t);
            }

        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Unhandled exception in BrandListener", t);
        }
    }

    // Fallback method to send brand via plugin message (if packet-level replacement fails or for reload)
    public void sendBrandFallback(org.bukkit.entity.Player p) {
        try {
            String raw = plugin.getConfig().getString("server-brand", "NeyroCore");
            if (raw == null) raw = "NeyroCore";
            String brand = org.bukkit.ChatColor.translateAlternateColorCodes('&', raw);
            byte[] payload = brand.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            // send plugin message as fallback
            p.sendPluginMessage(plugin, "minecraft:brand", payload);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Fallback sendBrand failed for " + p.getName(), t);
        }
    }

}
