package elucent.eidolon.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Enchantments;
import net.minecraft.world.World;

import java.util.List;

public class WandItem extends Item implements IRechargeableWand {
    private static final String CHARGE_TAG = "EidolonCharge";
    private static final int MAX_CHARGE = 253;

    public WandItem() {
        setMaxStackSize(1);
        setMaxDamage(MAX_CHARGE);
        setNoRepair();
    }

    @Override
    public int getItemEnchantability() {
        return 20;
    }

    @Override
    public ItemStack recharge(ItemStack stack) {
        ItemStack charged = stack.copy();
        migrateLegacyCharge(charged);
        charged.setItemDamage(0);
        return charged;
    }

    public int getCharge(ItemStack stack) {
        migrateLegacyCharge(stack);
        return Math.max(0, Math.min(MAX_CHARGE, MAX_CHARGE - stack.getItemDamage()));
    }

    public int getMaxCharge() {
        return MAX_CHARGE;
    }

    public boolean consumeCharge(ItemStack stack, int amount) {
        return getCharge(stack) >= amount;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.eidolon.wand_charge", getCharge(stack), getMaxCharge()));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment)
                || enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING;
    }

    public void damageWand(ItemStack stack, int amount, net.minecraft.entity.EntityLivingBase entity) {
        if (consumeCharge(stack, amount)) {
            stack.damageItem(amount, entity);
        }
    }

    private void migrateLegacyCharge(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(CHARGE_TAG)) {
            int charge = Math.max(0, Math.min(MAX_CHARGE, tag.getInteger(CHARGE_TAG)));
            stack.setItemDamage(MAX_CHARGE - charge);
            tag.removeTag(CHARGE_TAG);
            if (tag.getSize() == 0) {
                stack.setTagCompound(null);
            }
        }
    }
}
