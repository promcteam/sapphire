package com.promcteam.sapphire.commands;

import com.promcteam.codex.legacy.command.RiseCommand;
import com.promcteam.sapphire.Sapphire;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SapphireCommand extends RiseCommand {

    private Sapphire eco;

    public SapphireCommand(Sapphire economy) {
        super("sapphire", Arrays.asList("sapphire", "econ", "pmcu", "promcutilities"), economy);
        this.setUsage("sapphire.commands.help");
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
