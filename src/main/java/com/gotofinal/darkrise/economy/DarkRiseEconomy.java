package com.gotofinal.darkrise.economy;

import java.io.File;

import com.gotofinal.darkrise.economy.cfg.EconomyConfig;
import com.gotofinal.darkrise.spigot.core.DarkRisePlugin;
import com.gotofinal.diggler.core.nms.NMSPlayerUtils;
import com.gotofinal.messages.Init;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderType;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.chat.TextComponent;

public class DarkRiseEconomy extends DarkRisePlugin
{
    private static DarkRiseEconomy instance;

    private DarkRiseItems items;
    private EconomyConfig config;

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
        this.reloadConfigs();

    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        this.saveConfigs();
    }
}
