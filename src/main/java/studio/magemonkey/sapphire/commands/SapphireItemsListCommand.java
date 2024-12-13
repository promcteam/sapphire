package studio.magemonkey.sapphire.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.compat.VersionManager;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.sapphire.Sapphire;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "list", aliases = {"list", "ls"})
public class SapphireItemsListCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final Sapphire eco;

    public SapphireItemsListCommand(final Sapphire plugin, SapphireItemsCommand command) {
        super("list", List.of("list", "ls"), command);
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "sapphire.items.list")) {
            return;
        }
        SortedMap<String, DarkRiseItem> map  = this.eco.getItems().getSortedMap();
        int                             page = 0;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {
            }
        }
        MessageData pageMsgData = new MessageData("page", page + 1);
        int         index       = 0;

//        TextComponent list = new TextComponent("");
        CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.items.list-header",
                sender,
                pageMsgData,
                new MessageData("sender", sender));
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
            textComponent.setClickEvent(new ClickEvent(Action.RUN_COMMAND,
                    "/econ items give " + sender.getName() + " " + entry.getKey() + " 1"));
            textComponent.setHoverEvent(VersionManager.getNms().getHoverEvent(entry.getValue().getItem()));
            textComponent.setExtra(Arrays.asList(CodexEngine.get().getMessageUtil().getMessageAsComponent(
                    "sapphire.commands.items.list-entry",
                    new MessageData("index", index),
                    new MessageData("riseItem", entry.getValue())
            )));
            CodexEngine.get().getMessageUtil().sendMessage(sender, textComponent);
//            list.addExtra(textComponent);
//            if (index % PAGE_SIZE != 0 && iterator.hasNext()) {
//                list.addExtra(new TextComponent("\n"));
//            }
        }
        CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.items.list-footer",
                sender,
                pageMsgData,
                new MessageData("sender", sender));
    }
}
