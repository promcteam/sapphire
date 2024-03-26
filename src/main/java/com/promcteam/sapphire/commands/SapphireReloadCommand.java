package com.promcteam.sapphire.commands;

import com.promcteam.codex.legacy.command.RiseCommand;
import com.promcteam.codex.util.messages.MessageUtil;
import com.promcteam.sapphire.Sapphire;
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
