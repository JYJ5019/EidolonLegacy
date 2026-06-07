package elucent.eidolon.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UnholySymbolItem extends Item {
    public UnholySymbolItem() {
        setMaxStackSize(1);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
