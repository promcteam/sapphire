package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconCommand.class, name = "reload")
public class EconReloadCommand extends RiseCommand {
    private final DarkRiseEconomy eco;

    public EconReloadCommand(final DarkRiseEconomy plugin, EconCommand command) {
        super("reload", ArrayUtils.toArray("reload"), command);
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "pmcu.reload")) {
            return;
        }
        this.eco.reloadConfigs();
        MessageUtil.sendMessage("reload", sender);
    }
}
