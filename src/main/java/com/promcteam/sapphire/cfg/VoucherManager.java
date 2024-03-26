package com.promcteam.sapphire.cfg;

import com.promcteam.sapphire.Sapphire;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VoucherManager {
    private static VoucherManager            instance;
    private final  File                      vouchersFile;
    private        int                       nextId    = 1;
    private final  Map<Integer, VoucherData> data      = new HashMap<>();
    private final  Pattern                   idPattern = Pattern.compile("[0-9]+");

    public class VoucherData implements ConfigurationSerializable {
        public final int  id;
        public final UUID playerUUID;
        public final long timestamp;

        public VoucherData(int id, UUID playerUUID, long timestamp) {
            this.id = id;
            this.playerUUID = playerUUID;
            this.timestamp = timestamp;
        }

        public VoucherData(Map<String, Object> map) {
            this.id = (int) map.get("id");
            this.playerUUID = UUID.fromString((String) map.get("uuid"));
            this.timestamp = (int) map.get("timestamp");
        }

        @Override
        public Map<String, Object> serialize() {
            final Map<String, Object> map = new HashMap<>();
            map.put("id", this.id);
            map.put("uuid", this.playerUUID.toString());
            map.put("timestamp", this.timestamp);
            return map;
        }
    }

    private VoucherManager() {
        vouchersFile = new File(Sapphire.getInstance().getDataFolder(), "vouchers.yml");
    }

    public static VoucherManager getInstance() {
        if (instance == null) {
            instance = new VoucherManager();
        }

        return instance;
    }

    public boolean isVoucher(ItemStack item) {
        return item.hasItemMeta()
                && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().stream().anyMatch(s -> s.contains("Voucher"));
    }

    public int getId(ItemStack item) {
        Optional<String> line = item.getItemMeta()
                .getLore()
                .stream()
                .filter(s -> s.contains("Voucher"))
                .findFirst();

        if (line.isPresent()) {
            Matcher match = idPattern.matcher(line.get());

            if (match.find()) {
                return Integer.parseInt(match.group());
            }
        }

        return 0;
    }

    public ItemStack addNextId(ItemStack item) {
        return addId(item, this.nextId++);
    }

    public static ItemStack addId(ItemStack item, int id) {
        ItemMeta     meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            if (line.contains("{VOUCHER_ID}")) {
                line = line.replace("{VOUCHER_ID}", String.valueOf(id));
            }

            lore.set(i, line);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void load() throws IOException {
        if (!vouchersFile.exists()) {
            if (vouchersFile.createNewFile()) {
                nextId = 1;
            } else {
                Sapphire.getInstance().getLogger().severe("Failed while creating vouchers.yml vouchersFile.");
            }

            return;
        }

        Configuration conf = YamlConfiguration.loadConfiguration(vouchersFile);

        data.clear();
        //noinspection unchecked
        conf.getMapList("data")
                .stream()
                .map(map -> new VoucherData((Map<String, Object>) map))
                .forEach(d -> data.put(d.id, d));
        this.nextId = conf.getInt("nextid");
    }

    public void save() throws IOException {
        YamlConfiguration conf = new YamlConfiguration();
        conf.set("nextid", this.nextId);
        conf.set("data", this.data.values().stream().map(VoucherData::serialize).collect(Collectors.toList()));
        conf.save(vouchersFile);
    }

    public void use(Player player, ItemStack item) {
        VoucherData voucherData = new VoucherData(getId(item), player.getUniqueId(), System.currentTimeMillis() / 1000);
        data.put(voucherData.id, voucherData);
    }

    public Optional<VoucherData> getData(int id) {
        return Optional.ofNullable(data.get(id));
    }

    public Optional<VoucherData> getData(ItemStack item) {
        return isVoucher(item) ? getData(getId(item)) : Optional.empty();
    }
}
