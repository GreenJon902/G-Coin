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
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Logger;

public final class GCoin extends JavaPlugin implements Listener {
    Logger logger = this.getLogger();

    private static final int gcoinCustomModelData = 71;

    private static ItemStack gCoinItemStack;

    public static ItemStack getGCoinItemStack() {
        return gCoinItemStack;
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

        ArrayList<CraftingHelper> craftingHelpers = new ArrayList<>() {{
            add(new CraftingHelper("G-Coin", Material.SUNFLOWER, 0));
            add(new CraftingHelper("5 G-Coins Note", Material.GOLD_INGOT, 5));
            add(new CraftingHelper("10 G-Coins Note", Material.GOLD_INGOT, 2));
            add(new CraftingHelper("20 G-Coins Note", Material.GOLD_INGOT, 2));
            add(new CraftingHelper("G-Block", Material.GOLD_BLOCK, 9));
        }};
        HashMap<String, ItemStack> items = new HashMap<>() {{
           put("G-Coin", gCoinItemStack);
        }};

        for (CraftingHelper craftingHelper : craftingHelpers) {
            ItemStack itemStack = new ItemStack(craftingHelper.material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(gcoinCustomModelData);
            itemMeta.displayName(Component.text(craftingHelper.name, TextColor.color(255, 170, 0)).asComponent());
            itemStack.setItemMeta(itemMeta);

            items.put(craftingHelper.name, itemStack);
        }

        ItemStack last = items.get("G-Coin");
        for (int i = 1; i < craftingHelpers.size(); i++) {
            CraftingHelper craftingHelper = craftingHelpers.get(i);
            getLogger().info("Adding recipe for " + craftingHelper.name + " from " + last.toString());

            ItemStack itemStack = items.get(craftingHelper.name);


            String corrected_name = craftingHelper.name.toLowerCase(Locale.ROOT);
            corrected_name = corrected_name.replace(" ", "_");
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(this, "gcoin_craft_for_" + corrected_name), itemStack);
            shapelessRecipe.addIngredient(craftingHelper.amountOfLast, last);

            Bukkit.addRecipe(shapelessRecipe);
            last = itemStack;
        }

        last = items.get("G-Coin");
        for (int i = 1; i < craftingHelpers.size(); i++) {
            CraftingHelper craftingHelper = craftingHelpers.get(i);
            getLogger().info("Adding recipe from " + craftingHelper.name + " to " + last.toString());

            ItemStack itemStack = items.get(craftingHelper.name);

            ItemStack last_ = last.clone();
            last_.setAmount(craftingHelper.amountOfLast);

            String corrected_name = craftingHelper.name.toLowerCase(Locale.ROOT);
            corrected_name = corrected_name.replace(" ", "_");
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(this, "gcoin_craft_from_" + corrected_name), last_);
            shapelessRecipe.addIngredient(1, itemStack);

            Bukkit.addRecipe(shapelessRecipe);
            last = itemStack;
        }

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

class CraftingHelper {
    public final String name;
    public final Material material;
    public final int amountOfLast;

    CraftingHelper(String name, Material material, int amountOfLast) {
        this.name = name;
        this.material = material;
        this.amountOfLast = amountOfLast;
    }
}