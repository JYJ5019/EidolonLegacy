package elucent.eidolon.item;

import elucent.eidolon.Reference;
import elucent.eidolon.client.render.RavenCloakModel;
import elucent.eidolon.network.RavenCloakPacket;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class RavenCloakItem extends EidolonCurioArmorItem {
    public static final int MAX_CHARGES = 12;
    public static final int MAX_DASH_TICKS = 100;
    private static final double SLOW_FALL_GRAVITY_COMPENSATION = 0.047D;

    private static final String DASH_TICKS = "EidolonRavenDashTicks";
    private static final String FLAP_CHARGES = "EidolonRavenFlapCharges";
    private static final String FLYING = "EidolonRavenFlying";

    private static SimpleNetworkWrapper syncChannel;

    @SideOnly(Side.CLIENT)
    private RavenCloakModel armorModel;

    public RavenCloakItem(ArmorMaterial material) {
        super(material, EntityEquipmentSlot.CHEST, "raven", "tooltip.eidolon.raven_cloak");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Reference.MOD_ID + ":textures/entity/raven_cloak.png";
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot armorSlot,
                                    ModelBiped defaultModel) {
        if (armorSlot != EntityEquipmentSlot.CHEST) {
            return defaultModel;
        }
        if (armorModel == null) {
            armorModel = new RavenCloakModel();
        }
        setModelState(armorModel, entity, stack);
        return armorModel;
    }

    @SideOnly(Side.CLIENT)
    private static void setModelState(RavenCloakModel model, EntityLivingBase entity, ItemStack stack) {
        elucent.eidolon.client.render.RavenCloakRenderState.Snapshot state =
                elucent.eidolon.client.render.RavenCloakRenderState.get(entity, isFlying(stack),
                        getDashTicks(stack), getFlapCharges(stack));
        model.setState(state.flying, state.dashTicks, state.flapCharges, state.flapAge);
    }

    public static void setSyncChannel(SimpleNetworkWrapper channel) {
        syncChannel = channel;
    }

    public static void tickWings(EntityPlayer player, ItemStack stack) {
        boolean clampedDescent = false;
        boolean wasFlying = isFlying(stack);
        boolean wasDashing = getDashTicks(stack) > 0;
        int oldCharges = getFlapCharges(stack);
        if (player.isSneaking() && player.motionY < -0.10D) {
            setFlying(stack, true);
            player.motionY = -0.10D;
            player.velocityChanged = true;
            clampedDescent = true;
        }
        if (isFlying(stack)) {
            if (!clampedDescent && player.motionY < 0.0D) {
                player.motionY = Math.min(0.0D, player.motionY + SLOW_FALL_GRAVITY_COMPENSATION);
                player.velocityChanged = true;
            }
            player.fallDistance = 0.0F;
        }
        tickDash(player, stack);
        if (player.onGround) {
            setFlapCharges(stack, MAX_CHARGES);
            stopFlying(stack);
        }
        syncStateIfChanged(player, stack, false, wasFlying, wasDashing, oldCharges);
    }

    public static boolean tryFlap(EntityPlayer player, ItemStack stack) {
        if (getFlapCharges(stack) <= 0) {
            setFlapCharges(stack, 0);
            return false;
        }
        if (!canFlap(player)) {
            return false;
        }
        Vec3d look = player.getLookVec();
        if (getDashTicks(stack) > 0) {
            setDashTicks(stack, MAX_DASH_TICKS);
        } else {
            player.motionX += look.x * 0.25D;
            player.motionZ += look.z * 0.25D;
            player.motionY = 0.5D;
        }
        setFlapCharges(stack, getFlapCharges(stack) - 1);
        setFlying(stack, true);
        player.fallDistance = 0.0F;
        player.velocityChanged = true;
        syncState(player, stack, true);
        return true;
    }

    public static boolean tryStartDash(EntityPlayer player, ItemStack stack) {
        if (getFlapCharges(stack) <= 0) {
            setFlapCharges(stack, 0);
            return false;
        }
        if (!canFlap(player)) {
            return false;
        }
        setFlapCharges(stack, getFlapCharges(stack) - 1);
        setDashTicks(stack, MAX_DASH_TICKS);
        setFlying(stack, true);
        player.fallDistance = 0.0F;
        player.velocityChanged = true;
        syncState(player, stack, false);
        return true;
    }

    public static int getDashTicks(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger(DASH_TICKS);
    }

    public static boolean isDashing(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof RavenCloakItem && getDashTicks(stack) > 0;
    }

    public static boolean isFlying(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.getBoolean(FLYING);
    }

    public static int getFlapCharges(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null || !tag.hasKey(FLAP_CHARGES) ? 0 : tag.getInteger(FLAP_CHARGES);
    }

    private static void tickDash(EntityPlayer player, ItemStack stack) {
        int ticks = getDashTicks(stack);
        if (ticks <= 0) {
            return;
        }
        Vec3d look = player.getLookVec();
        float coeff = ticks / (float) MAX_DASH_TICKS;
        coeff = 1.0F - (1.0F - coeff) * (1.0F - coeff) + 0.25F;
        player.motionX = player.motionX * 0.8D + look.x * coeff * 0.2D;
        player.motionY = player.motionY * 0.8D + look.y * coeff * 0.2D;
        player.motionZ = player.motionZ * 0.8D + look.z * coeff * 0.2D;
        player.fallDistance = 0.0F;
        player.velocityChanged = true;
        setDashTicks(stack, ticks - 1);
    }

    public static boolean canFlap(EntityPlayer player) {
        return !player.onGround && !player.isInWater() && !player.isRiding() && !player.capabilities.isFlying;
    }

    private static void setDashTicks(ItemStack stack, int ticks) {
        getOrCreateTag(stack).setInteger(DASH_TICKS, Math.max(0, Math.min(MAX_DASH_TICKS, ticks)));
    }

    private static void setFlapCharges(ItemStack stack, int charges) {
        getOrCreateTag(stack).setInteger(FLAP_CHARGES, Math.max(0, Math.min(MAX_CHARGES, charges)));
    }

    private static void setFlying(ItemStack stack, boolean flying) {
        getOrCreateTag(stack).setBoolean(FLYING, flying);
    }

    private static void stopFlying(ItemStack stack) {
        if (isFlying(stack)) {
            setFlying(stack, false);
            if (getDashTicks(stack) > 0) {
                setDashTicks(stack, 0);
            }
        }
    }

    private static void syncStateIfChanged(EntityPlayer player, ItemStack stack, boolean flapped,
                                           boolean wasFlying, boolean wasDashing, int oldCharges) {
        boolean flying = isFlying(stack);
        boolean dashing = getDashTicks(stack) > 0;
        int charges = getFlapCharges(stack);
        if (flapped || flying != wasFlying || dashing != wasDashing || charges != oldCharges || shouldRefreshSync(player)) {
            syncState(player, stack, flapped);
        }
    }

    private static boolean shouldRefreshSync(EntityPlayer player) {
        return !player.world.isRemote && player.ticksExisted % 20 == 0;
    }

    private static void syncState(EntityPlayer player, ItemStack stack, boolean flapped) {
        if (player.world.isRemote || !(player instanceof EntityPlayerMP) || syncChannel == null) {
            return;
        }
        syncChannel.sendToAllTracking(RavenCloakPacket.sync(player, stack, flapped), player);
        syncChannel.sendTo(RavenCloakPacket.sync(player, stack, flapped), (EntityPlayerMP)player);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
    }
}
