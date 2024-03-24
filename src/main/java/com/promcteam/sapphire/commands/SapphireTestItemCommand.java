package com.promcteam.sapphire.commands;

import com.promcteam.sapphire.Sapphire;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class SapphireTestItemCommand extends RiseCommand {
    private final Sapphire eco;

    public SapphireTestItemCommand(final Sapphire plugin, final SapphireCommand command) {
        super("testItem", new ArrayList<>(Collections.singletonList("ti")), command);
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

        Sapphire.getInstance().getConfig().set("item", mainHand);
        Sapphire.getInstance().getConfig().set("itemMeta", mainHand.getItemMeta());
        Sapphire.getInstance().saveConfig();

        MessageUtil.sendMessage("sapphire.commands.create.done", sender);
    }
}
