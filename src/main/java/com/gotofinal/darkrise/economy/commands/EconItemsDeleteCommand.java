package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.item.DarkRiseItem;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageData;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "delete", aliases = {"delete", "del"})
public class EconItemsDeleteCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy plugin;

    public EconItemsDeleteCommand(final DarkRiseEconomy plugin, final EconItemsCommand command) {
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
        if (!this.checkPermission(sender, "pmcu.items.delete")) {
            return;
        }
        DarkRiseItem riseItem = this.plugin.getItems().getItemByIdOrName(args[0]);
        if (riseItem == null) {
            MessageUtil.sendMessage("economy.commands.noItem", sender, new MessageData("name", args[0]));
            return;
        }
        this.plugin.getItems().removeItem(riseItem, true);
        MessageUtil.sendMessage("economy.commands.delete", sender, new MessageData("riseItem", riseItem));
    }
}
