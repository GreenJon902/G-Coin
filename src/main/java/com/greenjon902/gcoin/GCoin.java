package com.greenjon902.gcoin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class GCoin extends JavaPlugin implements Listener {
    Logger logger = this.getLogger();

    private static final int gcoinCustomModelData = 71;

    private static ItemStack gCoinItemStack;
    private static ItemStack gCoinNoteItemStack;
    private static ItemStack gCoinBlockItemStack;

    public static ItemStack getGCoinItemStack() {
        return gCoinItemStack;
    }

    public static ItemStack getGCoinNoteItemStack() {
        return gCoinNoteItemStack;
    }

    public static ItemStack getGCoinBlockItemStack() {
        return gCoinBlockItemStack;
    }

    @Override
    public void onEnable() {
        logger.info("G-Coin starting...");

        getCommand("givegcoin").setExecutor(new GiveGCoinCommand());
        getServer().getPluginManager().registerEvents(this, this);


        gCoinItemStack = new ItemStack(Material.SUNFLOWER);
        ItemMeta gCoinItemMeta = gCoinItemStack.getItemMeta();
        gCoinItemMeta.setCustomModelData(gcoinCustomModelData);
        gCoinItemMeta.displayName(Component.text("G-Coin", TextColor.color(255, 170, 0)).asComponent());
        gCoinItemStack.setItemMeta(gCoinItemMeta);

        gCoinNoteItemStack = new ItemStack(Material.GOLD_INGOT);
        ItemMeta gCoinNoteItemMeta = gCoinNoteItemStack.getItemMeta();
        gCoinNoteItemMeta.setCustomModelData(gcoinCustomModelData);
        gCoinNoteItemMeta.displayName(Component.text("G-Coin Note", TextColor.color(255, 170, 0)).asComponent());
        gCoinNoteItemStack.setItemMeta(gCoinNoteItemMeta);

        gCoinBlockItemStack = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta gCoinBlockItemMeta = gCoinBlockItemStack.getItemMeta();
        gCoinBlockItemMeta.setCustomModelData(gcoinCustomModelData);
        gCoinBlockItemMeta.displayName(Component.text("G-Block", TextColor.color(255, 170, 0)).asComponent());
        gCoinBlockItemStack.setItemMeta(gCoinBlockItemMeta);


        ShapedRecipe gCoinNoteShapedRecipe = new ShapedRecipe(new NamespacedKey(this,
                "gcoin_note"), gCoinNoteItemStack);
        gCoinNoteShapedRecipe.shape("C C", "CCC");
        gCoinNoteShapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(gCoinItemStack));

        ShapedRecipe gCoinBlockShapedRecipe = new ShapedRecipe(new NamespacedKey(this,
                "gcoin_block"), gCoinBlockItemStack);
        gCoinBlockShapedRecipe.shape("NNN", "NNN", "NNN");
        gCoinBlockShapedRecipe.setIngredient('N', new RecipeChoice.ExactChoice(gCoinNoteItemStack));

        Bukkit.addRecipe(gCoinNoteShapedRecipe);
        Bukkit.addRecipe(gCoinBlockShapedRecipe);

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
        if (event.getView().getType() == InventoryType.CRAFTING) {
            // 1 2  0
            // 3 4

            for (int i = 1; i < 5; i++) {
                ItemStack itemStack = event.getInventory().getItem(i);

                if (itemStack != null) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == gcoinCustomModelData) {
                        event.setCancelled(true);
                    }
                }
            }

        } else if (event.getView().getType() == InventoryType.WORKBENCH) {
            // 1 2 3 0
            // 4 5 6
            // 7 8 9

            for (int i = 1; i < 10; i++) {
                ItemStack itemStack = event.getInventory().getItem(i);

                if (itemStack != null) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == gcoinCustomModelData) {
                        event.setCancelled(true);
                    }
                }
            }

        }

        if (event.getInventory().getItem(0).getItemMeta().hasCustomModelData() &&
                event.getInventory().getItem(0).getItemMeta().getCustomModelData() == gcoinCustomModelData) {
            event.setCancelled(false);
        }
    }
}
