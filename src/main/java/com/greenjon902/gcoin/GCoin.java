package com.greenjon902.gcoin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sound.midi.Track;
import java.util.logging.Logger;

public final class GCoin extends JavaPlugin implements Listener {
    Logger logger = this.getLogger();

    public static final int gcoinCustomModelData = 71;

    @Override
    public void onEnable() {
        logger.info("G-Coin starting...");

        getCommand("givegcoin").setExecutor(new GiveGCoinCommand());
        getServer().getPluginManager().registerEvents(this, this);

        logger.info("G-Coin started!");
    }

    @Override
    public void onDisable() {
        logger.info("G-Coin stopping!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta().hasCustomModelData() &&
                event.getItemInHand().getItemMeta().getCustomModelData() == gcoinCustomModelData) {
            event.setCancelled(true);
            logger.info(event.getPlayer().getName() + " tried playing a g-coin item!");
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        System.out.println(1);
        System.out.println(event.getCurrentItem());
        System.out.println(event.getCurrentItem().getItemMeta());
        if (event.getCurrentItem().getItemMeta().hasCustomModelData()) {
            System.out.println(event.getCurrentItem().getItemMeta().getCustomModelData());
        } else {
            System.out.println((String) null);
        }
        System.out.println(event.getCurrentItem().getType());
        System.out.println(event.getSlot());
        System.out.println(event.getRawSlot());
        System.out.println(event.getView().convertSlot(event.getRawSlot()));
        System.out.println(event.getSlotType());
        System.out.println(event.getView().getType());
        System.out.println(event.getWhoClicked().getInventory().getItem(event.getView().convertSlot(event.getRawSlot())));
        System.out.println(event.getInventory().getItem(event.getView().convertSlot(event.getRawSlot())));
        event.setCancelled(true);
        System.out.println(event.getWhoClicked().getInventory().getItem(event.getView().convertSlot(event.getRawSlot())));
        System.out.println(event.getInventory().getItem(event.getView().convertSlot(event.getRawSlot())));
        event.setCancelled(false);
        System.out.println(event.getInventory().getItem(0));
        System.out.println(event.getInventory().getItem(1));
        System.out.println(event.getInventory().getItem(2));
        System.out.println(event.getInventory().getItem(3));
        System.out.println(event.getInventory().getItem(4));
    }
}
