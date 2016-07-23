package com.gotofinal.darkrise.economy.commands;

import java.util.Collections;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.InvokeOn;
import com.gotofinal.darkrise.core.annotation.InvokeOn.InvokeType;
import com.gotofinal.darkrise.core.annotation.SubCommandAnnotation;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.darkrise.spigot.core.command.PluginCommandImpl;

import org.bukkit.command.CommandSender;

public class EconCommand extends PluginCommandImpl implements CommandExecutor
{
    public EconCommand(DarkRiseEconomy economy)
    {
        super("econ", Collections.singletonList("econ"), economy);
        this.setUsage("economy.commands.help");
        this.setCommandExecutor(this);
        SubCommandAnnotation.register(this);
    }

    @InvokeOn(value = DarkRiseEconomy.class, on = InvokeType.ENABLE_OF)
    private static void init(DarkRiseEconomy riseEconomy)
    {
        riseEconomy.getCommandMap().registerCommand(new EconCommand(riseEconomy));
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        this.sendUsage(command.getUsage(), sender, command, args);
    }
}
