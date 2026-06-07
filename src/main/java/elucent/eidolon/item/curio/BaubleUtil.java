package elucent.eidolon.item.curio;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class BaubleUtil {
    private BaubleUtil() {
    }

    public static boolean hasBauble(EntityLivingBase entity, Item item) {
        return !findBauble(entity, item).isEmpty();
    }

    public static ItemStack findBauble(EntityLivingBase entity, Item item) {
        if (entity == null || item == null) {
            return ItemStack.EMPTY;
        }
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack != null && !stack.isEmpty() && stack.getItem() == item) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
