package com.gotofinal.darkrise.economy;

import com.gotofinal.darkrise.economy.item.DarkRiseItemImpl;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import su.nightexpress.divineitems.DivineItems;
import su.nightexpress.divineitems.api.ItemAPI;
import su.nightexpress.divineitems.modules.tiers.TierManager;
import su.nightexpress.divineitems.nms.NBTAttribute;
import su.nightexpress.divineitems.utils.ItemUtils;
import su.nightexpress.divineitems.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DivineItemsHook
{
    private static boolean enabled = true;

    public static boolean isEnabled()
    {
        return enabled;
    }

    public static void setEnabled(boolean enabled)
    {
        DivineItemsHook.enabled = enabled;
    }

    public static ItemStack replace(ItemStack itemStack, DarkRiseItemImpl.DivineItemsMeta dMeta)
    {
        TierManager tierManager = DivineItems.getInstance().getMM().getTierManager();
        TierManager.Tier tier = tierManager.getTierById(dMeta.tierName);
        Validate.notNull(tier, "Invalid tier: " + dMeta.tierName);
        itemStack = tierManager.setMetaFlags(itemStack, tier);
        itemStack = tierManager.replaceClass(itemStack ,tier);
        itemStack = tierManager.replaceAmmoTypes(itemStack ,tier);
        itemStack = tierManager.replaceArmorTypes(itemStack ,tier, dMeta.tierLevel);
        itemStack = tierManager.replaceDamageTypes(itemStack ,tier, dMeta.tierLevel);
        itemStack = tierManager.replaceEnchants(itemStack);
//        itemStack = tierManager.replaceLevel(itemStack, dMeta.tierLevel);
        itemStack = tierManager.replaceClass(itemStack, tier);
        itemStack = tierManager.replaceSet(itemStack, tier);
        itemStack = tierManager.replaceSlots(itemStack, tier);
        itemStack = tierManager.replaceSoul(itemStack);
        itemStack = tierManager.replaceUntrade(itemStack);

        return tierManager.replaceAttributes(itemStack, tier, dMeta.tierLevel);
    }

    public static ItemStack create(ItemStack localItemStack, DarkRiseItemImpl.DivineItemsMeta dMeta)
    {
        TierManager tierManager = DivineItems.getInstance().getMM().getTierManager();
        TierManager.Tier tier = tierManager.getTierById(dMeta.tierName);
        Validate.notNull(tier, "Invalid tier: " + dMeta.tierName);
        int paramInt = 0;

//        if (getMaterials().size() <= 0) return localItemStack;
//        if ((paramMaterial != null) && (getMaterials().contains(paramMaterial))) {
//            localItemStack.setType(paramMaterial);
//        }
//        else {
//            localItemStack.setType((Material)getMaterials().get(tierManager.r.nextInt(getMaterials().size())));
//        }

        ItemMeta localItemMeta = localItemStack.getItemMeta();
        List localList1 = tier.getDatas();
        if (tier.getDataSpecial().containsKey(localItemStack.getType().name())) {
            localList1 = tier.getDataSpecial().get(localItemStack.getType().name());
        }

        if (!localList1.isEmpty()) {
            if (tier.isDataUnbreak()) { localItemMeta.spigot().setUnbreakable(true);
            }
            if (tier.isDataReversed()) {
//                localItemStack.setDurability((short)((Integer)localList1.get(tierManager.r.nextInt(localList1.size()))).intValue());
            }
            else {
                int i = Utils.randInt(1, localItemStack.getType().getMaxDurability());
                while (localList1.contains(i)) {
                    i = Utils.randInt(1, localItemStack.getType().getMaxDurability());
                }
                localItemStack.setDurability((short) i);
            }
        }

//        localItemMeta.setLore(tier.getLore());


//        String str1 = localItemStack.getType().name();
//        String str2 = "";
//        String str3 = "";
//        String str4 = "";
//        String str5 = "";
//        String str6 = "";
//        String str7 = "";
//        String str8 = "";
//
//
//
//        List localList2 = tier.getSource().get(ResourceType.PREFIX);
//        if (!localList2.isEmpty()) { str2 = (String)localList2.get(tierManager.r.nextInt(localList2.size()));
//        }
//        List localList3 = tier.getSource().get(ResourceType.SUFFIX);
//        if (!localList3.isEmpty()) { str3 = (String)localList3.get(tierManager.r.nextInt(localList3.size()));
//        }
//
//
//        List localList4 = Resources.getSourceByFullType(ResourceType.PREFIX, str1);
//        List localList5 = Resources.getSourceByFullType(ResourceType.SUFFIX, str1);
//        if (localList4.size() > 0) str5 = (String)localList4.get(tierManager.r.nextInt(localList4.size()));
//        if (localList5.size() > 0) { str6 = (String)localList5.get(tierManager.r.nextInt(localList5.size()));
//        }
//
//
//        List localList6 = Resources.getSourceByHalfType(ResourceType.PREFIX, str1);
//        List localList7 = Resources.getSourceByHalfType(ResourceType.SUFFIX, str1);
//        if ((localList6 != null) && (localList6.size() > 0)) {
//            str7 = (String)localList6.get(tierManager.r.nextInt(localList6.size()));
//        }
//
//        if ((localList7 != null) && (localList7.size() > 0)) {
//            str8 = (String)localList7.get(tierManager.r.nextInt(localList7.size()));
//        }
//
//        if (str1.split("_").length == 2) {
//            str4 = su.nightexpress.divineitems.config.Lang.getHalfType(localItemStack.getType());
//        }
//        else {
//            str4 = DivineItems.getInstance().getCM().getDefaultItemName(localItemStack);
//        }



//        String str9 = getMetaName()
//                .replace("%itemtype%", str4)
//                .replace("%suffix_tier%", str3)
//                .replace("%prefix_tier%", str2)
//
//                .replace("%prefix_type%", str7)
//                .replace("%suffix_type%", str8)
//
//                .replace("%prefix_material%", str5)
//                .replace("%suffix_material%", str6)
//
//                .replace("%c%", "");
//        str9 = str9.trim().replaceAll("\\s+", " ");
//        str9 = getColor() + str9;
//
//
//        localItemMeta.setDisplayName(str9);
//        localItemStack.setItemMeta(localItemMeta);


//        if (localItemStack.getType().name().startsWith("LEATHER_")) {
//            localObject1 = (LeatherArmorMeta)localItemStack.getItemMeta();
//            if (isRandomLeatherColor()) {
//                ((LeatherArmorMeta)localObject1).setColor(Color.fromRGB(tierManager.r.nextInt(255), tierManager.r.nextInt(255), tierManager.r.nextInt(255)));
//            }
//            else {
//                ((LeatherArmorMeta)localObject1).setColor(getLeatherColor());
//            }
//            localItemStack.setItemMeta((ItemMeta)localObject1);
//        }
//        else if (localItemStack.getType() == Material.SHIELD) {
//            localItemMeta = localItemStack.getItemMeta();
//            localObject1 = (BlockStateMeta)localItemMeta;
//            if ((localObject1 != null) && (((BlockStateMeta)localObject1).hasBlockState()) && (((BlockStateMeta)localObject1).getBlockState() != null)) {
//                Banner localBanner = (Banner)((BlockStateMeta)localObject1).getBlockState();
//
//                DyeColor[] arrayOfDyeColor1 = DyeColor.values();
//                DyeColor localDyeColor1 = arrayOfDyeColor1[tierManager.r.nextInt(arrayOfDyeColor1.length - 1)];
//
//                localBanner.setBaseColor(localDyeColor1);
//
//                PatternType[] arrayOfPatternType = PatternType.values();
//                PatternType localPatternType = arrayOfPatternType[tierManager.r.nextInt(arrayOfPatternType.length - 1)];
//
//                DyeColor[] arrayOfDyeColor2 = DyeColor.values();
//                DyeColor localDyeColor2 = arrayOfDyeColor2[tierManager.r.nextInt(arrayOfDyeColor2.length - 1)];
//
//                localBanner.addPattern(new org.bukkit.block.banner.Pattern(localDyeColor2, localPatternType));
//                localBanner.update();
//                ((BlockStateMeta)localObject1).setBlockState(localBanner);
//                localItemStack.setItemMeta((ItemMeta)localObject1);
//            }
//        }






//        String[] localObject1 = tier.getLevels().split("-");
//
//        int j = 1;
//        int k = 1;
//        try
//        {
//            j = Integer.parseInt(localObject1[0]);
//            k = Integer.parseInt(localObject1[1]);
//        }
//        catch (NumberFormatException|ArrayIndexOutOfBoundsException ignored) {}
//        if ((paramInt > 0) && (paramInt > k)) paramInt = k;
//        if ((paramInt > 0) && (paramInt < j)) { paramInt = j;
//        }
//        if (paramInt <= 0) {
//            paramInt = tierManager.r.nextInt(k - j + 1) + j;
//        }

        double d1 = (tier.getLevelScale() * 100.0D - 100.0D) * paramInt / 100.0D + 1.0D;

//        int m = tier.getMinEnchantments();
//        int n = tier.getMaxEnchantments();
//
//        if ((m >= 0) && (n >= 0)) {
//            int i1 = tierManager.r.nextInt(n - m + 1) + m;
//            List<Object> localArrayList = new ArrayList<>();
//            for (Object localObject2 = tier.getEnchantments().keySet().iterator(); ((Iterator)localObject2).hasNext();) { Enchantment localEnchantment = (Enchantment)((Iterator)localObject2).next();
//                localArrayList.add(localEnchantment);
//            }
//            for (int i2 = 0; i2 < i1; i2++) {
//                if (localArrayList.isEmpty())
//                    break;
//                Enchantment enchantment = (Enchantment) localArrayList.get(tierManager.r.nextInt(localArrayList.size()));
//                double d2 = Math.max(1, Integer.parseInt(tier.getEnchantments().get(enchantment).split(":")[0]));
//                double d3 = Math.max(1, Integer.parseInt(tier.getEnchantments().get(enchantment).split(":")[1]));
//
//                int i3 = Utils.randInt((int)d2, (int)d3) - 1;
//                if (tier.isSafeEnchant()) {
//                    if (enchantment.canEnchantItem(localItemStack)) {
//                        localItemStack.addUnsafeEnchantment(enchantment, i3);
//                    }
//                    else {
//                        i2--;
//                    }
//                }
//                else {
//                    localItemStack.addUnsafeEnchantment(enchantment, i3);
//                }
//
//                localArrayList.remove(enchantment);
//            }
//        }


        localItemStack = tierManager.replaceDamageTypes(localItemStack, tier, d1);
        localItemStack = tierManager.replaceArmorTypes(localItemStack, tier, d1);
        localItemStack = tierManager.replaceAmmoTypes(localItemStack, tier);


        localItemStack = tierManager.replaceLevel(localItemStack, paramInt);
        localItemStack = tierManager.replaceClass(localItemStack, tier);
        localItemStack = tierManager.replaceEnchants(localItemStack);


        if (DivineItems.getInstance().getMM().getSoulboundManager().isActive()) {
            boolean bool1 = tier.isNeedSoul();
            boolean bool2 = tier.isNonTrade();
            if ((bool1) && (bool2)) {
                bool1 = false;
                bool2 = false;
            }
            if (bool1) {
                localItemStack = new ItemStack(tierManager.replaceSoul(localItemStack));
            }
            else if (bool2) {
                localItemStack = new ItemStack(tierManager.replaceUntrade(localItemStack));
            }
            else {
                localItemStack = new ItemStack(tierManager.replaceLore(localItemStack, "SOULBOUND", "delz"));
            }
        }
        else {
            localItemStack = new ItemStack(tierManager.replaceLore(localItemStack, "SOULBOUND", "delz"));
        }


        localItemStack = new ItemStack(tierManager.setMetaFlags(localItemStack, tier));
        localItemStack = new ItemStack(tierManager.replaceAttributes(localItemStack, tier, d1));
        localItemStack = new ItemStack(tierManager.replaceSlots(localItemStack, tier));

        String str10;

        if (localItemStack.getType().name().split("_").length == 2) {
            str10 = su.nightexpress.divineitems.config.Lang.getHalfType(localItemStack.getType());
        }
        else {
            str10 = DivineItems.getInstance().getCM().getDefaultItemName(localItemStack);
        }

        localItemStack = tierManager.replaceSet(localItemStack, tier);
        localItemStack = tierManager.replaceLore(localItemStack, "TYPE", str10);
        localItemStack = tierManager.replaceLore(localItemStack, "TIER", tier.getName());
        localItemStack = tierManager.replaceLore(localItemStack, "DEFENSE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "DAMAGE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "POISON_DEFENSE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "MAGIC_DEFENSE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "FIRE_DEFENSE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "WATER_DEFENSE", "delz");
        localItemStack = tierManager.replaceLore(localItemStack, "WIND_DEFENSE", "delz");
        localItemStack = replaceLoreAll(localItemStack, "c", tier.getColor());

        localItemStack = ItemAPI.addNBTTag(localItemStack, "TIER", tier.getId());
        if (ItemUtils.isArmor(localItemStack)) {
            localItemStack = DivineItems.getInstance().getNMS().setNBTAtt(localItemStack, NBTAttribute.attackDamage, 0.0D);
            localItemStack = DivineItems.getInstance().getNMS().setNBTAtt(localItemStack, NBTAttribute.armor, ItemAPI.getDefaultDefense(localItemStack));
            localItemStack = DivineItems.getInstance().getNMS().setNBTAtt(localItemStack, NBTAttribute.armorToughness, ItemAPI.getDefaultToughness(localItemStack));
        }

        return localItemStack;
    }

    private static ItemStack replaceLoreAll(ItemStack var1, String find, String replace) {
        ItemMeta var4 = var1.getItemMeta();
        List<String> lore = var4.getLore();
        if (lore == null) {
            return var1;
        } else {
            String placeholder = "%" + find + "%";

            for(String line : new ArrayList<>(lore)) {
                if(line.contains(placeholder)) {
                    int index = lore.indexOf(line);
                    lore.remove(index);
                    if(!replace.equals("delz") && !replace.equals("")) {
                        lore.add(index, line.replace(placeholder, replace));
                    }
                }
            }

            var4.setLore(lore);
            var1.setItemMeta(var4);
            return var1;
        }
    }
}
