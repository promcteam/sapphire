package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class EconCommand extends RiseCommand {

    private DarkRiseEconomy eco;

    public EconCommand(DarkRiseEconomy economy) {
        super("econ", Collections.singletonList("econ"), economy);
        this.setUsage("economy.commands.help");
//        this.setCommandExecutor(this);
//        SubCommandAnnotation.register(this);
    }
//
//    @InvokeOn(value = DarkRiseEconomy.class, on = InvokeType.ENABLE_OF)
//    private static void init(DarkRiseEconomy riseEconomy) {
//        riseEconomy.getCommandMap().registerCommand(new EconCommand(riseEconomy));
//    }
//
//    @Override
//    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label,
//                           final Matcher matchedPattern, final Arguments args) {
//        this.sendUsage(command.getUsage(), sender, command, args);
//    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        sendUsage(command.getUsage(), sender, this, args);
    }
}
