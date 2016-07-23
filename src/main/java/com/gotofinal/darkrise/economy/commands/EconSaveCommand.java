package com.gotofinal.darkrise.economy.commands;

import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;

import org.bukkit.command.CommandSender;

@DarkRiseSubCommand(value = EconCommand.class, name = "save")
public class EconSaveCommand implements CommandExecutor
{
    private final DarkRiseEconomy plugin;

    public EconSaveCommand(final DarkRiseEconomy plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        if (! this.checkPermission(sender, "econ.save"))
        {
            return;
        }
        this.plugin.getItems().saveItems();
        this.sendMessage("save", sender);
    }
}
