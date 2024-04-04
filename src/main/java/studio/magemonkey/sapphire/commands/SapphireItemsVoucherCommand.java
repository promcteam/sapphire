package studio.magemonkey.sapphire.commands;

import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.legacy.riseitem.DarkRiseItem;
import studio.magemonkey.codex.util.messages.MessageData;
import studio.magemonkey.codex.util.messages.MessageUtil;
import studio.magemonkey.sapphire.Sapphire;
import studio.magemonkey.sapphire.cfg.VoucherManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "voucher")
public class SapphireItemsVoucherCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireItemsVoucherCommand(Sapphire plugin, SapphireItemsCommand command) {
        super("voucher", List.of("voucter"), command);
        this.eco = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
        if (!this.checkPermission(sender, "sapphire.items.voucher")) {
            return;
        }

        if (args.length != 2 && args.length != 3) {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage("notAPlayer", sender, new MessageData("name", args[0]));
            return;
        }

        DarkRiseItem riseItem = this.eco.getItems().getItemById(args[1]);
        if (riseItem == null) {
            MessageUtil.sendMessage("sapphire.commands.noItem", sender, new MessageData("name", args[1]));
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            Integer i;
            try {
                i = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage("notANumber", sender, new MessageData("text", args[2]));
                return;
            }
            amount = i;
        }

        ItemStack                   item     = VoucherManager.getInstance().addNextId(riseItem.getItem(amount));
        Player                      player   = target != null ? target : (Player) sender;
        HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);

        if (!notAdded.isEmpty()) {
            notAdded.forEach((a, i) -> {
                i = i.clone();
                i.setAmount(a);
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
            });
        }
    }
}
