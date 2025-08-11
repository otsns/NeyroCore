package com.github.otsns.neyroCore;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ConfigurableBundles extends JavaPlugin implements Listener {

    private int maxBundleCapacity = 128;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Плагин успешно запущен! Максимальная вместимость бандлов: " + maxBundleCapacity);
    }

    private void reloadConfigValues() {
        reloadConfig();
        maxBundleCapacity = getConfig().getInt("max-bundle-capacity", 128);
    }

    @EventHandler
    public void onBundleClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;
        
        ItemStack bundle = event.getCurrentItem();
        ItemStack clickedItem = event.getCursor();

        if (bundle == null || bundle.getType() != Material.BUNDLE) return;
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        BundleMeta meta = (BundleMeta) bundle.getItemMeta();
        int totalItems = meta.getItems().stream().mapToInt(ItemStack::getAmount).sum();
        int newItems = clickedItem.getAmount();

        if (totalItems + newItems > maxBundleCapacity) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("§cБандл может хранить только " + maxBundleCapacity + " предметов!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        }
    }

    @EventHandler
    public void onBundleDrag(InventoryDragEvent event) {
        ItemStack bundle = event.getOldCursor();
        if (bundle == null || bundle.getType() != Material.BUNDLE) return;

        BundleMeta meta = (BundleMeta) bundle.getItemMeta();
        int totalItems = meta.getItems().stream().mapToInt(ItemStack::getAmount).sum();
        int newItems = event.getNewItems().values().stream().mapToInt(ItemStack::getAmount).sum();

        if (totalItems + newItems > maxBundleCapacity) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.sendMessage("§cБандл переполнен!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("configurablebundles")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("configurablebundles.reload")) {
                    sender.sendMessage("§cУ вас нет прав на эту команду!");
                    return true;
                }
                reloadConfigValues();
                sender.sendMessage("§aКонфигурация успешно перезагружена! Максимальная вместимость: " + maxBundleCapacity);
                return true;
            }
            sender.sendMessage("§eИспользование: /configurablebundles reload");
            return true;
        }
        return false;
    }
}
