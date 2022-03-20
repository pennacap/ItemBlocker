package com.pennacap.itemblocker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ItemBlocker extends JavaPlugin implements Listener {
    public List<Material> materials = new ArrayList<>();
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        for (String i: getConfig().getStringList("blacklisted-items")){
            try {
                materials.add(Material.valueOf(i));
            } catch (IllegalArgumentException e){
                getLogger().severe(i+" is not a valid material");
            }
        }
    }
    @EventHandler
    public void onMove(InventoryClickEvent event){
        AtomicBoolean shouldDelete = new AtomicBoolean(false);
        Arrays.stream(event.getWhoClicked().getInventory().getContents()).forEach(itemStack -> {
            if (materials.contains(itemStack.getType()) && !event.getWhoClicked().hasPermission("itemblocker.ignoreblock")){
                itemStack.setAmount(0);
                shouldDelete.set(true);
            }

        });
        if (materials.contains(event.getCursor().getType()) && !event.getWhoClicked().hasPermission("itemblocker.ignoreblock")){
            event.getCursor().setAmount(0);
            shouldDelete.set(true);
        }
        if (shouldDelete.get())
            event.getWhoClicked().sendMessage(getConfig().getString("found-blacklisted-item-error"));
    }

}
