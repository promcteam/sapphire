package studio.magemonkey.sapphire;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class Meta {
    public static void setMetadata(Metadatable obj, String key, Object value) {
        obj.setMetadata(key, new FixedMetadataValue(Sapphire.getInstance(), value));
    }

    public static MetadataValue getMetadata(Metadatable obj, String key) {
        for (MetadataValue value : obj.getMetadata(key)) {
            if (value.getOwningPlugin()
                    .getDescription()
                    .getName()
                    .equals(Sapphire.getInstance().getDescription().getName()))
                return value;
        }
        return null;
    }

    public static void removeMetadata(Metadatable obj, String key) {
        obj.removeMetadata(key, Sapphire.getInstance());
    }

    public static void protect(Block block) {
        setMetadata(block, "protected", Boolean.valueOf(true));
    }

    public static void unprotect(Block block) {
        removeMetadata(block, "protected");
    }

    public static boolean isProtected(Block block) {
        MetadataValue metadataValue = getMetadata(block, "protected");
        return (metadataValue != null && metadataValue.asBoolean());
    }
}
