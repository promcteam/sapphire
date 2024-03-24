package com.promcteam.sapphire.commands;

import com.promcteam.sapphire.Sapphire;
import com.promcteam.risecore.command.RiseCommand;
import com.promcteam.risecore.legacy.util.message.MessageUtil;
import com.promcteam.risecore.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconCommand.class, name = "reload")
public class SapphireReloadCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireReloadCommand(final Sapphire plugin, SapphireCommand command) {
        super("reload", ArrayUtils.toArray("reload"), command);
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
