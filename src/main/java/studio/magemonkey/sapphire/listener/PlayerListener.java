package studio.magemonkey.sapphire.listener;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.compat.VersionManager;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.RangeUtil;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.sapphire.DarkRiseItems;
import studio.magemonkey.sapphire.Sapphire;
import studio.magemonkey.sapphire.cfg.PlayerData;
import studio.magemonkey.sapphire.cfg.SapphireConfig;
import studio.magemonkey.sapphire.cfg.VoucherManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@EventListener(DarkRiseEconomy.class)
public class PlayerListener implements Listener {
    private static final Set<InventoryAction> dropActions          = Sets.newIdentityHashSet();
    private static final Set<InventoryType>   tradeableInventories = Sets.newIdentityHashSet();

    static {
        dropActions.add(InventoryAction.DROP_ALL_CURSOR);
        dropActions.add(InventoryAction.DROP_ALL_SLOT);
        dropActions.add(InventoryAction.DROP_ONE_CURSOR);
        dropActions.add(InventoryAction.DROP_ONE_SLOT);
        tradeableInventories.add(InventoryType.ANVIL);
        tradeableInventories.add(InventoryType.CRAFTING);
        tradeableInventories.add(InventoryType.CREATIVE);
        tradeableInventories.add(InventoryType.ENDER_CHEST);
        tradeableInventories.add(InventoryType.ENCHANTING);
        tradeableInventories.add(InventoryType.PLAYER);
        tradeableInventories.add(InventoryType.WORKBENCH);
    }

    private final Map<UUID, DarkRiseItem> confirm      = new ConcurrentHashMap<>(10, 0.4F, 2);
    private final Map<UUID, Integer>      confirmTasks = new ConcurrentHashMap<>(10, 0.4F, 2);
    private final Sapphire                plugin;

    public PlayerListener(Sapphire plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        ItemStack    item     = event.getItem();
        DarkRiseItem riseItem = this.plugin.getItems().getItemByStack(item);
        Player       p        = event.getPlayer();
        if (riseItem == null)
            return;
        if (!riseItem.getPermission().isEmpty() && riseItem
                .getPermission().stream().anyMatch(s -> !p.hasPermission(s))) {
            if (riseItem.getPermissionMessage() != null && !riseItem.getPermissionMessage().isEmpty())
                p.sendMessage(riseItem.getPermissionMessage());
            return;
        }
        if (riseItem.isEnabledEnchantedDurability()) {
            double random    = RangeUtil.getRandomDouble(riseItem.chanceToLostDurability());
            int    forceLost = 0;
            if (random > 1.0D) {
                forceLost = (int) random;
                random -= forceLost;
            }
            if (RangeUtil.getChance(random))
                forceLost++;
            if (forceLost > 0)
                item.setDurability((short) (item.getDurability() + forceLost));
        }
        int removeOnUse = riseItem.isRemoveOnUse();
        if (removeOnUse > item.getAmount())
            if (!p.getInventory().containsAtLeast(riseItem.getItem(), removeOnUse))
                return;
        boolean                              isVoucher = VoucherManager.getInstance().isVoucher(item);
        Optional<VoucherManager.VoucherData> data      = VoucherManager.getInstance().getData(item);
        if (isVoucher && data.isPresent()) {
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.voucher.already-used",
                    event.getPlayer(),
                    new MessageData("voucher_id",
                            Integer.valueOf(data.get().id)));
            return;
        }
        if (riseItem.isConfirmOnUse()) {
            event.setCancelled(true);
            if (this.confirm.containsKey(p.getUniqueId())) {
                if (riseItem.equals(this.confirm.get(p.getUniqueId())))
                    return;
                this.confirm.remove(p.getUniqueId());
                Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                    Bukkit.getScheduler().cancelTask(i.intValue());
            }
            MessageData yes = new MessageData("yes",
                    CodexEngine.get().getMessageUtil().getMessageAsString("sapphire.yes", "yes"));
            MessageData no =
                    new MessageData("no", CodexEngine.get().getMessageUtil().getMessageAsString("sapphire.no", "no"));
            MessageData config    = new MessageData("sapphireConfig", new SapphireConfig());
            MessageData playerMsg = new MessageData("player", p);
            MessageData itemMsg   = new MessageData("riseItem", riseItem);
            CodexEngine.get()
                    .getMessageUtil()
                    .sendMessage("sapphire.useWithConfirm", p, yes, no, config, playerMsg, itemMsg);
            this.confirm.put(p.getUniqueId(), riseItem);
            this.confirmTasks.put(p.getUniqueId(), Integer.valueOf(Sapphire.getInstance().runTaskLater(() -> {
                if (this.confirm.remove(p.getUniqueId()) != null) {
                    this.confirmTasks.remove(p.getUniqueId());
                    CodexEngine.get()
                            .getMessageUtil()
                            .sendMessage("sapphire.timeout", p, yes, no, config, playerMsg, itemMsg);
                }
            }, this.plugin.getTimeout() * 20L).getTaskId()));
            return;
        }

        if (isVoucher) VoucherManager.getInstance().use(event.getPlayer(), item);

        if (removeOnUse > 0) {
            ItemStack removeItem = item.clone();
            removeItem.setAmount(removeOnUse);
            if (!p.getInventory().removeItem(new ItemStack[]{removeItem}).isEmpty()) return;
        }
        riseItem.invoke(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Sapphire.getInstance().runTaskLater(() -> PlayerData.loadPlayer(event.getPlayer()), 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.confirm.remove(event.getEntity().getUniqueId());

        this.confirmTasks.remove(event.getEntity().getUniqueId());
        if (event.getKeepInventory()) return;
        if (event.getDrops().isEmpty()) return;

        DarkRiseItems        items   = this.plugin.getItems();
        ArrayList<ItemStack> keeps   = new ArrayList();
        ArrayList<ItemStack> removes = new ArrayList();

        for (ItemStack itemStack : event.getDrops()) {
            DarkRiseItem riseItem = items.getItemByStack(itemStack);
            if (riseItem != null
                    && (!riseItem.isDropOnDeath() || !riseItem.canDrop())) {
                keeps.add(itemStack);
            }
        }

        if (!keeps.isEmpty()) event.getDrops().removeAll(keeps);

        PlayerData.dumpPlayer(event.getEntity(), keeps);

    /*

    DarkRiseItems items = this.plugin.getItems();
    Map<String, Integer> removes = new HashMap<>(20);
    for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
      ItemStack itemStack = iterator.next();
      DarkRiseItem riseItem = items.getItemByStack(itemStack);
      if (riseItem == null) continue;
      if (!riseItem.isDropOnDeath()) iterator.remove();


      * Old Code Broken

      int toRemove = ((Integer)removes.getOrDefault(riseItem.getId(), Integer.valueOf(riseItem.isRemoveOnDeath()))).intValue();
      if (toRemove == 0)
        continue;
      if (toRemove >= itemStack.getAmount()) {
        toRemove -= itemStack.getAmount();
        iterator.remove();
        if (toRemove > 0) {
          removes.put(riseItem.getId(), Integer.valueOf(toRemove));
          continue;
        }
        removes.remove(riseItem.getId());
        continue;
      }
      itemStack.setAmount(itemStack.getAmount() - toRemove);
      event.getDrops().remove(riseItem);

      event.getDrops().remove(riseItem);
      removes.remove(riseItem.getId());
      */
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.confirm.remove(event.getPlayer().getUniqueId());
        this.confirmTasks.remove(event.getPlayer().getUniqueId());
        PlayerData.loadPlayer(event.getPlayer());
        if (this.plugin.getItemsToAdd().containsKey(event.getPlayer().getUniqueId()) &&
                !this.plugin.getItemsToAdd().get(event.getPlayer().getUniqueId()).isEmpty())
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.claim.pending", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (this.confirm.containsKey(p.getUniqueId())) {
            String       yes = CodexEngine.get().getMessageUtil().getMessageAsString("sapphire.yes", "yes", true);
            String       no  = CodexEngine.get().getMessageUtil().getMessageAsString("sapphire.no", "no", true);
            DarkRiseItem item;
            if (event.getMessage().equalsIgnoreCase(yes) && (item = this.confirm.remove(p.getUniqueId())) != null) {
                Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                    Bukkit.getScheduler().cancelTask(i.intValue());
                event.setCancelled(true);
                DarkRiseItem powerItem = item;
                Sapphire.getInstance().runTask(() -> {
                    boolean used = (powerItem.isRemoveOnUse() == 0);
                    if (!used) {
                        ItemStack                   toRemove       = powerItem.getItem(powerItem.isRemoveOnUse());
                        int                         toRemoveAmount = toRemove.getAmount();
                        HashMap<Integer, ItemStack> removeResult   = p.getInventory().removeItem(toRemove);
                        used = removeResult.isEmpty();
                        if (!used) {
                            int notRemovedAmount = toRemove.getAmount();
                            int removedAmount    = toRemoveAmount - notRemovedAmount;
                            if (removedAmount != 0) {
                                toRemove.setAmount(removedAmount);
                                p.getInventory().addItem(toRemove);
                            }
                        }
                    }
                    if (used) {
                        CodexEngine.get().getMessageUtil().sendMessage("sapphire.used",
                                p,
                                new MessageData("no", no),
                                new MessageData("riseItem", powerItem));
                        powerItem.invoke(p);
                    } else {
                        CodexEngine.get().getMessageUtil().sendMessage("sapphire.canNotFindItem",
                                p,
                                new MessageData("no", no),
                                new MessageData("riseItem", powerItem));
                    }
                });
            } else if (event.getMessage().equalsIgnoreCase(no)
                    && (item = this.confirm.remove(p.getUniqueId())) != null) {
                Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                    Bukkit.getScheduler().cancelTask(i.intValue());
                event.setCancelled(true);
                CodexEngine.get().getMessageUtil().sendMessage("sapphire.cancel",
                        p,
                        new MessageData("no", no),
                        new MessageData("riseItem", item));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.confirm.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        DarkRiseItems items = Sapphire.getInstance().getItems();
        if (dropActions.contains(event.getAction()) && (
                !items.canDrop(event.getCursor()) || !items.canDrop(event.getCurrentItem()))) {
            event.setCancelled(true);
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.canNotDrop", event.getWhoClicked());
            return;
        }
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(VersionManager.getCompat().getTopInventory(event))
                || tradeableInventories.contains(event.getClickedInventory().getType())) {
            return;
        }

        DarkRiseItem item = null;
        if (event.getCursor() != null) {
            item = items.getItemByStack(event.getCursor());
        } else if (event.getCurrentItem() != null) {
            item = items.getItemByStack(event.getCurrentItem());
        }

        if (item == null || item.isTradeable()) return;

        if (event.getAction() == InventoryAction.PLACE_ALL || event
                .getAction() == InventoryAction.PLACE_ONE || event
                .getAction() == InventoryAction.PLACE_SOME) {
            event.setCancelled(true);
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.canNotTrade", event.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        DarkRiseItems items = Sapphire.getInstance().getItems();
        if (!event.getInventory().equals(VersionManager.getCompat().getTopInventory(event))
                || tradeableInventories.contains(event.getInventory().getType())) {
            return;
        }

        DarkRiseItem item = items.getItemByStack(event.getOldCursor());
        if (item == null || item.isTradeable()) return;

        event.setCancelled(true);
        CodexEngine.get().getMessageUtil().sendMessage("sapphire.canNotTrade", event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        DarkRiseItems items = Sapphire.getInstance().getItems();

        if (!items.canDrop(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.canNotDrop", event.getPlayer());
        }
    }
}
