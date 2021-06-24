package com.gotofinal.darkrise.economy;

import com.gotofinal.darkrise.economy.cfg.EconomyConfig;
import com.gotofinal.darkrise.economy.cfg.PlayerData;
import com.gotofinal.darkrise.economy.cfg.VoucherManager;
import me.travja.darkrise.core.ConfigManager;
import me.travja.darkrise.core.item.DarkRiseItem;
import me.travja.darkrise.core.legacy.killme.chat.placeholder.PlaceholderType;
import me.travja.darkrise.core.legacy.util.Init;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.legacy.util.message.NMSPlayerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DarkRiseEconomy extends JavaPlugin {
    public static final PlaceholderType<DarkRiseItem> RISE_ITEM = PlaceholderType.create("riseItem", DarkRiseItem.class);
    public static final PlaceholderType<EconomyConfig> ECONOMY_CONFIG = PlaceholderType.create("economyConfig", EconomyConfig.class);
    private static DarkRiseEconomy instance;
    //private EconomyConfig config;
    private final File itemsToAddFile;

    private final Map<UUID, Map<DarkRiseItem, Integer>> itemsToAdd;
    private DarkRiseItems items;
    private FileConfiguration config;

    public DarkRiseEconomy() {
        this.itemsToAddFile = new File(getDataFolder(), "itemstoadd.yml");
        this.itemsToAdd = new HashMap<>();
        instance = this;
    }

    public static DarkRiseEconomy getInstance() {
        return instance;
    }

    public static DarkRiseItems getItemsRegistry() {
        return instance.items;
    }

    public FileConfiguration/*EconomyConfig*/ getCfg() {
        return this.config;
    }

    public int getTimeout() {
        return config.getInt("timeout");
    }

    public DarkRiseItems getItems() {
        return this.items;
    }

    public void reloadConfigs() {
        reloadConfig();
        this.items = new DarkRiseItems();
        this.items.loadItems();
        this.config = ConfigManager.loadConfigFile(new File(getDataFolder(), "config.yml"), getResource("config.yml"));
        FileConfiguration lang = ConfigManager.loadConfigFile(new File(getDataFolder() + File.separator + "lang", "lang_en.yml"), getResource("lang/lang_en.yml"));
        MessageUtil.reload(lang, this);
        //this.config = (EconomyConfig) loadConfigFile(new File(getDataFolder(), "config.yml"), EconomyConfig.class);
    }

    public void saveConfigs() {
        this.items.saveItems();
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().isPluginEnabled("PlayerPoints"))
            Init.PLAYER.registerItem("points", CurrencyType.POINTS::get);
        RISE_ITEM.registerItem("name", item -> {
            TextComponent textComponent = new TextComponent(item.getName());
            textComponent.setHoverEvent(NMSPlayerUtils.convert(item.getItem()));
            return textComponent;
        });
        RISE_ITEM.registerItem("displayName", item -> {
            TextComponent textComponent = new TextComponent(item.getName());
            textComponent.setHoverEvent(NMSPlayerUtils.convert(item.getItem()));
            return textComponent;
        });
        RISE_ITEM.registerItem("material", d -> d.getItem().getType());
        RISE_ITEM.registerItem("id", item -> {
            TextComponent textComponent = new TextComponent(item.getId());
            textComponent.setHoverEvent(NMSPlayerUtils.convert(item.getItem()));
            return textComponent;
        });
        RISE_ITEM.registerItem("lore", c -> StringUtils.join(c.getItem().getItemMeta().getLore(), '\n'));
        RISE_ITEM.registerItem("enchantments", c -> StringUtils.join(c.getItem().getEnchantments().keySet(), ", "));
        ECONOMY_CONFIG.registerItem("timeout", EconomyConfig::getTimeout);
        RISE_ITEM.registerChild("item", Init.ITEM, DarkRiseItem::getItem);
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ConfigurationSerialization.registerClass(Price.class);
        PlayerData.init();
        reloadConfigs();
        loadItemsToAdd();
        try {
            VoucherManager.getInstance().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Register.register(this); //Register our events and our commands.
    }

    @Override
    public void onDisable() {
        super.onDisable();
        saveConfigs();
        try {
            saveItemsToAdd();
            VoucherManager.getInstance().save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String msg) {
        getLogger().info(msg);
    }

    public void error(String msg) {
        getLogger().warning(msg);
    }

    public BukkitTask runTaskLater(Runnable run, long delay) {
        return getServer().getScheduler().runTaskLater(this, run, delay);
    }

    public BukkitTask runTask(Runnable run) {
        return getServer().getScheduler().runTask(this, run);
    }

    public Map<DarkRiseItem, Integer> addItems(Player player, Map<DarkRiseItem, Integer> map) {
        PlayerInventory inv = player.getInventory();
        Map<DarkRiseItem, Integer> playerMap = new HashMap<>();
        if (this.itemsToAdd.containsKey(player.getUniqueId()))
            playerMap.putAll(this.itemsToAdd.get(player.getUniqueId()));
        if (map != null)
            map.forEach((item, amount) -> {
                if (playerMap.containsKey(item))
                    amount = Integer.valueOf(amount.intValue() + playerMap.get(item).intValue());
                playerMap.put(item, amount);
            });
        if (playerMap.isEmpty())
            return new HashMap<>();
        ItemStack[] itemsArray = playerMap.entrySet().stream().map(e -> e.getKey().getItem(e.getValue().intValue())).toArray(x$0 -> new ItemStack[x$0]);
        Map<Integer, ItemStack> notAdded = inv.addItem(itemsArray);
        Map<DarkRiseItem, Integer> notAddedRise = notAdded.entrySet().stream().collect(Collectors.toMap(e -> getItems().getItemByStack(e.getValue()), e -> Integer.valueOf(e.getValue().getAmount())));
        this.itemsToAdd.put(player.getUniqueId(), notAddedRise);
        if (this.itemsToAdd.containsKey(player.getUniqueId()) && this.itemsToAdd.get(player.getUniqueId()).isEmpty())
            this.itemsToAdd.remove(player.getUniqueId());
        return playerMap.entrySet().stream()
                .filter(e -> (!notAddedRise.containsKey(e.getKey()) || !Objects.equals(notAddedRise.get(e.getKey()), e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void dropItems(Location loc, Map<DarkRiseItem, Integer> map) {
        map.keySet().forEach(item -> {
            Item it = loc.getWorld().dropItem(loc, item.getItem(map.get(item)));
            it.setPickupDelay(1);
        });
    }

    public Map<DarkRiseItem, Integer> addItems(Player player, final DarkRiseItem item, final Integer amount) {
        return addItems(player, new HashMap<DarkRiseItem, Integer>() {{
            put(item, amount);
        }});
    }

    public void dropItems(Location loc, final DarkRiseItem item, final Integer amount) {
        dropItems(loc, new HashMap<DarkRiseItem, Integer>() {{
            put(item, amount);
        }});
    }

    public Map<DarkRiseItem, Integer> checkItemsToAdd(Player player) {
        return addItems(player, null);
    }

    public void checkItemsToAdd() {
        Bukkit.getOnlinePlayers().stream().filter(o -> this.itemsToAdd.containsKey(o.getUniqueId())).forEach(this::checkItemsToAdd);
    }

    public void saveItemsToAdd() throws IOException {
        YamlConfiguration cfg = new YamlConfiguration();
        this.itemsToAdd.forEach((uuid, map) -> {
            ConfigurationSection section = cfg.createSection(uuid.toString());
            map.forEach((item, amount) -> section.set(item.getId(), amount));
        });
        cfg.save(this.itemsToAddFile);
    }

    public void loadItemsToAdd() {
        this.itemsToAdd.clear();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.itemsToAddFile);
        cfg.getKeys(false).forEach(uuid -> {
            Map<DarkRiseItem, Integer> itemMap = new HashMap<>();
            cfg.getConfigurationSection(uuid).getValues(false).forEach((itemName, amount) ->
                    itemMap.put(getItems().getItemById(itemName), (Integer) amount));
            this.itemsToAdd.put(UUID.fromString(uuid), itemMap);
        });
    }

    public Map<UUID, Map<DarkRiseItem, Integer>> getItemsToAdd() {
        return this.itemsToAdd;
    }
}
