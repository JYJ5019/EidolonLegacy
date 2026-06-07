package elucent.eidolon.tile;

import net.minecraft.item.ItemStack;

public class OffertoryPlateTileEntity extends ItemHolderTileEntity {
    @Override
    public ItemStack provide() {
        return ItemStack.EMPTY;
    }

    @Override
    public void take() {
    }
}
