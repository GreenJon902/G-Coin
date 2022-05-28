package com.greenjon902.gcoin;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GCoinCraftingBookListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftingBookListener(PlayerRecipeBookClickEvent event) throws Exception {
        System.out.println(event.getRecipe());
        if (event.getRecipe().getNamespace().equalsIgnoreCase("g-coin")) {
            if (!(Bukkit.getRecipe(event.getRecipe()) instanceof ShapelessRecipe)) {
                throw new Exception("Not shapeless recipe found as a g-coin recipe");
            }

            ShapelessRecipe recipe = (ShapelessRecipe) Bukkit.getRecipe(event.getRecipe());
            if (recipe == null) {
                throw new NullPointerException("Recipe was null");
            }

            event.setCancelled(true);

            System.out.println(recipe);
            System.out.println(recipe.getResult());
            System.out.println(recipe.getIngredientList());

            ArrayList<ItemStack> oldIngredients = (ArrayList<ItemStack>) recipe.getIngredientList();
            HashMap<ItemStack, Integer> ingredients = new HashMap<>(); // have amounts for checking if inventory contains that
            for (ItemStack itemStack : oldIngredients) { // count occurrences
                if (!ingredients.containsKey(itemStack)) {
                    ingredients.put(itemStack, 1);
                } else {
                    ingredients.put(itemStack, ingredients.get(itemStack) + 1);
                }
            }
            System.out.println(oldIngredients);

            if (!(event.getPlayer().getOpenInventory().getTopInventory() instanceof CraftingInventory)) {
                throw new Exception("Inventory that player in is not a crafting inventory");
            }

            CraftingInventory inventory = (CraftingInventory) event.getPlayer().getOpenInventory().getTopInventory();
            Inventory bottomInventory = event.getPlayer().getOpenInventory().getBottomInventory();
            System.out.println(inventory);
            System.out.println(bottomInventory);

            boolean firstLoop = true;
            boolean go = true;
            while (go) {
                if (!event.isMakeAll()) {
                    go = false;
                }

                boolean notEnough = false;
                for (ItemStack itemStack : ingredients.keySet()) {
                    int amount = ingredients.get(itemStack);
                    System.out.println(itemStack);
                    System.out.println(amount);
                    System.out.println(event.getPlayer().getInventory().contains(itemStack, amount));

                    System.out.println("calc inv amount");
                    int amountNeededLeft = amount;
                    for (ItemStack playerItemStack : bottomInventory) {
                        if (playerItemStack != null && playerItemStack.isSimilar(itemStack)) {
                            amountNeededLeft -= playerItemStack.getAmount();
                            System.out.println(playerItemStack);
                            System.out.println(amountNeededLeft);
                        }
                    }

                    if (amountNeededLeft > 0) {
                        notEnough = true;
                    }
                }

                if (notEnough) {
                    break;
                }

                System.out.println("Remove");
                for (ItemStack itemStack : ingredients.keySet()) {
                    System.out.println( " ------------------");
                    System.out.println(itemStack);
                    int amountToRemove = ingredients.get(itemStack);
                    System.out.println(amountToRemove);
                    for (int i = 0; i < bottomInventory.getSize(); i++) {
                        System.out.println(i);
                        ItemStack playerItemStack = bottomInventory.getItem(i);
                        System.out.println(playerItemStack);

                        if (playerItemStack != null && playerItemStack.isSimilar(itemStack)) {
                            System.out.println("Simialr");
                            if (playerItemStack.getAmount() > amountToRemove) {
                                playerItemStack.setAmount(playerItemStack.getAmount() - amountToRemove);
                                amountToRemove = 0;
                                System.out.println(1);
                                System.out.println(playerItemStack.getAmount());
                                System.out.println(bottomInventory.getItem(i).getAmount());

                            } else {
                                amountToRemove -= playerItemStack.getAmount();
                                bottomInventory.setItem(i, null);
                                System.out.println(2);
                                System.out.println(amountToRemove);
                                System.out.println(bottomInventory.getItem(i));
                            }
                        }
                    }
                }




                System.out.println(inventory);
                System.out.println(Arrays.toString(inventory.getMatrix()));
                if (firstLoop) {
                    firstLoop = false;

                    for (ItemStack itemStack : inventory.getMatrix()) { // clear crafting matrix (move it back to inv)
                        System.out.println(itemStack);
                        if (itemStack != null) {
                            System.out.println(1);
                            bottomInventory.addItem(itemStack);
                        }
                    }
                    System.out.println("first");
                    System.out.println(Arrays.toString(inventory.getMatrix()));
                    inventory.setMatrix(recipe.getIngredientList().toArray(new ItemStack[9])); // fill the matrix with the ingredients
                    System.out.println(Arrays.toString(inventory.getMatrix()));
                } else {
                    System.out.println("second");
                    ItemStack[] matrix = inventory.getMatrix();
                    System.out.println(Arrays.toString(matrix));
                    for (ItemStack itemStack : matrix) { // recipe will be same as last time
                        if (itemStack != null){
                            itemStack.setAmount(itemStack.getAmount() + 1);
                        }
                    }

                    System.out.println(Arrays.toString(matrix));
                    System.out.println(Arrays.toString(inventory.getMatrix()));
                    inventory.setMatrix(matrix);
                    System.out.println(Arrays.toString(inventory.getMatrix()));
                }

                System.out.println(Arrays.toString(recipe.getIngredientList().toArray(new ItemStack[0])));
                System.out.println(recipe.getIngredientList().toArray(new ItemStack[0]).length);
                System.out.println(recipe.getResult());
                inventory.setMatrix(recipe.getIngredientList().toArray(new ItemStack[9]));
                inventory.setResult(recipe.getResult());
            }
        }
    }
}
