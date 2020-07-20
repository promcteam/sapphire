package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class EconTestItemCommand extends RiseCommand {
    private final DarkRiseEconomy eco;

    public EconTestItemCommand(final DarkRiseEconomy plugin, final EconCommand command) {
        super("testItem", new ArrayList<>(Arrays.asList("ti")), command);
        setUsage("Insufficient Arguments");
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage("senderIsNotPlayer", sender);
            return;
        }
        if (!this.checkPermission(sender, "econ.items.create")) {
            return;
        }
        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() == Material.AIR) {
            MessageUtil.sendMessage("economy.commands.create.no-item", sender);
            return;
        }

        DarkRiseEconomy.getInstance().getConfig().set("item", mainHand);
        DarkRiseEconomy.getInstance().getConfig().set("itemMeta", mainHand.getItemMeta());
        DarkRiseEconomy.getInstance().saveConfig();

        MessageUtil.sendMessage("economy.commands.create.done", sender);
    }
}
