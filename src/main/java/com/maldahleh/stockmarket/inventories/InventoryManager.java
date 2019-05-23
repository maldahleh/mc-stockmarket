package com.maldahleh.stockmarket.inventories;

import com.maldahleh.stockmarket.StockMarket;
import com.maldahleh.stockmarket.config.Messages;
import com.maldahleh.stockmarket.config.Settings;
import com.maldahleh.stockmarket.inventories.list.ListInventory;
import com.maldahleh.stockmarket.inventories.lookup.LookupInventory;
import com.maldahleh.stockmarket.inventories.tutorial.TutorialInventory;
import com.maldahleh.stockmarket.stocks.StockManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class InventoryManager {
  private final LookupInventory lookupInventory;
  private final ListInventory listInventory;
  private final TutorialInventory tutorialInventory;

  public InventoryManager(StockMarket stockMarket, StockManager stockManager,
      FileConfiguration config, Messages messages, Settings settings) {
    this.lookupInventory = new LookupInventory(stockMarket, stockManager, messages, settings,
        config.getConfigurationSection("inventories.lookup"));
    this.tutorialInventory = new TutorialInventory(stockMarket, config
        .getConfigurationSection("inventories.tutorial"));
    this.listInventory = new ListInventory(stockMarket, lookupInventory, config
        .getConfigurationSection("inventories.list"));
  }

  public void openLookupInventory(Player player, String symbol) {
    lookupInventory.openInventory(player, symbol);
  }

  public void openListInventory(Player player) {
    listInventory.openInventory(player);
  }

  public void openTutorialInventory(Player player) {
    tutorialInventory.openInventory(player);
  }
}
