package elucent.eidolon.mixin;

import elucent.eidolon.Eidolon;
import elucent.eidolon.registries.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @Inject(method = "getCreatureAttribute", at = @At("HEAD"), cancellable = true)
    private void eidolon$getCreatureAttribute(CallbackInfoReturnable<EnumCreatureAttribute> callback) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;
        if (!Eidolon.trueCreatureAttribute && entity.isPotionActive(ModPotions.UNDEATH)) {
            callback.setReturnValue(EnumCreatureAttribute.UNDEAD);
        }
    }
}
