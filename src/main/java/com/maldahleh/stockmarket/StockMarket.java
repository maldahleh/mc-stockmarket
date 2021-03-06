package com.maldahleh.stockmarket;

import com.maldahleh.stockmarket.api.StockMarketAPI;
import com.maldahleh.stockmarket.brokers.BrokerManager;
import com.maldahleh.stockmarket.commands.StockMarketCommand;
import com.maldahleh.stockmarket.commands.StockMarketTabCompleter;
import com.maldahleh.stockmarket.config.Messages;
import com.maldahleh.stockmarket.config.Settings;
import com.maldahleh.stockmarket.inventories.InventoryManager;
import com.maldahleh.stockmarket.placeholder.StocksPlaceholder;
import com.maldahleh.stockmarket.players.PlayerManager;
import com.maldahleh.stockmarket.processor.StockProcessor;
import com.maldahleh.stockmarket.stocks.StockManager;
import com.maldahleh.stockmarket.storage.Storage;
import com.maldahleh.stockmarket.storage.types.MySQL;
import com.maldahleh.stockmarket.storage.types.SQLite;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StockMarket extends JavaPlugin {
  private StockMarketAPI api;
  private Economy econ;

  private StockManager stockManager;
  private PlayerManager playerManager;

  @Override
  public void onEnable() {
    if (!setupEconomy()) {
      getLogger().severe("Vault/economy plugin not found.");
      return;
    }

    saveDefaultConfig();

    Storage storage;
    if (getConfig().getBoolean("storage.mysql.enabled", false)) {
      storage = new MySQL(getConfig().getConfigurationSection("storage.mysql"));
    } else {
      storage = new SQLite();
    }

    Settings settings = new Settings(getConfig().getConfigurationSection("settings"));
    Messages messages = new Messages(getConfig().getConfigurationSection("messages"), settings);
    this.stockManager = new StockManager(this, getConfig()
        .getConfigurationSection("stocks"), settings);
    this.playerManager = new PlayerManager(this, stockManager, storage, settings);
    this.api = new StockMarketAPI(playerManager);
    StockProcessor stockProcessor = new StockProcessor(this, stockManager,
        playerManager, storage, settings, messages);
    InventoryManager inventoryManager = new InventoryManager(this, playerManager,
        stockManager, stockProcessor, getConfig(), messages, storage, settings);
    BrokerManager brokerManager = new BrokerManager(this, getConfig()
        .getConfigurationSection("brokers"), inventoryManager);

    getCommand("stockmarket").setExecutor(new StockMarketCommand(this, brokerManager,
        stockProcessor, inventoryManager, messages));
    getCommand("stockmarket").setTabCompleter(new StockMarketTabCompleter());

    new MetricsLite(this);

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new StocksPlaceholder().register();
    }
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }

    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
        .getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }

    econ = rsp.getProvider();
    return true;
  }
}