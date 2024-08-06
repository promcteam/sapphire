package studio.magemonkey.sapphire.commands;

import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.util.messages.MessageUtil;
import studio.magemonkey.sapphire.Sapphire;
import org.bukkit.command.CommandSender;

import java.util.List;

//@DarkRiseSubCommand(value = EconCommand.class, name = "save")
public class SapphireSaveCommand extends RiseCommand {
    private final Sapphire plugin;

    public SapphireSaveCommand(final Sapphire plugin, SapphireCommand command) {
        super("save", List.of("save"), command);
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "sapphire.save")) {
            return;
        }
        this.plugin.getItems().saveItems();
        MessageUtil.sendMessage("save", sender);
    }
}
