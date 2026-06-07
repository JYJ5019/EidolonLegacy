package elucent.eidolon.mixin;

import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.25D), require = 2)
    private double eidolon$reduceWarlockWebHorizontalSlowdown(double multiplier) {
        return hasWarlockBoots() ? reduceSlowdown(multiplier) : multiplier;
    }

    @ModifyConstant(method = "move", constant = @Constant(doubleValue = 0.05000000074505806D), require = 1)
    private double eidolon$reduceWarlockWebVerticalSlowdown(double multiplier) {
        return hasWarlockBoots() ? reduceSlowdown(multiplier) : multiplier;
    }

    private double reduceSlowdown(double multiplier) {
        return 1.0D - (1.0D - multiplier) * 0.5D;
    }

    private boolean hasWarlockBoots() {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }
        ItemStack boots = ((EntityLivingBase) entity).getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return !boots.isEmpty() && boots.getItem() == ModItems.WARLOCK_BOOTS;
    }
}
