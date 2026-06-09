package elucent.eidolon.capability;

import elucent.eidolon.CommonConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public final class SoulData {
    private static final String ROOT = "eidolonSoul";

    private SoulData() {
    }

    public static boolean hasEtherealHealth(EntityPlayer player) {
        return getMaxEtherealHealth(player) > 0.0F;
    }

    public static float getMaxEtherealHealth(EntityPlayer player) {
        return getTag(player).getFloat("maxEtherealHealth");
    }

    public static float getEtherealHealth(EntityPlayer player) {
        return getTag(player).getFloat("etherealHealth");
    }

    public static void setMaxEtherealHealth(EntityPlayer player, float max) {
        NBTTagCompound tag = getTag(player);
        float clamped = clamp(max, 0.0F, CommonConfig.maxEtherealHealth());
        tag.setFloat("maxEtherealHealth", clamped);
        tag.setFloat("etherealHealth", Math.min(clamped, tag.getFloat("etherealHealth")));
    }

    public static void setEtherealHealth(EntityPlayer player, float health) {
        NBTTagCompound tag = getTag(player);
        tag.setFloat("etherealHealth", clamp(health, 0.0F, tag.getFloat("maxEtherealHealth")));
    }

    public static void hurtEtherealHealth(EntityPlayer player, float amount, float persistentHealth) {
        amount = Math.max(0.0F, amount);
        float oldHealth = getEtherealHealth(player);
        setMaxEtherealHealth(player, Math.max(getMaxEtherealHealth(player) - amount,
                Math.min(persistentHealth, getMaxEtherealHealth(player))));
        setEtherealHealth(player, oldHealth - amount);
    }

    public static void healEtherealHealth(EntityPlayer player, float amount, float persistentHealth) {
        amount = Math.max(0.0F, amount);
        setEtherealHealth(player, Math.min(Math.max(getEtherealHealth(player), persistentHealth),
                getEtherealHealth(player) + amount));
    }

    public static boolean hasMagic(EntityPlayer player) {
        return getMaxMagic(player) > 0.0F;
    }

    public static float getMaxMagic(EntityPlayer player) {
        return getTag(player).getFloat("maxMagic");
    }

    public static float getMagic(EntityPlayer player) {
        return getTag(player).getFloat("magic");
    }

    public static void setMaxMagic(EntityPlayer player, float max) {
        NBTTagCompound tag = getTag(player);
        tag.setFloat("maxMagic", Math.max(0.0F, max));
        tag.setFloat("magic", Math.min(tag.getFloat("maxMagic"), tag.getFloat("magic")));
    }

    public static void setMagic(EntityPlayer player, float magic) {
        NBTTagCompound tag = getTag(player);
        tag.setFloat("magic", clamp(magic, 0.0F, tag.getFloat("maxMagic")));
    }

    public static boolean takeMagic(EntityPlayer player, float amount) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        if (getMagic(player) < amount) {
            return false;
        }
        setMagic(player, getMagic(player) - Math.max(0.0F, amount));
        return true;
    }

    public static void giveMagic(EntityPlayer player, float amount) {
        setMagic(player, getMagic(player) + Math.max(0.0F, amount));
    }

    public static void ensureDefaults(EntityPlayer player) {
        NBTTagCompound tag = getTag(player);
        if (!tag.hasKey("maxMagic")) {
            tag.setFloat("maxMagic", 0.0F);
            tag.setFloat("magic", 0.0F);
        }
        if (!tag.hasKey("maxEtherealHealth")) {
            tag.setFloat("maxEtherealHealth", 0.0F);
            tag.setFloat("etherealHealth", 0.0F);
        }
    }

    private static NBTTagCompound getTag(EntityPlayer player) {
        NBTTagCompound persisted = getPersistedTag(player);
        if (!persisted.hasKey(ROOT)) {
            persisted.setTag(ROOT, new NBTTagCompound());
        }
        return persisted.getCompoundTag(ROOT);
    }

    private static NBTTagCompound getPersistedTag(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
