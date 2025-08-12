package com.github.otsns.neyroCore;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BrandListener implements Listener, PluginMessageListener {

    private final NeyroCore plugin;
    private final ConfigManager configManager;

    public BrandListener(NeyroCore plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("minecraft:brand")) {
            return; // только бренд-канал
        }

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String originalBrand = in.readUTF();

            String newBrand = configManager.getServerBrand();
            if (newBrand == null || newBrand.isEmpty()) {
                newBrand = originalBrand;
            }

            plugin.getLogger().info("Original brand: " + originalBrand + " -> New brand: " + newBrand);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(newBrand);
            player.sendPluginMessage(plugin, "minecraft:brand", out.toByteArray());

        } catch (Exception e) {
            plugin.getLogger().warning("Error modifying brand packet: " + e.getMessage());
        }
    }
}
