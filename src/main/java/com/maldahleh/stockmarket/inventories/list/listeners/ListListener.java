package com.maldahleh.stockmarket.inventories.list.listeners;

import com.maldahleh.stockmarket.inventories.list.ListInventory;
import com.maldahleh.stockmarket.inventories.lookup.LookupInventory;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class ListListener implements Listener {
  private final ListInventory inventory;
  private final LookupInventory lookupInventory;

  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (!(e.getWhoClicked() instanceof Player)
        || !inventory.isActive(e.getWhoClicked())) {
      return;
    }

    e.setCancelled(true);
    String symbol = inventory.getSymbol(e.getRawSlot());
    if (symbol == null) {
      return;
    }

    lookupInventory.openInventory((Player) e.getWhoClicked(), symbol.toUpperCase());
  }

  @EventHandler
  public void onDrag(InventoryDragEvent e) {
    if (!inventory.isActive(e.getWhoClicked())) {
      return;
    }

    e.setCancelled(true);
  }

  @EventHandler
  public void onClose(InventoryCloseEvent e) {
    inventory.remove(e.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    inventory.remove(e.getPlayer());
  }
}
