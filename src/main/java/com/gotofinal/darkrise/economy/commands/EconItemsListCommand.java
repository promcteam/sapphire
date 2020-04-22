package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.item.DarkRiseItem;
import me.travja.darkrise.core.legacy.util.message.MessageData;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.legacy.util.message.NMSPlayerUtils;
import me.travja.darkrise.core.util.ArrayUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "list", aliases = {"list", "ls"})
public class EconItemsListCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy eco;

    public EconItemsListCommand(final DarkRiseEconomy plugin, EconItemsCommand command) {
        super("list", ArrayUtils.toArray("list", "ls"), command);
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "econ.items.list")) {
            return;
        }
        SortedMap<String, DarkRiseItem> map = this.eco.getItems().getSortedMap();
        int page = 0;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
            }
        }
        MessageData pageMsgData = new MessageData("page", page + 1);
        int index = 0;

        TextComponent list = new TextComponent("");
        for (Iterator<Entry<String, DarkRiseItem>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<String, DarkRiseItem> entry = iterator.next();
            index++;
            if (index <= (PAGE_SIZE * page)) {
                continue;
            }
            if (index > ((PAGE_SIZE * page) + PAGE_SIZE)) {
                break;
            }
            TextComponent textComponent = new TextComponent("");
            textComponent.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/econ items give " + sender.getName() + " " + entry.getKey() + " 1"));
            textComponent.setHoverEvent(NMSPlayerUtils.convert(entry.getValue().getItem()));
            textComponent.setExtra(Arrays.asList(MessageUtil.getMessageAsComponent(
                    "economy.commands.items.list-entry",
                    new MessageData("index", index),
                    new MessageData("riseItem", entry.getValue())
            )));
            list.addExtra(textComponent);
            if (index % PAGE_SIZE != 0 && iterator.hasNext()) {
                list.addExtra(new TextComponent("\n"));
            }
        }
        MessageUtil.sendMessage("economy.commands.items.list", sender, pageMsgData, new MessageData("sender", sender), new MessageData("items", list));
    }
}
