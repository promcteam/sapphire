package com.promcteam.sapphire.commands;

import com.promcteam.sapphire.Sapphire;
import me.travja.darkrise.core.command.RiseCommand;
import org.bukkit.command.CommandSender;

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
