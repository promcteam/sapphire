package com.promcteam.sapphire.commands;

import com.promcteam.sapphire.Sapphire;
import com.promcteam.risecore.command.RiseCommand;
import com.promcteam.risecore.item.DarkRiseItem;
import com.promcteam.risecore.legacy.util.message.MessageData;
import com.promcteam.risecore.legacy.util.message.MessageUtil;
import com.promcteam.risecore.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "delete", aliases = {"delete", "del"})
public class SapphireItemsDeleteCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final Sapphire plugin;

    public SapphireItemsDeleteCommand(final Sapphire plugin, final SapphireItemsCommand command) {
        super("delete", ArrayUtils.toArray("delete", "del"), command
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
