package com.github.otsns.neyroCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;

public class NeyroCore extends JavaPlugin implements Listener {

    private boolean enabled;
    private String serverBrand;
    private Component brandComponent;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NeyroCore enabled! Brand: " + serverBrand);
    }

    private void reloadConfigValues() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        serverBrand = getConfig().getString("server-brand", "NeyroCore");
        brandComponent = LegacyComponentSerializer.legacySection().deserialize(serverBrand);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (enabled) {
            event.motd(brandComponent);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (enabled) {
            updateBrand(event.getPlayer());
        }
    }

    private void updateBrand(Player player) {
        // Используем асинхронную задачу для отправки бренда
        Bukkit.getScheduler().runTaskLater(this, () -> {
            try {
                player.sendPlayerListHeader(brandComponent);
            } catch (Exception e) {
                getLogger().warning("Error updating brand for player: " + e.getMessage());
            }
        }, 20L); // Задержка 1 секунда для надёжности
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("core") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfigValues();
            // Обновляем бренд для всех онлайн-игроков
            Bukkit.getOnlinePlayers().forEach(this::updateBrand);
            sender.sendMessage(ChatColor.GREEN + "NeyroCore config reloaded!");
            return true;
        }
        return false;
    }
}
