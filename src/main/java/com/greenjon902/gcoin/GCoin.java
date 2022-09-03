package com.greenjon902.gcoin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class GCoin extends JavaPlugin implements Listener {
    public static Logger logger;

    public static final List<Integer> gcoinCustomModelDatas = new ArrayList<>() {{
            add(711);
            add(715);
            add(7110);
            add(7120);
            add(71180);
    }};

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

        ArrayList<CraftingHelper> craftingHelpers = new ArrayList<>() {{
            add(new CraftingHelper("G-Coin", Material.SUNFLOWER, 0, 1));
            add(new CraftingHelper("5 G-Coins Note", Material.GOLD_INGOT, 5, 5));
            add(new CraftingHelper("10 G-Coins Note", Material.GOLD_INGOT, 2, 10));
            add(new CraftingHelper("20 G-Coins Note", Material.GOLD_INGOT, 2, 20));
            add(new CraftingHelper("G-Block", Material.GOLD_BLOCK, 9, 180));
        }};

        HashMap<String, ItemStack> items = new HashMap<>() {{
           put("G-Coin", gCoinItemStack);
        }};

        int n = 0;
        for (CraftingHelper craftingHelper : craftingHelpers) {
            ItemStack itemStack = new ItemStack(craftingHelper.material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(gcoinCustomModelDatas.get(n));
            n++;

            TextComponent name = Component.text(craftingHelper.name)
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)
                                .color(NamedTextColor.GOLD);

            itemMeta.displayName(name);

            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.text("Worth " + craftingHelper.value + " G-Coin"));
            lore.add(Component.text(""));
            lore.add(Component.text("The official ")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .append(Component.text("G-Dem SMP ")
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("currency"))
                    .decoration(TextDecoration.BOLD, false)
                    .decoration(TextDecoration.ITALIC, false));
            itemMeta.lore(lore);

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
                Boolean isGCoin = null;
                boolean foundBefore = false;
                for (int i = 0; i < inventory.getType().getDefaultSize(); i++) {

                    ItemStack item = inventory.getItem(i);
                    if (item != null) {

                        Integer customModelData = null;
                        if (item.getItemMeta().hasCustomModelData()) {
                            customModelData = item.getItemMeta().getCustomModelData();
                        }

                        if (foundBefore) { // Do we know what the custom model data should be
                            if (isGCoin ^ gcoinCustomModelDatas.contains(customModelData)) {
                                return true;
                            }
                        } else { // ran on first valid item
                            isGCoin = gcoinCustomModelDatas.contains(customModelData);
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
    public final int value;

    CraftingHelper(String name, Material material, int amountOfLast, int value) {
        this.name = name;
        this.material = material;
        this.amountOfLast = amountOfLast;
        this.value = value;
    }
}