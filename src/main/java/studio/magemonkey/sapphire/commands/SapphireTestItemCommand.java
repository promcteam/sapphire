package studio.magemonkey.sapphire.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studio.magemonkey.codex.CodexEngine;
import studio.magemonkey.codex.legacy.command.RiseCommand;
import studio.magemonkey.sapphire.Sapphire;

import java.util.ArrayList;
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
            CodexEngine.get().getMessageUtil().sendMessage("senderIsNotPlayer", sender);
            return;
        }
        if (!this.checkPermission(sender, "sapphire.items.create")) {
            return;
        }
        ItemStack mainHand = ((Player) sender).getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() == Material.AIR) {
            CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.create.no-item", sender);
            return;
        }

        Sapphire.getInstance().getConfig().set("item", mainHand);
        Sapphire.getInstance().getConfig().set("itemMeta", mainHand.getItemMeta());
        Sapphire.getInstance().saveConfig();

        CodexEngine.get().getMessageUtil().sendMessage("sapphire.commands.create.done", sender);
    }
}
