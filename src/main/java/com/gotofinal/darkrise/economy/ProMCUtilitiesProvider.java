package com.gotofinal.darkrise.economy;

import mc.promcteam.engine.NexEngine;
import mc.promcteam.engine.items.ItemType;
import mc.promcteam.engine.items.ProItemManager;
import mc.promcteam.engine.items.providers.IProItemProvider;
import me.travja.darkrise.core.item.DarkRiseItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ProMCUtilitiesProvider implements IProItemProvider<ProMCUtilitiesProvider.ProMCUtilitiesItemType> {
    public static String NAMESPACE = "PROMCU";

    public static void register() {
        NexEngine.getEngine().getItemManager().registerProvider(NAMESPACE, new ProMCUtilitiesProvider());
    }

    public static void unregister() {
        NexEngine.getEngine().getItemManager().unregisterProvider(ProMCUtilitiesProvider.class);
    }

    @Override
    public String pluginName() {
        return "ProMCUtilities";
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public Category getCategory() {
        return Category.PRO;
    }

    @Override
    public ProMCUtilitiesItemType getItem(String id) {
        id = ProItemManager.stripPrefix(NAMESPACE, id);

        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemById(id);
        if (riseItem == null) {
            return null;
        }

        return new ProMCUtilitiesItemType(riseItem);
    }

    @Override
    @Nullable
    public ProMCUtilitiesItemType getItem(ItemStack item) {
        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemByStack(item);
        if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
            return null;
        }

        return new ProMCUtilitiesItemType(riseItem);
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemByStack(item);
        return riseItem != null && !riseItem.getId().startsWith("vanilla_");
    }

    @Override
    public boolean isCustomItemOfId(ItemStack item, String id) {
        id = ProItemManager.stripPrefix(NAMESPACE, id);

        DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemByStack(item);
        if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
            return false;
        }

        return riseItem.getId().equals(id);
    }

    public static class ProMCUtilitiesItemType extends ItemType {
        private final DarkRiseItem riseItem;

        public ProMCUtilitiesItemType(DarkRiseItem riseItem) {
            this.riseItem = riseItem;
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getID() {
            return this.riseItem.getId();
        }

        @Override
        public Category getCategory() {
            return Category.PRO;
        }

        @Override
        public ItemStack create() {
            return this.riseItem.getItem();
        }

        @Override
        public boolean isInstance(ItemStack item) {
            DarkRiseItem riseItem = DarkRiseEconomy.getItemsRegistry().getItemByStack(item);
            if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
                return false;
            }

            return riseItem.getId().equals(this.riseItem.getId());
        }

        public DarkRiseItem getRiseItem() {
            return riseItem;
        }
    }
}
