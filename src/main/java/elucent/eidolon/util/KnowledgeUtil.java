package elucent.eidolon.util;

import elucent.eidolon.network.MagicKnowledgeSyncPacket;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.KnowledgeResetPacket;
import elucent.eidolon.network.KnowledgeSyncPacket;
import elucent.eidolon.spell.Sign;
import elucent.eidolon.spell.Rune;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashSet;
import java.util.Set;

public final class KnowledgeUtil {
    private static final String ROOT = "eidolonKnownResearch";
    private static final String SIGNS_ROOT = "eidolonKnownSigns";
    private static final String FACTS_ROOT = "eidolonKnownFacts";
    private static final String RUNES_ROOT = "eidolonKnownRunes";

    private KnowledgeUtil() {
    }

    public static boolean knowsResearch(EntityPlayer player, ResourceLocation research) {
        return research != null && getResearchTag(player).getBoolean(research.toString());
    }

    public static void grantResearch(EntityPlayer player, ResourceLocation research) {
        if (research != null && !knowsResearch(player, research)) {
            getResearchTag(player).setBoolean(research.toString(), true);
            syncResearch(player, research, true);
            notifyResearch(player, research);
        }
    }

    public static void removeResearch(EntityPlayer player, ResourceLocation research) {
        if (research != null && knowsResearch(player, research)) {
            getResearchTag(player).removeTag(research.toString());
            syncResearch(player, research, false);
        }
    }

    public static void setResearchKnown(EntityPlayer player, ResourceLocation research, boolean known) {
        setKnown(player, ROOT, research, known);
    }

    public static void clearResearch(EntityPlayer player) {
        Set<String> known = new HashSet<>(getResearchTag(player).getKeySet());
        getPersistedTag(player).removeTag(ROOT);
        for (String id : known) {
            syncResearch(player, new ResourceLocation(id), false);
        }
    }

    public static Set<String> getKnownResearchIds(EntityPlayer player) {
        return new HashSet<>(getResearchTag(player).getKeySet());
    }

    public static boolean knowsSign(EntityPlayer player, Sign sign) {
        return sign != null && getSignTag(player).getBoolean(sign.getRegistryName().toString());
    }

    public static void grantSign(EntityPlayer player, Sign sign) {
        if (sign != null && !knowsSign(player, sign)) {
            getSignTag(player).setBoolean(sign.getRegistryName().toString(), true);
            sync(player, MagicKnowledgeSyncPacket.SIGN, sign.getRegistryName(), true);
            notifySign(player, sign);
        }
    }

    public static void removeSign(EntityPlayer player, Sign sign) {
        if (sign != null) {
            getSignTag(player).removeTag(sign.getRegistryName().toString());
            sync(player, MagicKnowledgeSyncPacket.SIGN, sign.getRegistryName(), false);
        }
    }

    public static void setSignKnown(EntityPlayer player, ResourceLocation sign, boolean known) {
        setKnown(player, SIGNS_ROOT, sign, known);
    }

    public static void clearSigns(EntityPlayer player) {
        clearKnown(player, SIGNS_ROOT, MagicKnowledgeSyncPacket.SIGN);
    }

    public static Set<String> getKnownSignIds(EntityPlayer player) {
        return new HashSet<>(getSignTag(player).getKeySet());
    }

    public static boolean knowsFact(EntityPlayer player, ResourceLocation fact) {
        return fact != null && getFactTag(player).getBoolean(fact.toString());
    }

    public static void grantFact(EntityPlayer player, ResourceLocation fact) {
        if (fact != null && !knowsFact(player, fact)) {
            getFactTag(player).setBoolean(fact.toString(), true);
            sync(player, MagicKnowledgeSyncPacket.FACT, fact, true);
            notifyFact(player);
        }
    }

    public static void removeFact(EntityPlayer player, ResourceLocation fact) {
        if (fact != null) {
            getFactTag(player).removeTag(fact.toString());
            sync(player, MagicKnowledgeSyncPacket.FACT, fact, false);
        }
    }

    public static void setFactKnown(EntityPlayer player, ResourceLocation fact, boolean known) {
        setKnown(player, FACTS_ROOT, fact, known);
    }

    public static void clearFacts(EntityPlayer player) {
        clearKnown(player, FACTS_ROOT, MagicKnowledgeSyncPacket.FACT);
    }

    public static Set<String> getKnownFactIds(EntityPlayer player) {
        return new HashSet<>(getFactTag(player).getKeySet());
    }

    public static boolean knowsRune(EntityPlayer player, Rune rune) {
        return rune != null && getRuneTag(player).getBoolean(rune.getRegistryName().toString());
    }

    public static void grantRune(EntityPlayer player, Rune rune) {
        if (rune != null && !knowsRune(player, rune)) {
            getRuneTag(player).setBoolean(rune.getRegistryName().toString(), true);
            sync(player, MagicKnowledgeSyncPacket.RUNE, rune.getRegistryName(), true);
            notifyRune(player, rune);
        }
    }

    public static void removeRune(EntityPlayer player, Rune rune) {
        if (rune != null) {
            getRuneTag(player).removeTag(rune.getRegistryName().toString());
            sync(player, MagicKnowledgeSyncPacket.RUNE, rune.getRegistryName(), false);
        }
    }

    public static void setRuneKnown(EntityPlayer player, ResourceLocation rune, boolean known) {
        setKnown(player, RUNES_ROOT, rune, known);
    }

    public static void clearRunes(EntityPlayer player) {
        clearKnown(player, RUNES_ROOT, MagicKnowledgeSyncPacket.RUNE);
    }

    public static Set<String> getKnownRuneIds(EntityPlayer player) {
        return new HashSet<>(getRuneTag(player).getKeySet());
    }

    public static void copyKnowledge(EntityPlayer from, EntityPlayer to) {
        NBTTagCompound fromPersisted = getPersistedTag(from);
        NBTTagCompound toPersisted = getPersistedTag(to);
        copyKnowledgeRoot(fromPersisted, toPersisted, ROOT);
        copyKnowledgeRoot(fromPersisted, toPersisted, SIGNS_ROOT);
        copyKnowledgeRoot(fromPersisted, toPersisted, FACTS_ROOT);
        copyKnowledgeRoot(fromPersisted, toPersisted, RUNES_ROOT);
    }

    public static void syncAll(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
        ModNetwork.CHANNEL.sendTo(new KnowledgeResetPacket(), mpPlayer);
        for (String id : getKnownResearchIds(player)) {
            ModNetwork.CHANNEL.sendTo(new KnowledgeSyncPacket(new ResourceLocation(id), true), mpPlayer);
        }
        for (String id : getKnownSignIds(player)) {
            ModNetwork.CHANNEL.sendTo(new MagicKnowledgeSyncPacket(MagicKnowledgeSyncPacket.SIGN,
                    new ResourceLocation(id), true), mpPlayer);
        }
        for (String id : getKnownRuneIds(player)) {
            ModNetwork.CHANNEL.sendTo(new MagicKnowledgeSyncPacket(MagicKnowledgeSyncPacket.RUNE,
                    new ResourceLocation(id), true), mpPlayer);
        }
        for (String id : getKnownFactIds(player)) {
            ModNetwork.CHANNEL.sendTo(new MagicKnowledgeSyncPacket(MagicKnowledgeSyncPacket.FACT,
                    new ResourceLocation(id), true), mpPlayer);
        }
    }

    private static void setKnown(EntityPlayer player, String root, ResourceLocation id, boolean known) {
        if (id == null) {
            return;
        }
        NBTTagCompound tag = getKnowledgeTag(player, root);
        if (known) {
            tag.setBoolean(id.toString(), true);
        } else {
            tag.removeTag(id.toString());
        }
    }

    private static void clearKnown(EntityPlayer player, String root, int type) {
        Set<String> known = new HashSet<>(getKnowledgeTag(player, root).getKeySet());
        getPersistedTag(player).removeTag(root);
        for (String id : known) {
            sync(player, type, new ResourceLocation(id), false);
        }
    }

    private static void syncResearch(EntityPlayer player, ResourceLocation id, boolean known) {
        if (player instanceof EntityPlayerMP) {
            ModNetwork.CHANNEL.sendTo(new KnowledgeSyncPacket(id, known), (EntityPlayerMP) player);
        }
    }

    private static void sync(EntityPlayer player, int type, ResourceLocation id, boolean known) {
        if (player instanceof EntityPlayerMP) {
            ModNetwork.CHANNEL.sendTo(new MagicKnowledgeSyncPacket(type, id, known), (EntityPlayerMP) player);
        }
    }

    private static void notifyResearch(EntityPlayer player, ResourceLocation research) {
        notify(player, "eidolon.title.new_research",
                new TextComponentTranslation("research." + research.getNamespace() + "." + research.getPath()));
    }

    private static void notifySign(EntityPlayer player, Sign sign) {
        ResourceLocation id = sign.getRegistryName();
        notify(player, "eidolon.title.new_sign",
                new TextComponentTranslation(id.getNamespace() + ".sign." + id.getPath()));
    }

    private static void notifyRune(EntityPlayer player, Rune rune) {
        ResourceLocation id = rune.getRegistryName();
        notify(player, "eidolon.title.new_rune",
                new TextComponentTranslation(id.getNamespace() + ".rune." + id.getPath()));
    }

    private static void notifyFact(EntityPlayer player) {
        notify(player, "eidolon.title.new_fact");
    }

    private static void notify(EntityPlayer player, String key, Object... args) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendStatusMessage(new TextComponentTranslation(key, args), true);
        }
    }

    private static void copyKnowledgeRoot(NBTTagCompound fromPersisted, NBTTagCompound toPersisted, String key) {
        if (fromPersisted.hasKey(key)) {
            toPersisted.setTag(key, fromPersisted.getCompoundTag(key).copy());
        } else {
            toPersisted.removeTag(key);
        }
    }

    private static NBTTagCompound getResearchTag(EntityPlayer player) {
        return getKnowledgeTag(player, ROOT);
    }

    private static NBTTagCompound getSignTag(EntityPlayer player) {
        return getKnowledgeTag(player, SIGNS_ROOT);
    }

    private static NBTTagCompound getFactTag(EntityPlayer player) {
        return getKnowledgeTag(player, FACTS_ROOT);
    }

    private static NBTTagCompound getRuneTag(EntityPlayer player) {
        return getKnowledgeTag(player, RUNES_ROOT);
    }

    private static NBTTagCompound getKnowledgeTag(EntityPlayer player, String key) {
        NBTTagCompound persisted = getPersistedTag(player);
        if (!persisted.hasKey(key)) {
            persisted.setTag(key, new NBTTagCompound());
        }
        return persisted.getCompoundTag(key);
    }

    private static NBTTagCompound getPersistedTag(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
