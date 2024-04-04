package studio.magemonkey.sapphire.commands;

import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.codex.util.messages.MessageUtil;
import studio.magemonkey.sapphire.Sapphire;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class SapphireTestItemEqualityCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireTestItemEqualityCommand(final Sapphire plugin, final SapphireCommand command) {
        super("testItemEquality", new ArrayList<>(Collections.singletonList("tie")), command);
        setUsage("Insufficient Arguments");
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage("senderIsNotPlayer", sender);
            return;
        }
        if (!this.checkPermission(sender, "sapphire.items.create")) {
            return;
        }
        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() == Material.AIR) {
            MessageUtil.sendMessage("sapphire.commands.create.no-item", sender);
            return;
        }

        ItemStack confItem = ((ItemStack) Sapphire.getInstance().getConfig().get("item"));

        sender.sendMessage("Base Check: " + confItem.isSimilar(mainHand));
        sender.sendMessage("Type: " + (confItem.getType() == mainHand.getType()));
        sender.sendMessage("Durability: " + (confItem.getDurability() == mainHand.getDurability()));
        sender.sendMessage("Has Meta: " + (confItem.hasItemMeta() == mainHand.hasItemMeta()));
        sender.sendMessage("Meta: " + (!confItem.hasItemMeta() || Bukkit.getItemFactory()
                .equals(confItem.getItemMeta(), mainHand.getItemMeta())));
    }
}
