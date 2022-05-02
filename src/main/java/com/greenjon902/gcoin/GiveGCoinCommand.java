package com.greenjon902.gcoin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveGCoinCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player recipient;
        Integer amount;

        if (args.length == 1 && sender instanceof Player) {
            recipient = (Player) sender;
            amount = Integer.parseInt(args[0]);

        } else if (args.length == 2) {
            recipient = Bukkit.getServer().getPlayer(args[0]);

            if (recipient == null) {
                sender.sendMessage("/givegcoin's first argument is a player");
                return true;
            }
            amount = Integer.parseInt(args[1]);
        } else {
            sender.sendMessage("/givegcoin takes one (if sent by a player) or two arguments");
            return true;
        }

        ItemStack itemStack = new ItemStack(Material.SUNFLOWER, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(GCoin.gcoinCustomModelData);
        itemStack.setItemMeta(itemMeta);
        recipient.getInventory().addItem(itemStack);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> stuff = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                stuff.add(player.getName());
            }
        }

        return stuff;
    }
}
