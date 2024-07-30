package studio.magemonkey.sapphire;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItemImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DarkRiseItems {
    private static final String ALLOWED_CHARS = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM -_.";

    private final File dataFile = new File(Sapphire.getInstance().getDataFolder(), "items");

    private final Map<String, DarkRiseItem> itemsById = new ConcurrentHashMap<>(200);

    private final Map<String, File> itemFiles = new ConcurrentHashMap<>(200);

    private final Map<String, DarkRiseItem> itemsByName = new ConcurrentHashMap<>(200);

    private final SortedMap<String, DarkRiseItem> sortedItems = Collections.synchronizedSortedMap(new TreeMap<>());

    public Collection<DarkRiseItem> getItems() {
        return this.itemsById.values();
    }

    public SortedMap<String, DarkRiseItem> getSortedMap() {
        return this.sortedItems;
    }

    public DarkRiseItem addItem(String fileName, DarkRiseItem item, boolean save) {
        Validate.notNull(item, "Item can't be null");
        if (!fileName.toLowerCase().endsWith(".yml"))
            fileName = fileName + ".yml";
        StringBuilder sb = new StringBuilder(fileName.length());
        for (char c : fileName.toCharArray()) {
            if ("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM -_.".indexOf(c) != -1)
                sb.append(c);
        }
        File file = new File(this.dataFile, sb.toString());
        return addItem(file, item, save);
    }

    public DarkRiseItem addItem(File file, DarkRiseItem item, boolean save) {
        Validate.notNull(item, "Item can't be null");
        String lowerId = item.getId().toLowerCase().intern();
        if (file != null)
            this.itemFiles.put(lowerId, file);
        this.itemsByName.put(item.getName().toLowerCase().intern(), item);
        this.itemsById.put(lowerId, item);
        this.sortedItems.put(item.getId(), item);
        if (save)
            Sapphire.getInstance()
                    .getServer()
                    .getScheduler()
                    .runTaskAsynchronously(Sapphire.getInstance(), this::saveItems);
        return item;
    }

    public boolean removeItem(DarkRiseItem item, boolean save) {
        if (item == null)
            return false;
        DarkRiseItem remove = this.itemsById.remove(item.getId().toLowerCase());
        if (remove == null)
            return false;
        File file = this.itemFiles.remove(item.getId().toLowerCase());
        if (file != null) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            Collection<DarkRiseItemImpl> items = yaml.getMapList("items")
                    .stream()
                    .map(m -> new DarkRiseItemImpl((Map<String, Object>) m))
                    .collect(Collectors.toSet());
            items.remove(item);
            if (items.isEmpty()) {
                if (!file.delete())
                    file.deleteOnExit();
            } else {
                Collection<Map<String, Object>> toSave =
                        items.stream().map(DarkRiseItemImpl::serialize).collect(Collectors.toList());
                yaml.set("items", toSave);
                try {
                    yaml.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Can't remove item from file: " + file + ", error when saving file.");
                }
            }
        }
        this.sortedItems.remove(item.getId());
        boolean result = (this.itemsByName.remove(item.getName().toLowerCase()) != null);
        if (save)
            Sapphire.getInstance()
                    .getServer()
                    .getScheduler()
                    .runTaskAsynchronously(Sapphire.getInstance(), this::saveItems);
        return result;
    }

    public DarkRiseItem getItemByIdOrName(String id) {
        DarkRiseItem itemById = getItemById(id);
        if (itemById == null)
            return getItemByName(id);
        return itemById;
    }

    public DarkRiseItem getItemById(String id) {
        if (id == null)
            return null;
        return this.itemsById.get(id.toLowerCase());
    }

    public DarkRiseItem getItemByName(String name) {
        if (name == null)
            return null;
        return this.itemsByName.get(name.toLowerCase());
    }

    public boolean canDrop(ItemStack itemStack) {
        DarkRiseItem item = getItemByStack(itemStack);
        return (item == null || (item.canDrop() && item.isTradeable()));
    }

    public DarkRiseItem getItemByStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta())
            return getVanillaItemByStack(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!itemMeta.hasDisplayName())
            return getVanillaItemByStack(itemStack);
        return getItemByName(itemMeta.getDisplayName());
    }

    public DarkRiseItem getVanillaItemByStack(ItemStack itemStack) {
        return (itemStack == null) ? null : getItemById("vanilla_" + itemStack.getType().name());
    }

    public synchronized void saveItems() {
        Map<File, Collection<DarkRiseItem>> data = new HashMap<>(20);
        for (DarkRiseItem darkRiseItem : this.itemsById.values()) {
            if (darkRiseItem.isVanilla())
                continue;
            File                     file  = this.itemFiles.get(darkRiseItem.getId().toLowerCase());
            Collection<DarkRiseItem> items = data.computeIfAbsent(file, k -> new ArrayList(20));
            items.add(darkRiseItem/*.serialize()*/);
        }
        for (Map.Entry<File, Collection<DarkRiseItem>> entry : data.entrySet()) {
            try {
                File saveFile = entry.getKey();
                if (!saveFile.exists()) {
                    saveFile.getAbsoluteFile().getParentFile().mkdirs();
                    saveFile.createNewFile();
                }
                YamlConfiguration yml = new YamlConfiguration();
                yml.set("items", entry.getValue());
                yml.save(saveFile);
            } catch (IOException e) {
                Sapphire.getInstance().error("Can't save items to file: " + entry);
                e.printStackTrace();
            }
        }
    }

    public void loadItems(File dataFile) {
        this.itemFiles.clear();
        this.itemsById.clear();
        this.itemsByName.clear();
        this.sortedItems.clear();
        this.dataFile.mkdirs();
        File[] files = dataFile.listFiles();
        if (files == null || files.length == 0)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                loadItems(file);
            } else if (!file.getName().endsWith(".yml")) {
                continue;
            }
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.getList("items").forEach(i -> addItem(file, (DarkRiseItem) i, false));
            } catch (Exception e) {
                Sapphire.getInstance().getLogger().warning("Could not load " + file.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
            continue;
        }

        this.saveItems();
    }

    public void loadItems() {
        loadItems(this.dataFile);
        Sapphire.getInstance().info("Loaded " + this.sortedItems.size() + " items from " + this.itemFiles
                .size() + " files.");
        addVanillaItems();
    }

    private void addVanillaItems() {
        for (Material material : Material.values()) {
            if (!material.isItem()) continue;

            if (getItemById("vanilla_" + material.name()) == null) {
                DarkRiseItemImpl riseItem =
                        new DarkRiseItemImpl("vanilla_" + material.name().toLowerCase(), new ItemStack(material));
                addItem((File) null, riseItem, false);
            }
        }
    }
}
