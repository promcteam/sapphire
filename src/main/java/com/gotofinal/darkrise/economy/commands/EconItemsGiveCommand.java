package com.gotofinal.darkrise.economy.commands;

import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@DarkRiseSubCommand(value = EconItemsCommand.class, name = "give")
public class EconItemsGiveCommand implements CommandExecutor
{
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy  plugin;
    private final EconItemsCommand command;

    public EconItemsGiveCommand(final DarkRiseEconomy plugin, final EconItemsCommand command)
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
        if (args.length() == 1)
        {
            if (! (sender instanceof Player))
            {
                this.sendMessage("senderIsNotPlayer", sender);
                return;
            }
            DarkRiseItem riseItem = this.plugin.getItems().getItemByIdOrName(args.asString(0));
            if (riseItem == null)
            {
                this.sendMessage("economy.commands.noItem", sender, new MessageData("name", args.asString(0)));
                return;
            }
            ((Player) sender).getInventory().addItem(riseItem.getItem());
            return;
        }
        Player target = args.asPlayer(0);
        if (target == null)
        {
            this.sendMessage("notAPlayer", sender, new MessageData("name", args.asString(0)));
            return;
        }
        DarkRiseItem riseItem = this.plugin.getItems().getItemById(args.asString(1));
        if (riseItem == null)
        {
            this.sendMessage("economy.commands.noItem", sender, new MessageData("name", args.asString(1)));
            return;
        }
        int amount = 1;
        if (args.has(2))
        {
            Integer i = args.asInt(2);
            if (i == null)
            {
                this.sendMessage("notANumber", sender, new MessageData("text", args.asText(2)));
                return;
            }
            amount = i;
        }
        target.getInventory().addItem(riseItem.getItem(amount));
    }
}
