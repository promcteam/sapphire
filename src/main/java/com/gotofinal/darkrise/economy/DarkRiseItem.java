package com.gotofinal.darkrise.economy;

import java.util.List;

import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.diorite.utils.math.DoubleRange;

public interface DarkRiseItem extends ConfigurationSerializable
{
    String getId();

    String getName();

    boolean isDropOnDeath();

    int isRemoveOnDeath();

    boolean isConfirmOnUse();

    int isRemoveOnUse();

    boolean canDrop();

    /**
     * If the item is tradeable
     * This extends 'canDrop'
     * If not it can't be put in chests etc.
     *
     * @return true if tradeable
     */
    boolean isTradeable();

    boolean isEnabledEnchantedDurability();

    boolean isTwoHand();

    DoubleRange chanceToLostDurability();

    List<DelayedCommand> getCommands();

    List<String> getPermission();

    String getPermissionMessage();

    ItemStack getItem(int amount);

    default ItemStack getItem()
    {
        return this.getItem(1);
    }

    @SuppressWarnings("ObjectEquality")
    default boolean isSimilar(DarkRiseItem item)
    {
        if (item == null)
        {
            return false;
        }
        return (item == this) || this.getId().equals(item.getId());
    }

    @SuppressWarnings("ObjectEquality")
    default boolean isSimilar(ItemStack item)
    {
        if ((item == null) || ! item.hasItemMeta())
        {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (! meta.hasDisplayName())
        {
            return false;
        }
        return (item == this) || this.getName().equals(meta.getDisplayName());
    }

    void invoke(CommandSender sender);
}
