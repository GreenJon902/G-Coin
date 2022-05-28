package com.greenjon902.gcoin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class GCoin extends JavaPlugin implements Listener {
    public static Logger logger;

    public static final int gcoinCustomModelData = 71;

    private static ItemStack gCoinItemStack;

    public static ItemStack getGCoinItemStack() {
        return gCoinItemStack;
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("G-Coin starting...");

        getCommand("givegcoin").setExecutor(new GiveGCoinCommand());
        getServer().getPluginManager().registerEvents(new GCoinModificationListener(), this);
        getServer().getPluginManager().registerEvents(new GCoinPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new GCoinCraftingBookListener(), this);

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
            itemMeta.displayName(Component.text(craftingHelper.name,
                    TextColor.color(255, 171, 0)));
            itemStack.setItemMeta(itemMeta);

            items.put(craftingHelper.name, itemStack);
        }

        gCoinItemStack = items.get("G-Coin");

        ItemStack last = items.get("G-Coin");
        for (int i = 1; i < craftingHelpers.size(); i++) {
            CraftingHelper craftingHelper = craftingHelpers.get(i);
            getLogger().info("Adding recipe for " + craftingHelper.name + " from " + last.toString());

            ItemStack itemStack = items.get(craftingHelper.name);


            String corrected_name = craftingHelper.name.toLowerCase(Locale.ROOT);
            corrected_name = corrected_name.replace(" ", "_");
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(this, "gcoin_craft_for_" + corrected_name), itemStack);
            shapelessRecipe = shapelessRecipe.addIngredient(craftingHelper.amountOfLast, last);

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
            shapelessRecipe = shapelessRecipe.addIngredient(1, itemStack);

            Bukkit.addRecipe(shapelessRecipe);
            last = itemStack;
        }

        logger.info("G-Coin started!");
    }

    @Override
    public void onDisable() {
        logger.info("G-Coin stopping!");
    }


    /**
     * Checks whether the crafting is impossible
     */
    public static Boolean checkIllegalCraft(Inventory inventory) {
        switch (inventory.getType()) {
            case WORKBENCH:
            case CRAFTING: // check that the crafting
                Integer customModelData = -1; // null if no model data, -1 if no item has been found yet
                boolean foundBefore = false;
                for (int i = 0; i < inventory.getType().getDefaultSize(); i++) { // check that all values have that same customModelData

                    ItemStack item = inventory.getItem(i);
                    if (item != null) {

                        Integer customModelData2 = null;
                        if (item.getItemMeta().hasCustomModelData()) {
                            customModelData2 = item.getItemMeta().getCustomModelData();
                        }

                        if (foundBefore) { // Do we know what the custom model data should be
                            if (!Objects.equals(customModelData, customModelData2)) {
                                return true;
                            }
                        } else { // ran on first valid item
                            customModelData = customModelData2;
                            foundBefore = true;
                        }
                    }
                }

                break;
        }
        return false;
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