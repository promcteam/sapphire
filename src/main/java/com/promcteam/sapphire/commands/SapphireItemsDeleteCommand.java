package com.promcteam.sapphire.commands;

import com.promcteam.codex.legacy.command.RiseCommand;
import com.promcteam.codex.legacy.riseitem.DarkRiseItem;
import com.promcteam.codex.util.messages.MessageData;
import com.promcteam.codex.util.messages.MessageUtil;
import com.promcteam.sapphire.Sapphire;
import org.bukkit.command.CommandSender;

import java.util.List;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "delete", aliases = {"delete", "del"})
public class SapphireItemsDeleteCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final Sapphire plugin;

    public SapphireItemsDeleteCommand(final Sapphire plugin, final SapphireItemsCommand command) {
        super("delete", List.of("delete", "del"), command
        );
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (args.length == 0) {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }
        if (!this.checkPermission(sender, "sapphire.items.delete")) {
            return;
        }
        DarkRiseItem riseItem = this.plugin.getItems().getItemByIdOrName(args[0]);
        if (riseItem == null) {
            MessageUtil.sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[0]));
            return;
        }
        this.plugin.getItems().removeItem(riseItem, true);
        MessageUtil.sendMessage("sapphire.commands.delete", sender, new MessageData("riseItem", riseItem));
    }
}
