package com.gotofinal.darkrise.economy.listener;

import com.gotofinal.darkrise.core.annotation.EventListener;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@EventListener(DarkRiseEconomy.class)
public class TwoHandListener implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event)
    {
        ItemStack item = event.getNewItems().getOrDefault(45, null);

        if (item == null)
        {
            return;
        }

        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);

        if (darkRiseItem != null && darkRiseItem.isTwoHand())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        ItemStack item = event.getCursor();

        if (event.getAction() == InventoryAction.HOTBAR_SWAP)
        {
            item = event.getClickedInventory().getItem(event.getSlot());
        }

        if (item == null)
        {
            return;
        }

        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
        Optional<DarkRiseItem> offHandItem = Optional.ofNullable(DarkRiseEconomy.getInstance().getItems().getItemByStack(event.getWhoClicked().getInventory().getItemInOffHand()));

        if (darkRiseItem == null || !darkRiseItem.isTwoHand())
        {
            return;
        }

        if (event.getRawSlot() == 45
                || (offHandItem.isPresent()
                && darkRiseItem.isTwoHand()
                && ((event.getAction() == InventoryAction.HOTBAR_SWAP
                    || event.getAction() == InventoryAction.PLACE_SOME
                    || event.getAction() == InventoryAction.PLACE_ONE
                    || event.getAction() == InventoryAction.PLACE_ALL)
                && (event.getHotbarButton() == event.getWhoClicked().getInventory().getHeldItemSlot()
                    || event.getWhoClicked().getInventory().getHeldItemSlot() == event.getSlot()))))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
    {
        ItemStack item = event.getOffHandItem();

        if (item == null)
        {
            return;
        }

        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);

        if (darkRiseItem != null && darkRiseItem.isTwoHand())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event)
    {
        ItemStack item = event.getItem().getItemStack();

        if (item == null || !(event.getEntity() instanceof Player))
        {
            return;
        }

        Player player = (Player) event.getEntity();

        if (player.getInventory().getItemInOffHand() == null
                || player.getInventory().getItemInOffHand().getType() == Material.AIR)
        {
            return;
        }

        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);

        if (darkRiseItem != null && darkRiseItem.isTwoHand()
                && player.getInventory().firstEmpty() == player.getInventory().getHeldItemSlot())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event)
    {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (item == null || event.getPlayer().getInventory().getItemInOffHand() == null
                || event.getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR)
        {
            return;
        }

        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);

        if (darkRiseItem != null && darkRiseItem.isTwoHand())
        {
            event.setCancelled(true);
        }
    }
}
