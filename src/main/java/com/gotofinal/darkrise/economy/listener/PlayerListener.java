package com.gotofinal.darkrise.economy.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import com.gotofinal.darkrise.core.annotation.EventListener;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.economy.DarkRiseItems;
import com.gotofinal.darkrise.economy.cfg.PlayerData;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import org.diorite.utils.math.DioriteRandomUtils;

@EventListener(DarkRiseEconomy.class)
public class PlayerListener implements Listener
{
    private final Map<UUID, DarkRiseItem> confirm      = new ConcurrentHashMap<>(10, .4f, 2);
    private final Map<UUID, Integer>      confirmTasks = new ConcurrentHashMap<>(10, .4f, 2);

    private final DarkRiseEconomy plugin;

    public PlayerListener(final DarkRiseEconomy plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        if (! ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR)))
        {
            return;
        }
        ItemStack item = event.getItem();
        final DarkRiseItem riseItem = this.plugin.getItems().getItemByStack(item);
        final Player p = event.getPlayer();
        if (riseItem == null)
        {
            return;
        }

        if (!riseItem.getPermission().isEmpty() && riseItem.getPermission().stream().anyMatch(s -> !p.hasPermission(s)))
        {
            if (riseItem.getPermissionMessage() != null && !riseItem.getPermissionMessage().isEmpty())
            {
                p.sendMessage(riseItem.getPermissionMessage());
            }
            return;
        }

        if (riseItem.isEnabledEnchantedDurability())
        {
            double random = riseItem.chanceToLostDurability().getRandom();
            int forceLost = 0;
            if (random > 1)
            {
                forceLost = (int) random;
                random -= forceLost;
            }
            if (DioriteRandomUtils.getChance(random))
            {
                ++ forceLost;
            }
            if (forceLost > 0)
            {
                item.setDurability((short) (item.getDurability() + forceLost));
            }
        }
        int removeOnUse = riseItem.isRemoveOnUse();
        if (removeOnUse > item.getAmount())
        {
            if (! p.getInventory().containsAtLeast(riseItem.getItem(), removeOnUse))
            {
                return;
            }
        }
        if (riseItem.isConfirmOnUse())
        {
            event.setCancelled(true);
            if (this.confirm.containsKey(p.getUniqueId()))
            {
                if (riseItem.equals(this.confirm.get(p.getUniqueId())))
                {
                    return;
                }
                this.confirm.remove(p.getUniqueId());
                final Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                {
                    Bukkit.getScheduler().cancelTask(i);
                }
            }
//        event.setCancelled(true);
            MessageData yes = new MessageData("yes", this.plugin.getMessageAsString("economy.yes", "yes"));
            MessageData no = new MessageData("no", this.plugin.getMessageAsString("economy.no", "no"));
            MessageData config = new MessageData("economyConfig", this.plugin.getCfg());
            MessageData playerMsg = new MessageData("player", p);
            MessageData itemMsg = new MessageData("riseItem", riseItem);
            this.plugin.sendMessage("economy.useWithConfirm", p, yes, no, config, playerMsg, itemMsg);
            this.confirm.put(p.getUniqueId(), riseItem);
            this.confirmTasks.put(p.getUniqueId(), DarkRiseEconomy.getInstance().runTaskLater(() ->
                                                                                              {
                                                                                                  if (this.confirm.remove(p.getUniqueId()) != null)
                                                                                                  {
                                                                                                      this.confirmTasks.remove(p.getUniqueId());
                                                                                                      this.plugin.sendMessage("economy.timeout", p, yes, no,
                                                                                                                              config, playerMsg, itemMsg);
                                                                                                  }
                                                                                              }, this.plugin.getCfg().getTimeout() * 20).getTaskId());
            return;
        }
        if (removeOnUse > 0)
        {
            if (! p.getInventory().removeItem(riseItem.getItem(removeOnUse)).isEmpty())
            {
                return;
            }
        }
        riseItem.invoke(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event)
    {
        DarkRiseEconomy.getInstance().runTaskLater(() -> PlayerData.loadPlayer(event.getPlayer()), 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        this.confirm.remove(event.getEntity().getUniqueId());
        this.confirmTasks.remove(event.getEntity().getUniqueId());
        if (event.getKeepInventory())
        {
            return;
        }
        PlayerData.dumpPlayer(event.getEntity());
        DarkRiseItems items = this.plugin.getItems();
        Map<String, Integer> removes = new HashMap<>(20);
        for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext(); )
        {
            final ItemStack itemStack = iterator.next();
            DarkRiseItem riseItem = items.getItemByStack(itemStack);
            if (riseItem == null)
            {
                continue;
            }
            boolean dropOnDeath = riseItem.isDropOnDeath();
            if (! dropOnDeath)
            {
                iterator.remove();
            }
            int toRemove = removes.getOrDefault(riseItem.getId(), riseItem.isRemoveOnDeath());
            if (toRemove == 0)
            {
                continue;
            }
            if (toRemove >= itemStack.getAmount())
            {
                toRemove -= itemStack.getAmount();
                iterator.remove();
                if (toRemove > 0)
                {
                    removes.put(riseItem.getId(), toRemove);
                }
                else
                {
                    removes.remove(riseItem.getId());
                }
                continue;
            }
            itemStack.setAmount(itemStack.getAmount() - toRemove);
            removes.remove(riseItem.getId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        this.confirm.remove(event.getPlayer().getUniqueId());
        this.confirmTasks.remove(event.getPlayer().getUniqueId());
        PlayerData.loadPlayer(event.getPlayer());

        if (! plugin.getItemsToAdd().get(event.getPlayer().getUniqueId()).isEmpty())
        {
            DarkRiseEconomy.getInstance().sendMessage("economy.commands.claim.pending", event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(final AsyncPlayerChatEvent event)
    {
        final Player p = event.getPlayer();
        if (this.confirm.containsKey(p.getUniqueId()))
        {
            String yes = DarkRiseEconomy.getInstance().getMessageAsString("economy.yes", "yes");
            String no = DarkRiseEconomy.getInstance().getMessageAsString("economy.no", "no");
            DarkRiseItem item;
            if (event.getMessage().equalsIgnoreCase(yes) && ((item = this.confirm.remove(p.getUniqueId())) != null))
            {
                final Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                {
                    Bukkit.getScheduler().cancelTask(i);
                }
                event.setCancelled(true);
                final DarkRiseItem powerItem = item;
                DarkRiseEconomy.getInstance().runTask(
                        () ->
                        { // sync with main thread
                            boolean used = powerItem.isRemoveOnUse() == 0;
                            if (! used)
                            {
                                ItemStack toRemove = powerItem.getItem(powerItem.isRemoveOnUse());
                                int toRemoveAmount = toRemove.getAmount();
                                HashMap<Integer, ItemStack> removeResult = p.getInventory().removeItem(toRemove);
                                used = removeResult.isEmpty();
                                if (! used)
                                {
                                    int notRemovedAmount = toRemove.getAmount();
                                    int removedAmount = toRemoveAmount - notRemovedAmount;
                                    if (removedAmount != 0)
                                    {
                                        toRemove.setAmount(removedAmount);
                                        p.getInventory().addItem(toRemove);
                                    }
                                }
                            }
                            if (used)
                            {
                                DarkRiseEconomy.getInstance().sendMessage("economy.used", p, new MessageData("no", no), new MessageData("riseItem", powerItem));
                                powerItem.invoke(p);
                            }
                            else
                            {
                                DarkRiseEconomy.getInstance()
                                               .sendMessage("economy.canNotFindItem", p, new MessageData("no", no), new MessageData("riseItem", powerItem));
                            }
                        });
            }
            else if (event.getMessage().equalsIgnoreCase(no) && ((item = this.confirm.remove(p.getUniqueId())) != null))
            {
                final Integer i = this.confirmTasks.remove(p.getUniqueId());
                if (i != null)
                {
                    Bukkit.getScheduler().cancelTask(i);
                }
                event.setCancelled(true);
                DarkRiseEconomy.getInstance().sendMessage("economy.cancel", p, new MessageData("no", no), new MessageData("riseItem", item));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        this.confirm.remove(event.getPlayer().getUniqueId());
    }

    private static final Set<InventoryAction> dropActions = Sets.newIdentityHashSet();

    static
    {
        dropActions.add(InventoryAction.DROP_ALL_CURSOR);
        dropActions.add(InventoryAction.DROP_ALL_SLOT);
        dropActions.add(InventoryAction.DROP_ONE_CURSOR);
        dropActions.add(InventoryAction.DROP_ONE_SLOT);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(final InventoryClickEvent event)
    {
        DarkRiseItems items = DarkRiseEconomy.getInstance().getItems();
        boolean cursor = ! items.canDrop(event.getCursor());
        boolean current = ! items.canDrop(event.getCurrentItem());
        if (dropActions.contains(event.getAction()) && (cursor || current))
        {
            event.setCancelled(true);
            DarkRiseEconomy.getInstance().sendMessage("economy.canNotDrop", event.getWhoClicked());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event)
    {
        DarkRiseItems items = DarkRiseEconomy.getInstance().getItems();
        if (! items.canDrop(event.getItemDrop().getItemStack()))
        {
            event.setCancelled(true);
            DarkRiseEconomy.getInstance().sendMessage("economy.canNotDrop", event.getPlayer());
        }
    }

}
