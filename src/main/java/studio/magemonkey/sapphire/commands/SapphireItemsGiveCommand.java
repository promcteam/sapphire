package studio.magemonkey.sapphire.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.sapphire.Sapphire;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "give")
public class SapphireItemsGiveCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final Sapphire             eco;
    private final SapphireItemsCommand command;

    public SapphireItemsGiveCommand(Sapphire plugin, SapphireItemsCommand command) {
        super("give", Collections.singletonList("gives"), command);
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

        if (!this.checkPermission(sender, "sapphire.items.give")) {
            return;
        }
        if (args.length == 0) {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                CodexEngine.get().getMessageUtil().sendMessage("senderIsNotPlayer", sender);
                return;
            }
            DarkRiseItem riseItem = this.eco.getItems().getItemByIdOrName(args[0]);
            if (riseItem == null) {
                CodexEngine.get()
                        .getMessageUtil()
                        .sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[0]));
                return;
            }

            eco.addItems((Player) sender, riseItem, 1);
            if (eco.getItemsToAdd().containsKey(((Player) sender).getUniqueId())
                    && !eco.getItemsToAdd().get(((Player) sender).getUniqueId()).isEmpty()) {
                CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.claim.pending", sender);
            }
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            CodexEngine.get().getMessageUtil().sendMessage("notAPlayer", sender, new MessageData("name", args[0]));
            return;
        }
        DarkRiseItem riseItem = this.eco.getItems().getItemByIdOrName(args[1]);
        if (riseItem == null) {
            CodexEngine.get()
                    .getMessageUtil()
                    .sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[1]));
            return;
        }
        int amount = 1;
        if (args.length >= 3) {
            try {
                Integer i = Integer.parseInt(args[2]);
                amount = i;
            } catch (NumberFormatException e) {
                CodexEngine.get().getMessageUtil().sendMessage("notANumber", sender, new MessageData("text", args[2]));
                return;
            }
        }

        eco.addItems(target, riseItem, amount);
        CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.give.success",
                sender,
                new MessageData("player", target.getName()),
                new MessageData("riseItem", riseItem));
        if (eco.getItemsToAdd().containsKey(target.getUniqueId())
                && !eco.getItemsToAdd().get(target.getUniqueId()).isEmpty()) {
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.claim.pending", target);
        }
    }
}
