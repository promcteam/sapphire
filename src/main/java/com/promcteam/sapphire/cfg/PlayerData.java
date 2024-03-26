package com.promcteam.sapphire.cfg;

import com.promcteam.sapphire.Sapphire;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class PlayerData {
    private static final Map<UUID, List<ItemStack>> items = new HashMap<>(20, .4f);
    private static       File                       dataFile;
    private static       FileConfiguration          cfg;
    // if server crashes/stops when player die, to have sure that he don't lose items

    private PlayerData() {
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        dataFile = new File(Sapphire.getInstance().getDataFolder(), "players.yml");
        if (!dataFile.exists()) {
            dataFile.getAbsoluteFile().getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        cfg = YamlConfiguration.loadConfiguration(dataFile);

        for (final String sUuid : cfg.getKeys(false)) {
            final List<ItemStack> its = (List<ItemStack>) cfg.getList(sUuid);
            if ((its == null) || its.isEmpty()) {
                continue;
            }
            items.put(UUID.fromString(sUuid), its);
        }
    }

    public static void dumpPlayer(final Player player, ArrayList<ItemStack> keeps) {
//        final List<ItemBuilder> items = new ArrayList<>(5);
        Map<String, Integer> removes = new HashMap<>(20);
        if (keeps.isEmpty()) return;
//        for (ItemStack itemStack : keeps) {
//            items.add(ItemBuilder.newItem(itemStack));
//        }

//        if (items.isEmpty()) return;

        PlayerData.items.put(player.getUniqueId(), keeps);
        cfg.set(player.getUniqueId().toString(), keeps);
        try {
            cfg.save(dataFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPlayer(final Player player) {
        final List<ItemStack> items = PlayerData.items.remove(player.getUniqueId());
        if ((items == null) || items.isEmpty()) {
            return;
        }
        cfg.set(player.getUniqueId().toString(), null);
        try {
            cfg.save(dataFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        player.getInventory().addItem(items.toArray(new ItemStack[0]));
    }
}
