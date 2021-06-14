package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class EconTestItemEqualityCommand extends RiseCommand {
    private final DarkRiseEconomy eco;

    public EconTestItemEqualityCommand(final DarkRiseEconomy plugin, final EconCommand command) {
        super("testItemEquality", new ArrayList<>(Arrays.asList("tie")), command);
        setUsage("Insufficient Arguments");
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage("senderIsNotPlayer", sender);
            return;
        }
        if (!this.checkPermission(sender, "pmcu.items.create")) {
            return;
        }
        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() == Material.AIR) {
            MessageUtil.sendMessage("economy.commands.create.no-item", sender);
            return;
        }

        ItemStack confItem = ((ItemStack) DarkRiseEconomy.getInstance().getConfig().get("item"));

        sender.sendMessage("Base Check: " + confItem.isSimilar(mainHand));
        sender.sendMessage("Type: " + (confItem.getType() == mainHand.getType()));
        sender.sendMessage("Durability: " + (confItem.getDurability() == mainHand.getDurability()));
        sender.sendMessage("Has Meta: " + (confItem.hasItemMeta() == mainHand.hasItemMeta()));
        sender.sendMessage("Meta: " + (!confItem.hasItemMeta() || Bukkit.getItemFactory().equals(confItem.getItemMeta(), mainHand.getItemMeta())));
    }
}
