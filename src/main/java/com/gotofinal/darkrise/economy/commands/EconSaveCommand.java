package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconCommand.class, name = "save")
public class EconSaveCommand extends RiseCommand {
    private final DarkRiseEconomy plugin;

    public EconSaveCommand(final DarkRiseEconomy plugin, EconCommand command) {
        super("save", ArrayUtils.toArray("save"), command);
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "econ.save")) {
            return;
        }
        this.plugin.getItems().saveItems();
        MessageUtil.sendMessage("save", sender);
    }
}
