package elucent.eidolon.item;

import elucent.eidolon.entity.SoulfireProjectileEntity;
import elucent.eidolon.registries.ModSounds;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class SoulfireWandItem extends WandItem {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (getCharge(stack) <= 0) {
            if (!worldIn.isRemote) {
                playerIn.sendStatusMessage(new TextComponentTranslation("message.eidolon.wand_no_charge"), true);
            }
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!worldIn.isRemote) {
            Vec3d look = playerIn.getLookVec();
            Vec3d pos = playerIn.getPositionVector()
                    .add(look.scale(0.5D))
                    .add(0.5D * Math.sin(Math.toRadians(225.0D - playerIn.rotationYawHead)),
                            playerIn.height * 2.0D / 3.0D,
                            0.5D * Math.cos(Math.toRadians(225.0D - playerIn.rotationYawHead)));
            Vec3d vel = playerIn.getPositionEyes(0.0F).add(look.scale(40.0D)).subtract(pos).scale(1.0D / 20.0D);
            SoulfireProjectileEntity projectile = new SoulfireProjectileEntity(worldIn, playerIn);
            projectile.setPosition(pos.x, pos.y, pos.z);
            projectile.shoot(vel.x, vel.y, vel.z, 1.0F, 0.0F);
            worldIn.spawnEntity(projectile);
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                    ModSounds.CAST_SOULFIRE, SoundCategory.PLAYERS, 0.75F, itemRand.nextFloat() * 0.2F + 0.9F);
            damageWand(stack, 1, playerIn);
        }
        playerIn.swingArm(handIn);
        playerIn.getCooldownTracker().setCooldown(this, 10);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.format("tooltip.eidolon.soulfire_wand_use"));
    }
}
