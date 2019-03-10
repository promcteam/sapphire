package com.gotofinal.darkrise.economy;

import com.gotofinal.darkrise.economy.cfg.EconomyConfig;
import com.gotofinal.darkrise.economy.cfg.PlayerData;
import com.gotofinal.darkrise.economy.cfg.VoucherManager;
import com.gotofinal.darkrise.spigot.core.DarkRisePlugin;
import com.gotofinal.diggler.core.nms.NMSPlayerUtils;
import com.gotofinal.messages.Init;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderType;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DarkRiseEconomy extends DarkRisePlugin
{
    private static DarkRiseEconomy instance;

    private DarkRiseItems items;
    private EconomyConfig config;
    private final File itemsToAddFile = new File(getDataFolder(), "itemstoadd.yml");
    private final Map<UUID, Map<DarkRiseItem, Integer>> itemsToAdd = new HashMap<>();

    public static DarkRiseEconomy getInstance()
    {
        return instance;
    }

    {
        DarkRiseEconomy.instance = this;
    }

    public EconomyConfig getCfg()
    {
        return this.config;
    }

    public DarkRiseItems getItems()
    {
        return this.items;
    }

    public static DarkRiseItems getItemsRegistry()
    {
        return instance.items;
    }

    @Override
    public void reloadConfigs()
    {
        super.reloadConfigs();
        this.items = new DarkRiseItems();
        this.items.loadItems();
        this.config = this.loadConfigFile(new File(this.getDataFolder(), "config.yml"), EconomyConfig.class);
    }

    @Override
    public void saveConfigs()
    {
        this.items.saveItems();
    }

    public static final PlaceholderType<DarkRiseItem>  RISE_ITEM      = PlaceholderType.create("riseItem", DarkRiseItem.class);
    public static final PlaceholderType<EconomyConfig> ECONOMY_CONFIG = PlaceholderType.create("economyConfig", EconomyConfig.class);

    @Override
    public void onLoad()
    {
        Init.PLAYER.registerItem("points", CurrencyType.POINTS::get);
        RISE_ITEM.registerItem("name", (item) ->
        {
            TextComponent textComponent = new TextComponent(item.getName());
            textComponent.setHoverEvent(NMSPlayerUtils.convert(item.getItem()));
            return textComponent;
        });
        RISE_ITEM.registerItem("displayName", (item) ->
        {
            TextComponent textComponent = new TextComponent(item.getName());
            textComponent.setHoverEvent(NMSPlayerUtils.convert(item.getItem()));
            return textComponent;
        });
        RISE_ITEM.registerItem("material", d -> d.getItem().getType());
        RISE_ITEM.registerItem("id", (item) ->
        {
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
    public void onEnable()
    {
        super.onEnable();
        PlayerData.init();
        this.reloadConfigs();
        this.loadItemsToAdd();

        DivineItemsHook.setEnabled(getServer().getPluginManager().getPlugin("DivineItemsRPG") != null);
        getLogger().info("DivineItemsRPG Hook is now " + (DivineItemsHook.isEnabled() ? "enabled" : "disabled"));
	    try {
		    VoucherManager.getInstance().load();
	    }
	    catch(IOException e) {
		    e.printStackTrace();
	    }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        this.saveConfigs();
        try
        {
            this.saveItemsToAdd();
	        VoucherManager.getInstance().save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Adds items
     *
     * @param player player
     * @param map    items map (item, amount)
     * @return map of added items
     */
    public Map<DarkRiseItem, Integer> addItems(Player player, Map<DarkRiseItem, Integer> map)
    {
        final PlayerInventory inv = player.getInventory();

        //3
        final Map<DarkRiseItem, Integer> playerMap = new HashMap<>();

        if (itemsToAdd.containsKey(player.getUniqueId()))
        {
            playerMap.putAll(itemsToAdd.get(player.getUniqueId()));
        }

        if (map != null)
        {
            map.forEach((item, amount) -> {
                if (playerMap.containsKey(item))
                {
                    amount += playerMap.get(item);
                }

                playerMap.put(item, amount);
            });
        }

        if (playerMap.isEmpty())
        {
            return new HashMap<>();
        }

        final ItemStack[] itemsArray = playerMap.entrySet().stream()
                .map(e -> e.getKey().getItem(e.getValue())).toArray(ItemStack[]::new);
        final Map<Integer, ItemStack> notAdded = inv.addItem(itemsArray);
        final Map<DarkRiseItem, Integer> notAddedRise = notAdded.entrySet().stream()
                .collect(Collectors.toMap(e -> getItems().getItemByStack(e.getValue()), e -> e.getValue().getAmount()));
        itemsToAdd.put(player.getUniqueId(), notAddedRise);

        if (itemsToAdd.containsKey(player.getUniqueId()) && itemsToAdd.get(player.getUniqueId()).isEmpty())
        {
            itemsToAdd.remove(player.getUniqueId());
        }

        return playerMap.entrySet().stream()
                .filter(e -> !notAddedRise.containsKey(e.getKey()) || !Objects.equals(notAddedRise.get(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<DarkRiseItem, Integer> addItems(Player player, DarkRiseItem item, Integer amount)
    {
        return addItems(player, new HashMap<DarkRiseItem, Integer>(){{put(item, amount);}});
    }

    public Map<DarkRiseItem, Integer> checkItemsToAdd(Player player)
    {
        return addItems(player, null);
    }

    public void checkItemsToAdd()
    {
        Bukkit.getOnlinePlayers().stream().filter(o -> itemsToAdd.containsKey(o.getUniqueId())).forEach(this::checkItemsToAdd);
    }

    public void saveItemsToAdd() throws IOException
    {
        final YamlConfiguration cfg = new YamlConfiguration();

        itemsToAdd.forEach((uuid, map) -> {
            final ConfigurationSection section = cfg.createSection(uuid.toString());
            map.forEach((item, amount) -> section.set(item.getId(), amount));
        });

        cfg.save(itemsToAddFile);
    }

    public void loadItemsToAdd()
    {
        itemsToAdd.clear();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(itemsToAddFile);
        cfg.getKeys(false).forEach(uuid -> {
            final Map<DarkRiseItem, Integer> itemMap = new HashMap<>();
            cfg.getConfigurationSection(uuid).getValues(false).forEach((itemName, amount) ->
                    itemMap.put(getItems().getItemById(itemName), (Integer) amount));
            itemsToAdd.put(UUID.fromString(uuid), itemMap);
        });
    }

    public Map<UUID, Map<DarkRiseItem, Integer>> getItemsToAdd()
    {
        return itemsToAdd;
    }
}
