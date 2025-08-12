package com.github.otsns.neyroCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
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
        return new PacketAdapter(plugin, PacketType.Play.Server.SERVER_DATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!configManager.isEnabled()) return;

                PacketContainer packet = event.getPacket();
                try {
                    packet.getStrings().write(0, configManager.getServerBrand());
                    // Для версий 1.21+
                    packet.getBooleans().write(0, false); // previewsChat = false
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
