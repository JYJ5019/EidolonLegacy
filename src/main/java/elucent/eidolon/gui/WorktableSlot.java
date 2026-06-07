package elucent.eidolon.gui;

import elucent.eidolon.tile.WorktableTileEntity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WorktableSlot extends Slot {
    private final WorktableTileEntity tile;
    private final Container container;

    public WorktableSlot(WorktableTileEntity tile, Container container, int index, int xPosition, int yPosition) {
        super(tile, index, xPosition, yPosition);
        this.tile = tile;
        this.container = container;
    }

    @Override
    public void putStack(ItemStack stack) {
        super.putStack(stack);
        notifyChanged();
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        notifyChanged();
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack stack = super.decrStackSize(amount);
        notifyChanged();
        return stack;
    }

    private void notifyChanged() {
        tile.markDirty();
        container.onCraftMatrixChanged(tile);
    }
}
