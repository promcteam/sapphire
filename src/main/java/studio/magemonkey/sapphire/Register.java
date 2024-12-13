package studio.magemonkey.sapphire;

import org.bukkit.plugin.PluginManager;
import studio.magemonkey.sapphire.commands.*;
import studio.magemonkey.sapphire.listener.PlayerListener;

public class Register {

    public static void register(Sapphire plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(plugin), plugin);
//        pm.registerEvents(new TwoHandListener(), plugin);

        SapphireCommand econ = new SapphireCommand(plugin);
        new SapphireClaimCommand(plugin, econ);
        SapphireItemsCommand items = new SapphireItemsCommand(plugin, econ);
        new SapphireItemsCreateCommand(plugin, items);
        new SapphireItemsDeleteCommand(plugin, items);
        new SapphireItemsGiveCommand(plugin, items);
        new SapphireItemsDropCommand(plugin, items);
        new SapphireItemsListCommand(plugin, items);
        new SapphireItemsVoucherCommand(plugin, items);
        new SapphireReloadCommand(plugin, econ);
        new SapphireSaveCommand(plugin, econ);
        new SapphireVoucherCommand(plugin, econ);

        //FOR TESTING PURPOSES ONLY
//        new EconTestItemCommand(plugin, econ);
//        new EconTestItemEqualityCommand(plugin, econ);
    }

}
