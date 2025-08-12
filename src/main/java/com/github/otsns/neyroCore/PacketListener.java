package com.github.otsns.neyroCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.plugin.Plugin;

public class PacketListener {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private final PacketAdapter adapter;

    public PacketListener(Plugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.adapter = createAdapter();
    }

    private PacketAdapter createAdapter() {
        return new PacketAdapter(plugin, PacketType.Play.Server.LOGIN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!configManager.isEnabled()) return;

                PacketContainer packet = event.getPacket();
                try {
                    WrappedServerPing serverPing = packet.getServerPings().read(0);
                    
                    // Используем встроенный WrappedChatComponent из ProtocolLib
                    serverPing.setMotD(WrappedChatComponent.fromText(configManager.getServerBrand()));
                    
                    packet.getServerPings().write(0, serverPing);
                    plugin.getLogger().info("Modified server brand to: " + configManager.getServerBrand());
                } catch (Exception e) {
                    plugin.getLogger().warning("Error modifying brand packet: " + e.getMessage());
                }
            }
        };
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
    }

    public void unregister() {
        ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
    }
}
