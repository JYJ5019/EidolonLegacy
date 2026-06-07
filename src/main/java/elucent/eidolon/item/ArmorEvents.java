package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.capability.SoulData;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SoulSyncPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ArmorEvents {
    private static final String ROOT = "eidolonBonelordArmor";
    private static final String LAST_BONUS = "lastBonus";

    private ArmorEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!chest.isEmpty() && chest.getItem() instanceof RavenCloakItem) {
            RavenCloakItem.tickWings(player, chest);
        }
        updateBonelordSoul(player);
    }

    private static void updateBonelordSoul(EntityPlayer player) {
        float persistent = getBonelordPersistentHealth(player);
        SoulData.ensureDefaults(player);
        float lastBonus = getLastBonelordBonus(player);
        if (persistent != lastBonus) {
            float newMax = Math.max(0.0F, SoulData.getMaxEtherealHealth(player) - lastBonus + persistent);
            SoulData.setMaxEtherealHealth(player, newMax);
            setLastBonelordBonus(player, persistent);
            syncSoul(player);
        }
    }

    private static float getBonelordPersistentHealth(EntityPlayer player) {
        float persistent = 0.0F;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (!stack.isEmpty() && stack.getItem() instanceof BonelordArmorItem) {
                persistent += ((BonelordArmorItem) stack.getItem()).getPersistentEtherealHealth();
            }
        }
        return persistent;
    }

    private static float getLastBonelordBonus(EntityPlayer player) {
        return getArmorTag(player).getFloat(LAST_BONUS);
    }

    private static void setLastBonelordBonus(EntityPlayer player, float bonus) {
        getArmorTag(player).setFloat(LAST_BONUS, bonus);
    }

    private static net.minecraft.nbt.NBTTagCompound getArmorTag(EntityPlayer player) {
        net.minecraft.nbt.NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new net.minecraft.nbt.NBTTagCompound());
        }
        net.minecraft.nbt.NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(ROOT)) {
            persisted.setTag(ROOT, new net.minecraft.nbt.NBTTagCompound());
        }
        return persisted.getCompoundTag(ROOT);
    }

    private static void syncSoul(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ModNetwork.CHANNEL.sendTo(new SoulSyncPacket(player), (EntityPlayerMP) player);
        }
    }
}
