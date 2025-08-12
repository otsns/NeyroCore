package com.github.otsns.neyrocore;

import io.papermc.paper.event.packet.PlayerHandshakeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {
    private final BrandManager brandManager;

    public PacketListener(NeyroCorePlugin plugin, BrandManager brandManager) {
        this.brandManager = brandManager;
    }

    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        event.setServerHostname(
            event.getServerHostname()
                .replace("Paper", brandManager.getCustomBrand())
        );
    }
}
