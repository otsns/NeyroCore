package com.github.otsns.neyroCore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public class PacketListener {
    // ... остальной код без изменений ...

    private PacketAdapter createAdapter() {
        return new PacketAdapter(plugin, PacketType.Play.Server.SERVER_DATA) { // Изменили LOGIN на SERVER_DATA
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!configManager.isEnabled()) return;

                PacketContainer packet = event.getPacket();
                try {
                    // Для версий 1.19+
                    packet.getStrings().write(0, configManager.getServerBrand());
                } catch (Exception e) {
                    plugin.getLogger().warning("Error modifying brand packet: " + e.getMessage());
                }
            }
        };
    }
}
