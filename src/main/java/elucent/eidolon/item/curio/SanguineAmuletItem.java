package elucent.eidolon.item.curio;

import baubles.api.BaubleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SanguineAmuletItem extends EidolonBaubleItem {
    public static final int MAX_CHARGE = 40;
    private static final String TAG_CHARGE = "charge";

    public SanguineAmuletItem() {
        super(BaubleType.AMULET, "tooltip.eidolon.sanguine_amulet");
    }

    public static int getCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger(TAG_CHARGE);
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.setTagInfo(TAG_CHARGE, new net.minecraft.nbt.NBTTagInt(MathHelper.clamp(charge, 0, MAX_CHARGE)));
    }

    public static void addCharge(ItemStack stack, int charge) {
        setCharge(stack, getCharge(stack) + charge);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (entity.world.isRemote) {
            return;
        }
        if (entity.ticksExisted % 80 == 0
                && entity instanceof EntityPlayer
                && entity.getHealth() >= entity.getMaxHealth() - 0.001F
                && getCharge(stack) < MAX_CHARGE) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getFoodStats().getFoodLevel() >= 18) {
                float exhaustion = player.getFoodStats().getSaturationLevel() > 0.0F
                        ? Math.min(4.0F * player.getFoodStats().getSaturationLevel(), 16.0F)
                        : 4.0F;
                player.getFoodStats().addExhaustion(exhaustion);
                addCharge(stack, 1);
            }
        }
        if (entity.ticksExisted % 10 == 0 && getCharge(stack) > 0 && entity.getHealth() < entity.getMaxHealth()) {
            int taken = (int) Math.min(1.0F, entity.getMaxHealth() - entity.getHealth());
            if (taken > 0) {
                entity.heal(taken);
                addCharge(stack, -taken);
            }
        }
    }

    @Override
    public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.RED + String.format(translate("tooltip.eidolon.sanguine_amulet_charge"),
                getCharge(stack), MAX_CHARGE));
    }
}
