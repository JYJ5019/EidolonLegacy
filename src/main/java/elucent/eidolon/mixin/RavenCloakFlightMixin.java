package elucent.eidolon.mixin;

import elucent.eidolon.item.RavenCloakItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class RavenCloakFlightMixin {
    @Inject(method = "isElytraFlying", at = @At("HEAD"), cancellable = true)
    private void eidolon$isElytraFlying(CallbackInfoReturnable<Boolean> callback) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        if (entity instanceof EntityPlayer) {
            ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (RavenCloakItem.isDashing(chest)) {
                callback.setReturnValue(true);
            }
        }
    }
}
