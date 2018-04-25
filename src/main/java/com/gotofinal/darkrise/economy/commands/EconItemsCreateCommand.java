package com.gotofinal.darkrise.economy.commands;

import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.item.DarkRiseItemImpl;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.diorite.utils.math.DioriteMathUtils;
import org.diorite.utils.math.DoubleRange;

@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class EconItemsCreateCommand implements CommandExecutor
{
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy  plugin;
    private final EconItemsCommand command;

    public EconItemsCreateCommand(final DarkRiseEconomy plugin, final EconItemsCommand command)
    {
        this.plugin = plugin;
        this.command = command;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        if (args.length() == 0)
        {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }
        if (! (sender instanceof Player))
        {
            this.sendMessage("senderIsNotPlayer", sender);
            return;
        }
        if (! this.checkPermission(sender, "econ.items.create"))
        {
            return;
        }
        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand == null)
        {
            this.sendMessage("economy.commands.create.no-item", sender);
            return;
        }
        String id = args.asString(0);
        boolean dropOnDeath = false;
        boolean confirmOnUse = false;
        boolean canDrop = true;
        int removeOnDeath = 0;
        int removeOnUse = 0;
        String fileName = id + ".yml";
        DoubleRange chanceToLostDurability = DoubleRange.EMPTY;
        String prev = "";
        Iterator<String> iterator = args.iterator();
        iterator.next();
        while (iterator.hasNext())
        {
            final String arg = iterator.next();
            if (arg.equalsIgnoreCase("-dropOnDeath"))
            {
                dropOnDeath = true;
            }
            else if (arg.equalsIgnoreCase("-dontDropOnDeath") || arg.equalsIgnoreCase("-don'tDropOnDeath") || arg.equalsIgnoreCase("-doNotDropOnDeath"))
            {
                dropOnDeath = false;
            }
            else if (arg.equalsIgnoreCase("-confirmOnUse"))
            {
                confirmOnUse = true;
            }
            else if (arg.equalsIgnoreCase("-dontConfirmOnUse") || arg.equalsIgnoreCase("-don'tConfirmOnUse") || arg.equalsIgnoreCase("-doNotConfirmOnUse"))
            {
                confirmOnUse = false;
            }
            else if (arg.equalsIgnoreCase("-canDrop"))
            {
                canDrop = true;
            }
            else if (arg.equalsIgnoreCase("-dontCanDrop") || arg.equalsIgnoreCase("-doNotCanDrop") || arg.equalsIgnoreCase("-CantDrop") || arg.equalsIgnoreCase("-Can'tDrop") || arg.equalsIgnoreCase("-CanNotDrop"))
            {
                canDrop = false;
            }
            else if (prev.equalsIgnoreCase("-removeOnDeath") || prev.equalsIgnoreCase("-remOnDeath") || prev.equalsIgnoreCase("-delOnDeath") || prev.equalsIgnoreCase("-deleteOnDeath"))
            {
                prev = "";
                Integer i = DioriteMathUtils.asInt(arg);
                if (i == null)
                {
                    this.sendMessage("notANumber", sender, new MessageData("text", arg));
                    return;
                }
                removeOnDeath = i;
            }
            else if (prev.equalsIgnoreCase("-removeOnUse") || prev.equalsIgnoreCase("-remOnUse") || prev.equalsIgnoreCase("-delOnUse") || prev.equalsIgnoreCase("-deleteOnUse"))
            {
                prev = "";
                Integer i = DioriteMathUtils.asInt(arg);
                if (i == null)
                {
                    this.sendMessage("notANumber", sender, new MessageData("text", arg));
                    return;
                }
                removeOnUse = i;
            }
            else if (prev.equalsIgnoreCase("-file") || prev.equalsIgnoreCase("-f") || prev.equalsIgnoreCase("-catalog"))
            {
                prev = "";
                fileName = arg;
            }
            else if (prev.equalsIgnoreCase("-dura") || prev.equalsIgnoreCase("-durability"))
            {
                prev = "";
                DoubleRange doubleRange = DoubleRange.valueOf(arg);
                if (doubleRange == null)
                {
                    this.sendUsage(command.getUsage(), sender, command, args);
                    return;
                }
                chanceToLostDurability = doubleRange;
            }
            else if (! prev.equalsIgnoreCase(""))
            {
                this.sendUsage(command.getUsage(), sender, command, args);
                return;
            }
            else
            {
                prev = arg;
            }
        }

        if(!mainHand.hasItemMeta()) {
            ItemMeta meta = mainHand.getItemMeta();
            meta.setDisplayName(StringUtils.capitalize(mainHand.getType().name().toLowerCase()));
            mainHand.setItemMeta(meta);
        }

        DarkRiseItemImpl riseItem = new DarkRiseItemImpl(id, mainHand, dropOnDeath, removeOnDeath, confirmOnUse, removeOnUse, canDrop, ! chanceToLostDurability.equals(DoubleRange.EMPTY), chanceToLostDurability, Collections.emptyList());
        this.plugin.getItems().addItem(fileName, riseItem, true);
        this.sendMessage("economy.commands.create.done", sender, new MessageData("riseItem", riseItem));
    }
}
