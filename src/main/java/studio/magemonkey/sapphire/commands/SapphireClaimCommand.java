package studio.magemonkey.sapphire.commands;

import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.codex.util.messages.MessageUtil;
import studio.magemonkey.sapphire.Sapphire;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

public class SapphireClaimCommand extends RiseCommand {

    private final Sapphire eco;

    public SapphireClaimCommand(Sapphire plugin, SapphireCommand command) {
        super("claim", Collections.singletonList("claim"), command);
        eco = plugin;
    }


    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't perform this command from the console!");
            return;
        }

        Map<DarkRiseItem, Integer> added = eco.checkItemsToAdd((Player) sender);
        MessageUtil.sendMessage("sapphire.commands.claim.claimed", sender, new MessageData("amount", added.size()));
    }
}
