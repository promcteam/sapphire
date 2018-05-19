package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.core.annotation.InvokeOn;
import com.gotofinal.darkrise.core.annotation.SubCommandAnnotation;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.darkrise.spigot.core.command.PluginCommandImpl;
import com.gotofinal.messages.api.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;

public class EconClaimCommand extends PluginCommandImpl implements CommandExecutor
{
    public EconClaimCommand(DarkRiseEconomy economy)
    {
        super("claim", Collections.singletonList("claim"), economy);
        this.setUsage("economy.commands.help");
        this.setCommandExecutor(this);
        SubCommandAnnotation.register(this);
    }

    @InvokeOn(value = DarkRiseEconomy.class, on = InvokeOn.InvokeType.ENABLE_OF)
    private static void init(DarkRiseEconomy riseEconomy)
    {
        riseEconomy.getCommandMap().registerCommand(new EconClaimCommand(riseEconomy));
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        if (! (sender instanceof Player))
        {
            sender.sendMessage("You can't perform this command from the console!");
            return;
        }

        DarkRiseEconomy plugin = DarkRiseEconomy.getInstance();
        Map<DarkRiseItem, Integer> added = plugin.checkItemsToAdd((Player) sender);
        this.sendMessage("economy.commands.claim.claimed", sender, new Message.MessageData("amount", added.size()));
    }

}
