package com.gotofinal.darkrise.economy.cfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gotofinal.darkrise.spigot.core.utils.item.ItemBuilder;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerData
{
    private static final Map<UUID, List<ItemBuilder>> items = new HashMap<>(20, .4f);
    private static File              dataFile;
    private static FileConfiguration cfg; // if server crashes/stops when player die, to have sure that he don't lose items

    private PlayerData()
    {
    }

    @SuppressWarnings("unchecked")
    public static void init()
    {
        dataFile = new File(DarkRiseEconomy.getInstance().getDataFolder(), "players.yml");
        if (! dataFile.exists())
        {
            dataFile.getAbsoluteFile().getParentFile().mkdirs();
            try
            {
                dataFile.createNewFile();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }
        }
        cfg = YamlConfiguration.loadConfiguration(dataFile);

        for (final String sUuid : cfg.getKeys(false))
        {
            final List<ItemBuilder> its = (List<ItemBuilder>) cfg.getList(sUuid);
            if ((its == null) || its.isEmpty())
            {
                continue;
            }
            items.put(UUID.fromString(sUuid), its);
        }
    }

    public static void dumpPlayer(final Player player)
    {
        final List<ItemBuilder> items = new ArrayList<>(5);
        Map<String, Integer> removes = new HashMap<>(20);
        for (final ItemStack item : player.getInventory().getContents())
        {
            final DarkRiseItem riseItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
            if ((riseItem == null) || (riseItem.isDropOnDeath()))
            {
                continue;
            }
            int toRemove = removes.getOrDefault(riseItem.getId(), riseItem.isRemoveOnDeath());
            if (toRemove >= item.getAmount())
            {
                toRemove -= item.getAmount();
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
            item.setAmount(item.getAmount() - toRemove);
            removes.remove(riseItem.getId());
            items.add(ItemBuilder.newItem(item));

        }
        for (final ItemStack item : player.getInventory().getContents())
        {
            final DarkRiseItem powerItem = DarkRiseEconomy.getInstance().getItems().getItemByStack(item);
            if ((powerItem == null) || ! powerItem.isDropOnDeath())
            {
                continue;
            }
            items.add(ItemBuilder.newItem(item));
        }
        if (items.isEmpty())
        {
            return;
        }
        PlayerData.items.put(player.getUniqueId(), items);
        cfg.set(player.getUniqueId().toString(), items);
        try
        {
            cfg.save(dataFile);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadPlayer(final Player player)
    {
        final List<ItemBuilder> items = PlayerData.items.remove(player.getUniqueId());
        if ((items == null) || items.isEmpty())
        {
            return;
        }
        cfg.set(player.getUniqueId().toString(), null);
        try
        {
            cfg.save(dataFile);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        player.getInventory().addItem(items.stream().map(ItemBuilder::build).toArray(ItemStack[]::new));
    }
}
