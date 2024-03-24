package com.promcteam.sapphire.commands;

import com.promcteam.sapphire.Sapphire;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import me.travja.darkrise.core.util.ArrayUtils;
import org.bukkit.command.CommandSender;

//@DarkRiseSubCommand(value = EconCommand.class, name = "save")
public class SapphireSaveCommand extends RiseCommand {
    private final Sapphire plugin;

    public SapphireSaveCommand(final Sapphire plugin, SapphireCommand command) {
        super("save", ArrayUtils.toArray("save"), command);
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
