package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;

//@DarkRiseSubCommand(value = EconCommand.class)
public class EconItemsCommand extends RiseCommand {
    private DarkRiseEconomy eco;

    public EconItemsCommand(DarkRiseEconomy economy, EconCommand command) {
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
