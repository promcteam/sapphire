package com.gotofinal.darkrise.economy.commands;

import java.util.Collections;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.darkrise.spigot.core.command.SubCommandImpl;

import org.bukkit.command.CommandSender;

@DarkRiseSubCommand(value = EconCommand.class)
public class EconItemsCommand extends SubCommandImpl implements CommandExecutor
{
    public EconItemsCommand(DarkRiseEconomy economy, EconCommand command)
    {
        super("items", Collections.singletonList("items"), command);
        this.setUsage(command.getUsage());
        this.setCommandExecutor(this);
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        this.sendUsage(command.getUsage(), sender, command, args);
    }
}
