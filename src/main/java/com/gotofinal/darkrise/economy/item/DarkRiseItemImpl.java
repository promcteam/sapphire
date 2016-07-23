package com.gotofinal.darkrise.economy.item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;
import com.gotofinal.darkrise.spigot.core.utils.cmds.R;
import com.gotofinal.darkrise.spigot.core.utils.item.ItemBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import org.diorite.utils.math.DoubleRange;

public class DarkRiseItemImpl implements DarkRiseItem
{
    private final String               id;
    private final ItemStack            item;
    private final boolean              dropOnDeath;
    private final int                  removeOnDeath;
    private final boolean              confirmOnUse;
    private final int                  removeOnUse;
    private final boolean              canDrop;
    private final boolean              enabledEnchantedDurability;
    private final DoubleRange          chanceToLostDurability;
    private final List<DelayedCommand> commands;

    @SuppressWarnings("unchecked")
    public DarkRiseItemImpl(Map<String, Object> map)
    {
        DeserializationWorker w = DeserializationWorker.start(map);
        this.id = w.getString("id").intern();
        this.item = w.deserialize("item", ItemBuilder.class).build();
        this.dropOnDeath = w.getBoolean("dropOnDeath", true);
        this.removeOnDeath = w.getInt("removeOnDeath", 0);
        this.confirmOnUse = w.getBoolean("confirmOnUse", false);
        this.removeOnUse = w.getInt("removeOnUse", 0);
        this.canDrop = w.getBoolean("canDrop", true);
        this.enabledEnchantedDurability = w.getBoolean("enabledEnchantedDurability", false);
        this.chanceToLostDurability = DoubleRange.valueOf(w.getString("chanceToLostDurability", "0.0 - 0.0"));
        this.commands = ((List<Map<String, Object>>) map.get("commands")).stream().map(DelayedCommand::new).collect(Collectors.toList());
    }

    public DarkRiseItemImpl(final String id, final ItemStack item)
    {
        this.id = id.intern();
        this.item = item;
        this.dropOnDeath = false;
        this.removeOnDeath = 0;
        this.confirmOnUse = false;
        this.removeOnUse = 0;
        this.canDrop = true;
        this.enabledEnchantedDurability = false;
        this.chanceToLostDurability = DoubleRange.EMPTY;
        this.commands = Collections.emptyList();
    }

    public DarkRiseItemImpl(final String id, final ItemStack item, final boolean dropOnDeath, final int removeOnDeath, final boolean confirmOnUse, final int removeOnUse, final boolean canDrop, final boolean enabledEnchantedDurability, final DoubleRange chanceToLostDurability, final List<DelayedCommand> commands)
    {
        this.id = id;
        this.item = item.clone();
        this.dropOnDeath = dropOnDeath;
        this.removeOnDeath = removeOnDeath;
        this.confirmOnUse = confirmOnUse;
        this.removeOnUse = removeOnUse;
        this.canDrop = canDrop;
        this.enabledEnchantedDurability = enabledEnchantedDurability;
        this.chanceToLostDurability = chanceToLostDurability;
        this.commands = commands;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return this.item.getItemMeta().getDisplayName();
    }

    @Override
    public boolean isDropOnDeath()
    {
        return this.dropOnDeath;
    }

    @Override
    public int isRemoveOnDeath()
    {
        return this.removeOnDeath;
    }

    @Override
    public boolean isConfirmOnUse()
    {
        return this.confirmOnUse;
    }

    @Override
    public int isRemoveOnUse()
    {
        return this.removeOnUse;
    }

    @Override
    public boolean canDrop()
    {
        return this.canDrop;
    }

    @Override
    public boolean isEnabledEnchantedDurability()
    {
        return this.enabledEnchantedDurability;
    }

    @Override
    public DoubleRange chanceToLostDurability()
    {
        return this.chanceToLostDurability;
    }

    @Override
    public List<DelayedCommand> getCommands()
    {
        return this.commands;
    }

    @Override
    public ItemStack getItem(final int amount)
    {
        ItemStack clone = this.item.clone();
        clone.setAmount(amount);
        return clone;
    }

    @Override
    public void invoke(final CommandSender sender)
    {
        if (this.commands.isEmpty())
        {
            return;
        }
        DelayedCommand.invoke(DarkRiseEconomy.getInstance(), sender, this.commands, R.r("{canDrop}", this.canDrop), R.r("{enabledEnchantedDurability}", this.enabledEnchantedDurability), R.r("{chanceToLostDurability}", this.chanceToLostDurability.toString()), R.r("{dropOnDeath}", this.dropOnDeath), R.r("{removeOnDeath}", this.removeOnDeath), R.r("{confirmOnUse}", this.confirmOnUse), R.r("removeOnUse", this.removeOnUse), R.r("{id}", this.id), R.r("{name}", this.getName()));
    }

    @Override
    public Map<String, Object> serialize()
    {
        return SerializationBuilder.start(2).append("id", this.id).append("canDrop", this.canDrop).append("enabledEnchantedDurability", this.enabledEnchantedDurability).append("chanceToLostDurability", this.chanceToLostDurability.getMin() + "-" + this.chanceToLostDurability.getMax()).append("item", ItemBuilder.newItem(this.item)).append("dropOnDeath", this.dropOnDeath).append("removeOnDeath", this.removeOnDeath).append("confirmOnUse", this.confirmOnUse).append("removeOnUse", this.removeOnUse).append("commands", this.commands.stream().map(DelayedCommand::serialize).collect(Collectors.toList())).build();
    }
}
