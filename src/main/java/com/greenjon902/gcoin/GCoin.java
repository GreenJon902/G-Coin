package com.greenjon902.gcoin;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta().hasCustomModelData() &&
                event.getItemInHand().getItemMeta().getCustomModelData() == gcoinCustomModelData) {
            event.setCancelled(true);
            logger.info(event.getPlayer().getName() + " tried playing a g-coin item!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockGCoinInCertainInventories(InventoryDragEvent event) {
        for (int slot : event.getNewItems().keySet()) {
            ItemStack itemStack = event.getNewItems().get(slot);
            blockGCoinInCertainInventories(event, slot, itemStack);
        }
        System.out.println(4);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockGCoinInCertainInventories(InventoryClickEvent event) {
        blockGCoinInCertainInventories(event, event.getSlot(), event.getCursor());
        System.out.println(3);
    }


    public void blockGCoinInCertainInventories(InventoryInteractEvent event, int slot, ItemStack added) {
        Inventory inventory = event.getInventory();
        System.out.println(inventory);

        if (added == null) {
            return;
        }

        if (added.hasItemMeta() && added.getItemMeta().hasCustomModelData() && added.getItemMeta().getCustomModelData() == gcoinCustomModelData &&
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
                    System.out.println(1);
                    event.setCancelled(true);
                    break;
            } // Crafting gets handled elsewhere
        }
    }

    /**
     * Checks whether the crafting is impossible
     */
    public Boolean checkIllegalCraft(Inventory inventory) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fixResult(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        ItemStack resultBefore = inventory.getResult();
        if (event.getRecipe() != null) { // no recipe, no output, no problem
            inventory.setResult(event.getRecipe().getResult()); // make the inventory up to date

            if (checkIllegalCraft(inventory)) {
                System.out.println(2);
                inventory.setResult(new ItemStack(Material.AIR));
            } else {
                inventory.setResult(resultBefore);
            }
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