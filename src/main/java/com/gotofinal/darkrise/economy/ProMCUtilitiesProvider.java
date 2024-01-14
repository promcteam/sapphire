package com.gotofinal.darkrise.economy;

import mc.promcteam.engine.NexEngine;
import mc.promcteam.engine.items.providers.IProItemProvider;
import me.travja.darkrise.core.item.DarkRiseItem;
import org.bukkit.inventory.ItemStack;

public class ProMCUtilitiesProvider implements IProItemProvider {
    public static void register() {
        NexEngine.getEngine().getItemManager().registerProvider("PROMCU", new ProMCUtilitiesProvider());
    }

    @Override
    public String pluginName() {
        return "ProMCUtilities";
    }

    @Override
    public ItemStack getItem(String id) {
        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemById(id);
        if (riseItem == null) {
            return null;
        }

        return riseItem.getItem();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        return DarkRiseEconomy.getItemsRegistry().getItemByStack(item) != null;
    }

    @Override
    public boolean isCustomItemOfId(ItemStack item, String id) {
        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemByStack(item);
        if (riseItem == null) {
            return false;
        }

        return riseItem.getId().equals(id);

    }
}
