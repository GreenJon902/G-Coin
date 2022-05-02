package com.greenjon902.gcoin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class GCoin extends JavaPlugin {
    Logger logger = this.getLogger();

    public static final int gcoinCustomModelData = 71;

    @Override
    public void onEnable() {
        logger.info("G-Coin starting...");

        getCommand("getgcoin").setExecutor(new GiveGCoinCommand());
        getServer().getPluginManager().registerEvents(new Listener(), this);

        logger.info("G-Coin started!");
    }

    @Override
    public void onDisable() {
        logger.info("G-Coin stopping!");
    }
}
