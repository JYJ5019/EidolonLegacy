package elucent.eidolon.mixin;

import elucent.eidolon.registries.ModItems;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSoulSand.class)
public abstract class BlockSoulSandMixin {
    @Inject(method = "onEntityCollidedWithBlock", at = @At("TAIL"))
    private void eidolon$reduceWarlockSoulSandSlowdown(World worldIn, BlockPos pos, IBlockState state,
                                                       Entity entityIn, CallbackInfo callback) {
        if (hasWarlockBoots(entityIn)) {
            entityIn.motionX *= 1.75D;
            entityIn.motionZ *= 1.75D;
        }
    }

    private static boolean hasWarlockBoots(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }
        ItemStack boots = ((EntityLivingBase) entity).getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return !boots.isEmpty() && boots.getItem() == ModItems.WARLOCK_BOOTS;
    }
}
