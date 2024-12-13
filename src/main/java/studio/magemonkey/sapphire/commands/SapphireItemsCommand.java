package studio.magemonkey.sapphire.commands;

import org.bukkit.command.CommandSender;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.sapphire.Sapphire;

import java.util.Collections;

//@DarkRiseSubCommand(value = EconCommand.class)
public class SapphireItemsCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireItemsCommand(Sapphire economy, SapphireCommand command) {
        super("items", Collections.singletonList("items"), command);
        this.eco = economy;
        this.setUsage(command.getUsage());
        //this.setCommandExecutor(this);
    }


    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        this.sendUsage(command.getUsage(), sender, command, args);
    }
}
