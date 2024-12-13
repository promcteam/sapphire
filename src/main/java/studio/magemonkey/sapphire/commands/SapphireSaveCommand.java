package studio.magemonkey.sapphire.commands;

import org.bukkit.command.CommandSender;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.sapphire.Sapphire;

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
        CodexEngine.get().getMessageUtil().sendMessage("save", sender);
    }
}
