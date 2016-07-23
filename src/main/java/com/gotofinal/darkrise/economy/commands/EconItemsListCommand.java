package com.gotofinal.darkrise.economy.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.diggler.core.nms.NMSPlayerUtils;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

@DarkRiseSubCommand(value = EconItemsCommand.class, name = "list", aliases = {"list", "ls"})
public class EconItemsListCommand implements CommandExecutor
{
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy plugin;

    public EconItemsListCommand(final DarkRiseEconomy plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        SortedMap<String, DarkRiseItem> map = this.plugin.getItems().getSortedMap();
        int page = args.has(0) ? (args.asInt(0, 1) - 1) : 0;
        MessageData pageMsgData = new MessageData("page", page + 1);
        int index = 0;

        TextComponent list = new TextComponent("");
        for (Iterator<Entry<String, DarkRiseItem>> iterator = map.entrySet().iterator(); iterator.hasNext(); )
        {
            final Entry<String, DarkRiseItem> entry = iterator.next();
            index++;
            if (index <= (PAGE_SIZE * page))
            {
                continue;
            }
            if (index > ((PAGE_SIZE * page) + PAGE_SIZE))
            {
                break;
            }
            TextComponent textComponent = new TextComponent("");
            textComponent.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/econ items give " + sender.getName() + " \"" + entry.getKey() + "\" 1"));
            textComponent.setHoverEvent(NMSPlayerUtils.convert(entry.getValue().getItem()));
            textComponent.setExtra(new ArrayList<>(Arrays.asList(this.getMessageAsComponent("economy.commands.items.list-entry", new MessageData("index", index), new MessageData("riseItem", entry.getValue())))));
            list.addExtra(textComponent);
            if (iterator.hasNext())
            {
                list.addExtra(new TextComponent("\n"));
            }
        }
        this.sendMessage("economy.commands.items.list", sender, pageMsgData, new MessageData("sender", sender), new MessageData("items", list));
    }
}
