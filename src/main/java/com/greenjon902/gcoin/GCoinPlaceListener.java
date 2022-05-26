package com.greenjon902.gcoin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class GCoinPlaceListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta().hasCustomModelData() &&
                event.getItemInHand().getItemMeta().getCustomModelData() == GCoin.gcoinCustomModelData) {
            event.setCancelled(true);
            GCoin.logger.info(event.getPlayer().getName() + " tried playing a g-coin item!");
        }
    }
}
