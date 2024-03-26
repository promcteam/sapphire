package com.promcteam.sapphire.commands;

import com.promcteam.codex.legacy.command.RiseCommand;
import com.promcteam.codex.util.messages.MessageUtil;
import com.promcteam.sapphire.Sapphire;
import org.bukkit.command.CommandSender;

import java.util.List;

//@DarkRiseSubCommand(value = EconCommand.class, name = "save")
public class SapphireSaveCommand extends RiseCommand {
    private final Sapphire plugin;

    public SapphireSaveCommand(final Sapphire plugin, SapphireCommand command) {
        super("save", List.of("save"), command);
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!this.checkPermission(sender, "sapphire.save")) {
            return;
        }
        this.plugin.getItems().saveItems();
        MessageUtil.sendMessage("save", sender);
    }
}
