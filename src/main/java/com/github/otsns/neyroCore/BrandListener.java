package com.github.otsns.neyroCore;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BrandListener implements Listener, PluginMessageListener {

    private final NeyroCore plugin;

    public BrandListener(NeyroCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("minecraft:brand")) {
            return; // обрабатываем только бренд-канал
        }

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String originalBrand = in.readUTF();

            String newBrand = plugin.getConfig().getString("server-brand", originalBrand);
            plugin.getLogger().info("Original brand: " + originalBrand + " -> New brand: " + newBrand);

            // Отправляем обратно пакет с изменённым брендом
            player.sendPluginMessage(plugin, "minecraft:brand", ByteStreams.newDataOutput().writeUTF(newBrand).toByteArray());

        } catch (Exception e) {
            plugin.getLogger().warning("Error modifying brand packet: " + e.getMessage());
        }
    }
}
