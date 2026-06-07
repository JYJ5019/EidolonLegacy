package elucent.eidolon.item.curio;

import baubles.api.BaubleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class VoidAmuletItem extends EidolonBaubleItem {
    public static final int MAX_COOLDOWN = 200;
    private static final String TAG_COOLDOWN = "cooldown";

    public VoidAmuletItem() {
        super(BaubleType.AMULET, "tooltip.eidolon.void_amulet");
    }

    public static int getCooldown(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0 : tag.getInteger(TAG_COOLDOWN);
    }

    public static void setCooldown(ItemStack stack, int cooldown) {
        stack.setTagInfo(TAG_COOLDOWN, new net.minecraft.nbt.NBTTagInt(MathHelper.clamp(cooldown, 0, MAX_COOLDOWN)));
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entity) {
        if (!entity.world.isRemote && getCooldown(stack) > 0) {
            setCooldown(stack, getCooldown(stack) - 1);
        }
    }

    @Override
    public boolean willAutoSync(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (getCooldown(stack) > 0) {
            tooltip.add(TextFormatting.DARK_GRAY + String.format(translate("tooltip.eidolon.void_amulet_cooldown"),
                    getCooldown(stack) / 20 + 1));
        }
    }
}
