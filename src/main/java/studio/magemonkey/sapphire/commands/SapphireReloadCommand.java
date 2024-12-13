package studio.magemonkey.sapphire.commands;

import org.bukkit.command.CommandSender;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.sapphire.Sapphire;

import java.util.List;

//@DarkRiseSubCommand(value = EconCommand.class, name = "reload")
public class SapphireReloadCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireReloadCommand(final Sapphire plugin, SapphireCommand command) {
        super("reload", List.of("reload"), command);
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "sapphire.reload")) {
            return;
        }
        this.eco.reloadConfigs();
        CodexEngine.get().getMessageUtil().sendMessage("reload", sender);
    }
}
