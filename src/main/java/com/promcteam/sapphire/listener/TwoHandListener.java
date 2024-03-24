package com.promcteam.sapphire.listener;

import org.bukkit.event.Listener;

//@EventListener(DarkRiseEconomy.class)
public class TwoHandListener implements Listener {
//    @EventHandler
//    public void onInventoryDrag(InventoryDragEvent event) {
//        ItemStack item = event.getNewItems().getOrDefault(Integer.valueOf(45), null);
//        if (item == null)
//            return;
//        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
//        if (darkRiseItem != null && darkRiseItem.isTwoHand())
//            event.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        ItemStack item = event.getCursor();
//        if (event.getAction() == InventoryAction.HOTBAR_SWAP)
//            item = event.getClickedInventory().getItem(event.getSlot());
//        if (item == null)
//            return;
//        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
//        Optional<DarkRiseItem> offHandItem = Optional.ofNullable(
//                DarkRiseEconomy.getInstance()
//                        .getItems()
//                        .getItemByStack(event.getWhoClicked().getInventory().getItemInOffHand()));
//        if (darkRiseItem == null || !darkRiseItem.isTwoHand())
//            return;
//        if (event.getRawSlot() == 45 || (offHandItem
//                .isPresent() && darkRiseItem
//                .isTwoHand() && (event
//                .getAction() == InventoryAction.HOTBAR_SWAP || event
//                .getAction() == InventoryAction.PLACE_SOME || event
//                .getAction() == InventoryAction.PLACE_ONE || event
//                .getAction() == InventoryAction.PLACE_ALL) && (event
//                .getHotbarButton() == event.getWhoClicked().getInventory().getHeldItemSlot() || event
//                .getWhoClicked().getInventory().getHeldItemSlot() == event.getSlot())))
//            event.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
//        ItemStack item = event.getOffHandItem();
//        if (item == null)
//            return;
//        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
//        if (darkRiseItem != null && darkRiseItem.isTwoHand())
//            event.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onEntityPickupItem(EntityPickupItemEvent event) {
//        ItemStack item = event.getItem().getItemStack();
//        if (item == null || !(event.getEntity() instanceof Player))
//            return;
//        Player player = (Player) event.getEntity();
//        if (player.getInventory().getItemInOffHand() == null || player
//                .getInventory().getItemInOffHand().getType() == Material.AIR)
//            return;
//        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
//        if (darkRiseItem != null && darkRiseItem.isTwoHand() && player
//                .getInventory().firstEmpty() == player.getInventory().getHeldItemSlot())
//            event.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
//        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
//        if (item == null || event.getPlayer().getInventory().getItemInOffHand() == null || event
//                .getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR)
//            return;
//        DarkRiseItem darkRiseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
//        if (darkRiseItem != null && darkRiseItem.isTwoHand())
//            event.setCancelled(true);
//    }
}
