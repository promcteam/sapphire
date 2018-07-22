package com.gotofinal.darkrise.economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.economy.item.DarkRiseItemImpl;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DarkRiseItems
{
    private static final String ALLOWED_CHARS = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM -_.";
    private final        File   dataFile      = new File(DarkRiseEconomy.getInstance().getDataFolder(), "items");

    private final Map<String, DarkRiseItem>       itemsById   = new ConcurrentHashMap<>(200);
    private final Map<String, File>               itemFiles   = new ConcurrentHashMap<>(200);
    private final Map<String, DarkRiseItem>       itemsByName = new ConcurrentHashMap<>(200);
    private final SortedMap<String, DarkRiseItem> sortedItems = Collections.synchronizedSortedMap(new TreeMap<>());

    public Collection<DarkRiseItem> getItems()
    {
        return this.itemsById.values();
    }

    public SortedMap<String, DarkRiseItem> getSortedMap()
    {
        return this.sortedItems;
    }

    public DarkRiseItem addItem(String fileName, DarkRiseItem item, boolean save)
    {
        Validate.notNull(item, "Item can't be null");
        if (! fileName.toLowerCase().endsWith(".yml"))
        {
            fileName += ".yml";
        }
        StringBuilder sb = new StringBuilder(fileName.length());
        for (final char c : fileName.toCharArray())
        {
            if (ALLOWED_CHARS.indexOf(c) != - 1)
            {
                sb.append(c);
            }
        }
        File file = new File(this.dataFile, sb.toString());
        return this.addItem(file, item, save);
    }

    public DarkRiseItem addItem(File file, DarkRiseItem item, boolean save)
    {
        Validate.notNull(item, "Item can't be null");
        Validate.notNull(file, "File can't be null");
        String lowerId = item.getId().toLowerCase().intern();
        this.itemFiles.put(lowerId, file);
        this.itemsByName.put(item.getName().toLowerCase().intern(), item);
        DarkRiseItem put = this.itemsById.put(lowerId, item);
        this.sortedItems.put(item.getId(), item);
        if (save)
        {
            DarkRiseEconomy.getInstance().runTaskAsynchronously(this::saveItems);
        }
        return put;
    }

    @SuppressWarnings("unchecked")
    public boolean removeItem(DarkRiseItem item, boolean save)
    {
        if (item == null)
        {
            return false;
        }
        DarkRiseItem remove = this.itemsById.remove(item.getId().toLowerCase());
        if (remove == null)
        {
            return false;
        }
        File file = this.itemFiles.remove(item.getId().toLowerCase());
        if (file != null)
        {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            Collection<DarkRiseItemImpl> items = yaml.getMapList("items")
                    .stream().map(m -> new DarkRiseItemImpl((Map<String, Object>) m)).collect(Collectors.toSet());
            //noinspection SuspiciousMethodCalls
            items.remove(item);
            if (items.isEmpty())
            {
                if (! file.delete())
                {
                    file.deleteOnExit();
                }
            }
            else
            {
                Collection<Map<String, Object>> toSave = items.stream()
                        .map(DarkRiseItemImpl::serialize).collect(Collectors.toList());

                yaml.set("items", toSave);
                try
                {
                    yaml.save(file);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Can't remove item from file: " + file + ", error when saving file.");
                }
            }
        }
        this.sortedItems.remove(item.getId());
        boolean result = this.itemsByName.remove(item.getName().toLowerCase()) != null;
        if (save)
        {
            DarkRiseEconomy.getInstance().runTaskAsynchronously(this::saveItems);
        }
        return result;
    }

    public DarkRiseItem getItemByIdOrName(String id)
    {
        DarkRiseItem itemById = this.getItemById(id);
        if (itemById == null)
        {
            return this.getItemByName(id);
        }
        return itemById;
    }

    public DarkRiseItem getItemById(String id)
    {
        if (id == null)
        {
            return null;
        }
        return this.itemsById.get(id.toLowerCase());
    }

    public DarkRiseItem getItemByName(String name)
    {
        if (name == null)
        {
            return null;
        }
        return this.itemsByName.get(name.toLowerCase());
    }

    public boolean canDrop(ItemStack itemStack)
    {
        DarkRiseItem item = this.getItemByStack(itemStack);
        if (item == null)
        {
            return true;
        }
        return item.canDrop() && item.isTradeable();
    }

    public DarkRiseItem getItemByStack(ItemStack itemStack)
    {
        if ((itemStack == null) || ! itemStack.hasItemMeta())
        {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (! itemMeta.hasDisplayName())
        {
            return null;
        }
        return this.getItemByName(itemMeta.getDisplayName());
    }

    public synchronized void saveItems()
    {
        Map<File, Collection<Map<String, Object>>> data = new HashMap<>(20);
        for (final DarkRiseItem darkRiseItem : this.itemsById.values())
        {
            File file = this.itemFiles.get(darkRiseItem.getId().toLowerCase());
            Collection<Map<String, Object>> items = data.computeIfAbsent(file, k -> new ArrayList<>(20));
            items.add(darkRiseItem.serialize());
        }
        for (final Entry<File, Collection<Map<String, Object>>> entry : data.entrySet())
        {
            try
            {
                File saveFile = entry.getKey();
                if (! saveFile.exists())
                {
                    saveFile.getAbsoluteFile().getParentFile().mkdirs();
                    saveFile.createNewFile();
                }
                YamlConfiguration yml = new YamlConfiguration();
                yml.set("items", entry.getValue());
                yml.save(saveFile);
            }
            catch (IOException e)
            {
                DarkRiseCore.getInstance().error("Can't save items to file: " + entry);
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadItems(File dataFile)
    {
        this.itemFiles.clear();
        this.itemsById.clear();
        this.itemsByName.clear();
        this.sortedItems.clear();

        this.dataFile.mkdirs();
        File[] files = dataFile.listFiles();
        if ((files == null) || (files.length == 0))
        {
            return;
        }
        for (final File file : files)
        {
            if (file.isDirectory())
            {
                this.loadItems(file);
            }
            else if (! file.getName().endsWith(".yml"))
            {
                continue;
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.getMapList("items")
                    .stream()
                    .map(m -> new DarkRiseItemImpl((Map<String, Object>) m))
                    .forEach(i -> this.addItem(file, i, false));
        }
        this.saveItems();
    }

    @SuppressWarnings("unchecked")
    public void loadItems()
    {
        this.loadItems(this.dataFile);
        DarkRiseEconomy.getInstance().info("Loaded " + sortedItems.size()
                + " items from " + itemFiles.size() + " files.");
    }
}
