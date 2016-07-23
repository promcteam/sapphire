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

@DarkRiseSubCommand(value = EconItemsCommand.class, name = "delete", aliases = {"delete", "del"})
public class EconItemsDeleteCommand implements CommandExecutor
{
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy  plugin;
    private final EconItemsCommand command;

    public EconItemsDeleteCommand(final DarkRiseEconomy plugin, final EconItemsCommand command)
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
        DarkRiseItem riseItem = this.plugin.getItems().getItemByIdOrName(args.asString(0));
        if (riseItem == null)
        {
            this.sendMessage("economy.commands.noItem", sender, new MessageData("name", args.asString(0)));
            return;
        }
        this.plugin.getItems().removeItem(riseItem, true);
        this.sendMessage("economy.commands.delete", sender, new MessageData("riseItem", riseItem));
    }
}
