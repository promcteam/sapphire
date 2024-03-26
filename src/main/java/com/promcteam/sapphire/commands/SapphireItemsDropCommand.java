package com.promcteam.sapphire.commands;

import com.promcteam.codex.legacy.command.RiseCommand;
import com.promcteam.codex.legacy.riseitem.DarkRiseItem;
import com.promcteam.codex.util.messages.MessageData;
import com.promcteam.codex.util.messages.MessageUtil;
import com.promcteam.sapphire.Sapphire;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "drop")
public class SapphireItemsDropCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final Sapphire             eco;
    private final SapphireItemsCommand command;

    public SapphireItemsDropCommand(Sapphire plugin, SapphireItemsCommand command) {
        super("drop", Collections.singletonList("drops"), command);
        this.eco = plugin;
        this.command = command;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label,
                                      String[] args) {
        if (args.length == 2) {
            return Collections.emptyList();
        }
        if (args.length == 0) {
            return this.eco.getItems().getItems().stream().map(DarkRiseItem::getId).collect(Collectors.toList());
        }
        String str = args[0].toLowerCase();
        return this.eco.getItems()
                .getItems()
                .stream()
                .map(DarkRiseItem::getId)
                .filter(id -> id.toLowerCase().startsWith(str))
                .collect(Collectors.toList());
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {

        if (!this.checkPermission(sender, "sapphire.items.drop")) {
            return;
        }
        if (args.length == 0) {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                MessageUtil.sendMessage("senderIsNotPlayer", sender);
                return;
            }
            DarkRiseItem riseItem = this.eco.getItems().getItemByIdOrName(args[0]);
            if (riseItem == null) {
                MessageUtil.sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[0]));
                return;
            }

            eco.dropItems(((Player) sender).getLocation(), riseItem, 1);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage("notAPlayer", sender, new MessageData("name", args[0]));
            return;
        }
        DarkRiseItem riseItem = this.eco.getItems().getItemByIdOrName(args[1]);
        if (riseItem == null) {
            MessageUtil.sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[1]));
            return;
        }
        int amount = 1;
        if (args.length >= 3) {
            try {
                Integer i = Integer.parseInt(args[2]);
                amount = i;
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage("notANumber", sender, new MessageData("text", args[2]));
                return;
            }
        }

        eco.dropItems(target.getLocation(), riseItem, amount);
        MessageUtil.sendMessage("sapphire.commands.drop.success",
                sender,
                new MessageData("player", target.getName()),
                new MessageData("riseItem", riseItem));
    }
}
