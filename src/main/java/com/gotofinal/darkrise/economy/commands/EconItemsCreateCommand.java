package com.gotofinal.darkrise.economy.commands;

import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import me.travja.darkrise.core.command.RiseCommand;
import me.travja.darkrise.core.item.DarkRiseItemImpl;
import me.travja.darkrise.core.legacy.util.message.MessageData;
import me.travja.darkrise.core.legacy.util.message.MessageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

//@DarkRiseSubCommand(value = EconItemsCommand.class, name = "create", aliases = {"create", "c"})
public class EconItemsCreateCommand extends RiseCommand {
    private static final int PAGE_SIZE = 15;

    private final DarkRiseEconomy eco;

    public EconItemsCreateCommand(final DarkRiseEconomy plugin, final EconItemsCommand command) {
        super("create", new ArrayList<>(Arrays.asList("create", "c")), command);
        setUsage("Insufficient Arguments");
        this.eco = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, RiseCommand command, String label, String[] args) {
        if (args.length == 0) {
            this.sendUsage(command.getUsage(), sender, command, args);
            return;
        }
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
        String id = args[0];
        boolean dropOnDeath = false;
        boolean confirmOnUse = false;
        boolean canDrop = true;
        int removeOnDeath = 0;
        int removeOnUse = 0;
        String fileName = id + ".yml";
        DoubleRange chanceToLostDurability = new DoubleRange(0d, 0d);
        String prev = "";
//        Iterator<String> iterator = args.iterator();
//        iterator.next();


//        while (iterator.hasNext()) {
//            final String arg = iterator.next();
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-dropOnDeath")) {
                dropOnDeath = true;
            } else if (arg.equalsIgnoreCase("-dontDropOnDeath")
                    || arg.equalsIgnoreCase("-don'tDropOnDeath")
                    || arg.equalsIgnoreCase("-doNotDropOnDeath")) {
                dropOnDeath = false;
            } else if (arg.equalsIgnoreCase("-confirmOnUse")) {
                confirmOnUse = true;
            } else if (arg.equalsIgnoreCase("-dontConfirmOnUse")
                    || arg.equalsIgnoreCase("-don'tConfirmOnUse")
                    || arg.equalsIgnoreCase("-doNotConfirmOnUse")) {
                confirmOnUse = false;
            } else if (arg.equalsIgnoreCase("-canDrop")) {
                canDrop = true;
            } else if (arg.equalsIgnoreCase("-dontCanDrop")
                    || arg.equalsIgnoreCase("-doNotCanDrop")
                    || arg.equalsIgnoreCase("-CantDrop")
                    || arg.equalsIgnoreCase("-Can'tDrop")
                    || arg.equalsIgnoreCase("-CanNotDrop")) {
                canDrop = false;
            } else if (prev.equalsIgnoreCase("-removeOnDeath")
                    || prev.equalsIgnoreCase("-remOnDeath")
                    || prev.equalsIgnoreCase("-delOnDeath")
                    || prev.equalsIgnoreCase("-deleteOnDeath")) {
                prev = "";
                int i;
                try {
                    i = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    MessageUtil.sendMessage("notANumber", sender, new MessageData("text", arg));
                    return;
                }
                removeOnDeath = i;
            } else if (prev.equalsIgnoreCase("-removeOnUse")
                    || prev.equalsIgnoreCase("-remOnUse")
                    || prev.equalsIgnoreCase("-delOnUse")
                    || prev.equalsIgnoreCase("-deleteOnUse")) {
                prev = "";
                int i;
                try {
                    i = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                    MessageUtil.sendMessage("notANumber", sender, new MessageData("text", arg));
                    return;
                }
                removeOnUse = i;
            } else if (prev.equalsIgnoreCase("-file") || prev.equalsIgnoreCase("-f") || prev.equalsIgnoreCase("-catalog")) {
                prev = "";
                fileName = arg;
            } else if (prev.equalsIgnoreCase("-dura") || prev.equalsIgnoreCase("-durability")) {
                prev = "";
                DoubleRange doubleRange = new DoubleRange(0d, Double.parseDouble(arg));
                if (doubleRange == null) {
                    this.sendUsage(command.getUsage(), sender, command, args);
                    return;
                }
                chanceToLostDurability = doubleRange;
            } else if (!prev.equalsIgnoreCase("")) {
                this.sendUsage(command.getUsage(), sender, command, args);
                return;
            } else {
                prev = arg;
            }
        }

        if (!mainHand.hasItemMeta()) {
            ItemMeta meta = mainHand.getItemMeta();
            meta.setDisplayName(StringUtils.capitalize(mainHand.getType().name().toLowerCase()));
            mainHand.setItemMeta(meta);
        }

        DarkRiseItemImpl riseItem = new DarkRiseItemImpl(id, mainHand, dropOnDeath, removeOnDeath, confirmOnUse,
                removeOnUse, canDrop, !chanceToLostDurability.equals(new DoubleRange(0d)),
                chanceToLostDurability, new ArrayList<>());
        try {
            this.eco.getItems().addItem(fileName, riseItem, true);
        } catch (IllegalArgumentException e) {
            this.eco.getLogger().warning("Could not load item '" + fileName + "'");
            e.printStackTrace();
        }
        MessageUtil.sendMessage("economy.commands.create.done", sender, new MessageData("riseItem", riseItem));
    }
}
