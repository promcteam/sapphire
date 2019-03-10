package com.gotofinal.darkrise.economy.item;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.economy.DivineItemsHook;
import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;
import com.gotofinal.darkrise.spigot.core.utils.cmds.R;
import com.gotofinal.darkrise.spigot.core.utils.item.ItemBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.diorite.utils.math.DoubleRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DarkRiseItemImpl implements DarkRiseItem
{
    private final String               id;
    private final ItemStack            item;
    private final boolean              dropOnDeath;
    private final int                  removeOnDeath;
    private final boolean              confirmOnUse;
    private final int                  removeOnUse;
    private final boolean              canDrop;
    private final boolean              tradeable;
    private final boolean              enabledEnchantedDurability;
    private final DoubleRange          chanceToLostDurability;
    private final List<DelayedCommand> commands;
    private final List<String>         permissionList = new ArrayList<>();
    private final String               permissionMessage;
    private final boolean              twoHand;
    private final DivineItemsMeta      divineItemsMeta;

    public static class DivineItemsMeta implements ConfigurationSerializable
    {
        public boolean enabled;
        public String tierName;
        public Double tierLevel;

        public DivineItemsMeta(Map<String, Object> map)
        {
            DeserializationWorker w = DeserializationWorker.start(map);
            this.enabled = w.getBoolean("enabled", false);
            this.tierName = w.getString("tierName");
            this.tierLevel = w.getDouble("tierLevel");
        }

        public DivineItemsMeta()
        {
            this.enabled = false;
            this.tierName = "";
            this.tierLevel = 0.0d;
        }

        @Override
        public Map<String, Object> serialize()
        {
            return SerializationBuilder.start(3)
                    .append("enabled", enabled)
                    .append("tierName", this.tierName)
                    .append("tierLevel", this.tierLevel)
                    .build();
        }
    }

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
        this.tradeable = w.getBoolean("tradeable", true);
        this.twoHand = w.getBoolean("twoHand", false);

        //Apply DivineItemsRPG meta
        if(map.containsKey("divineItemsMeta"))
        {
            this.divineItemsMeta = w.deserialize("divineItemsMeta", DivineItemsMeta.class);
        }
        else
        {
            this.divineItemsMeta = new DivineItemsMeta();
        }

        if (w.getMap().containsKey("permission"))
        {
            DeserializationWorker permSec = DeserializationWorker.start(w.getSection("permission", new HashMap<>()));
            if (permSec.getObject("node") instanceof Collection)
            {
                this.permissionList.addAll(permSec.getList("node", new ArrayList<>()));
            }
            else
            {
                this.permissionList.add(permSec.getString("node"));
            }
            this.permissionMessage = permSec.getString("message","&4You don't have permission to use this");
        }
        else
        {
            this.permissionMessage = null;
        }

        this.enabledEnchantedDurability = w.getBoolean("enabledEnchantedDurability", false);
        this.chanceToLostDurability = DoubleRange.valueOf(w.getString("chanceToLostDurability", "0.0 - 0.0"));
        this.commands = ((List<Map<String, Object>>) map.get("commands"))
                .stream()
                .map(DelayedCommand::new)
                .collect(Collectors.toList());
    }

    public DarkRiseItemImpl(String id, ItemStack item)
    {
        this.id = id.intern();
        this.item = item;
        this.dropOnDeath = false;
        this.removeOnDeath = 0;
        this.confirmOnUse = false;
        this.removeOnUse = 0;
        this.canDrop = true;
        this.tradeable = true;
        this.enabledEnchantedDurability = false;
        this.chanceToLostDurability = DoubleRange.EMPTY;
        this.commands = Collections.emptyList();
        this.permissionMessage = null;
        this.twoHand = false;
        this.divineItemsMeta = null;
    }

    public DarkRiseItemImpl(final String id, final ItemStack item, final boolean dropOnDeath, final int removeOnDeath,
                            final boolean confirmOnUse, final int removeOnUse, final boolean canDrop,
                            final boolean enabledEnchantedDurability, final DoubleRange chanceToLostDurability,
                            final List<DelayedCommand> commands)
    {
        this.id = id;
        this.item = item.clone();
        this.dropOnDeath = dropOnDeath;
        this.removeOnDeath = removeOnDeath;
        this.confirmOnUse = confirmOnUse;
        this.removeOnUse = removeOnUse;
        this.canDrop = canDrop;
        this.tradeable = true;
        this.enabledEnchantedDurability = enabledEnchantedDurability;
        this.chanceToLostDurability = chanceToLostDurability;
        this.commands = commands;
        this.permissionMessage = null;
        this.twoHand = false;
        this.divineItemsMeta = null;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getName()
    {
        return isVanilla() ? this.item.getType().name() : this.item.getItemMeta().getDisplayName();
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
    public boolean isTradeable()
    {
        return tradeable;
    }

    @Override
    public boolean isEnabledEnchantedDurability()
    {
        return this.enabledEnchantedDurability;
    }

    @Override
    public boolean isTwoHand()
    {
        return this.twoHand;
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
    public List<String> getPermission()
    {
        return permissionList;
    }

    @Override
    public String getPermissionMessage()
    {
        return permissionMessage;
    }

    @Override
    public ItemStack getItem(int amount)
    {
        ItemStack clone = this.item.clone();
        clone.setAmount(amount);

        if(this.divineItemsMeta.enabled)
        {
            ItemStack divineItemStack = DivineItemsHook.create(clone, this.divineItemsMeta);
            ItemMeta divineMeta = divineItemStack.getItemMeta();
            divineMeta.setDisplayName(clone.getItemMeta().getDisplayName());
            clone.setItemMeta(divineMeta);
        }

        return clone;
    }

    @Override
    public boolean isVanilla() {
        return getId().startsWith("vanilla_");
    }

    @Override
    public void invoke(CommandSender sender)
    {
        if (this.commands.isEmpty())
        {
            return;
        }
        DelayedCommand.invoke(DarkRiseEconomy.getInstance(), sender, this.commands,
                              R.r("{canDrop}", this.canDrop),
                              R.r("{enabledEnchantedDurability}", this.enabledEnchantedDurability),
                              R.r("{chanceToLostDurability}", this.chanceToLostDurability.toString()),
                              R.r("{dropOnDeath}", this.dropOnDeath),
                              R.r("{removeOnDeath}", this.removeOnDeath),
                              R.r("{confirmOnUse}", this.confirmOnUse),
                              R.r("removeOnUse", this.removeOnUse),
                              R.r("{id}", this.id),
                              R.r("{name}", this.getName()));
    }

    @Override
    public Map<String, Object> serialize()
    {
        SerializationBuilder sb = SerializationBuilder.start(10)
            .append("id", this.id)
            .append("canDrop", this.canDrop)
            .append("tradeable", this.tradeable)
            .append("enabledEnchantedDurability", this.enabledEnchantedDurability)
            .append("chanceToLostDurability", this.chanceToLostDurability.getMin() + "-" + this.chanceToLostDurability.getMax())
            .append("item", ItemBuilder.newItem(this.item))
            .append("dropOnDeath", this.dropOnDeath)
            .append("removeOnDeath", this.removeOnDeath)
            .append("confirmOnUse", this.confirmOnUse)
            .append("removeOnUse", this.removeOnUse)
            .append("twoHand", this.twoHand)
            .append("permission", SerializationBuilder.start(2)
                .append("node", this.permissionList)
                .append("message", this.permissionMessage))
            .append("commands", this.commands.stream().map(DelayedCommand::serialize).collect(Collectors.toList()));

        if (this.divineItemsMeta != null)
        {
            sb.append("divineItemsMeta", this.divineItemsMeta);
        }

        return sb.build();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (! (object instanceof DarkRiseItemImpl))
        {
            return false;
        }
        DarkRiseItemImpl riseItem = (DarkRiseItemImpl) object;
        return Objects.equals(this.getId(), riseItem.getId());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                       .append("id", this.id)
                       .append("item", this.item)
                       .append("twoHand", this.twoHand)
                       .append("dropOnDeath", this.dropOnDeath)
                       .append("removeOnDeath", this.removeOnDeath)
                       .append("confirmOnUse", this.confirmOnUse)
                       .append("removeOnUse", this.removeOnUse)
                       .append("canDrop", this.canDrop)
                       .append("tradeable", this.tradeable)
                       .append("enabledEnchantedDurability", this.enabledEnchantedDurability)
                       .append("chanceToLostDurability", this.chanceToLostDurability)
                       .append("commands", this.commands)
                       .append("name", this.getName())
                       .toString();
    }
}
