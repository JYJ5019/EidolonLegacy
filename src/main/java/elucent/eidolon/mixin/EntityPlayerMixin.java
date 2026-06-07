package elucent.eidolon.mixin;

import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {
    @Redirect(
            method = "attackTargetEntityWithCurrentItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0)
    )
    private Item eidolon$disableDeathbringerSweep(ItemStack stack) {
        return stack.getItem() == ModItems.DEATHBRINGER_SCYTHE ? Items.AIR : stack.getItem();
    }
}
