package studio.magemonkey.sapphire.commands;

import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.util.messages.MessageUtil;
import studio.magemonkey.sapphire.Sapphire;
import org.bukkit.command.CommandSender;

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
        MessageUtil.sendMessage("reload", sender);
    }
}
