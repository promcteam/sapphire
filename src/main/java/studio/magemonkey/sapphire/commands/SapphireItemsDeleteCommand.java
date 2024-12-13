package studio.magemonkey.sapphire.commands;

import org.bukkit.command.CommandSender;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.sapphire.Sapphire;

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
            CodexEngine.get()
                    .getMessageUtil()
                    .sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[0]));
            return;
        }
        this.plugin.getItems().removeItem(riseItem, true);
        CodexEngine.get()
                .getMessageUtil()
                .sendMessage("sapphire.commands.delete", sender, new MessageData("riseItem", riseItem));
    }
}
