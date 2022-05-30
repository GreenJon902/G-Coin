package com.greenjon902.gcoin;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GCoinModificationListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockGCoinInCertainInventories(InventoryDragEvent event) {
        for (int slot : event.getNewItems().keySet()) {
            ItemStack itemStack = event.getNewItems().get(slot);
            blockGCoinInCertainInventories(event, slot, itemStack);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockGCoinInCertainInventories(InventoryClickEvent event) {
        blockGCoinInCertainInventories(event, event.getSlot(), event.getCursor());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fixResult(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack resultBefore = inventory.getResult();
        if (event.getRecipe() != null) { // no recipe, no output, no problem
            inventory.setResult(event.getRecipe().getResult()); // make the inventory up to date

            if (GCoin.checkIllegalCraft(inventory)) {
                inventory.setResult(new ItemStack(Material.AIR));
            } else {
                inventory.setResult(resultBefore);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fixResult(PrepareResultEvent event) { // Incease it gets into an anvil
        Inventory inventory = event.getInventory();

        for (ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData() && itemStack.getItemMeta().getCustomModelData() == GCoin.gcoinCustomModelData) {
                event.setResult(new ItemStack(Material.AIR));
                break;
            }
        }
    }

    public void blockGCoinInCertainInventories(InventoryInteractEvent event, int slot, ItemStack added) {
        Inventory inventory = event.getInventory();

        if (added == null) {
            return;
        }

        if (added.hasItemMeta() && added.getItemMeta().hasCustomModelData() && added.getItemMeta().getCustomModelData() == GCoin.gcoinCustomModelData &&
                slot < inventory.getType().getDefaultSize()) {
            switch (inventory.getType()) {
                case BREWING: // G-Coin should never be used with these so cancel all
                case SMITHING:
                case SMOKER:
                case STONECUTTER:
                case GRINDSTONE:
                case BLAST_FURNACE:
                case CARTOGRAPHY:
                case FURNACE:
                case ENCHANTING:
                case LOOM:
                case MERCHANT:
                case ANVIL:
                case BEACON:
                    event.setCancelled(true);
                    break;
            } // Crafting gets handled elsewhere
        }
    }
}
