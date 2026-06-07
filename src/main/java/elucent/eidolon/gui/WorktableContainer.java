package elucent.eidolon.gui;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.tile.WorktableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WorktableContainer extends Container {
    private final WorktableTileEntity tile;
    private final InventoryCraftResult result = new InventoryCraftResult();

    public WorktableContainer(InventoryPlayer playerInventory, WorktableTileEntity tile) {
        this.tile = tile;
        addSlotToContainer(new WorktableResultSlot(tile, this, result, 0, 163, 58));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlotToContainer(new WorktableSlot(tile, this, col + row * 3, 40 + col * 18, 40 + row * 18));
            }
        }

        addSlotToContainer(new WorktableSlot(tile, this, 9, 58, 18));
        addSlotToContainer(new WorktableSlot(tile, this, 10, 98, 58));
        addSlotToContainer(new WorktableSlot(tile, this, 11, 58, 98));
        addSlotToContainer(new WorktableSlot(tile, this, 12, 18, 58));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 142 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 16 + col * 18, 200));
        }

        updateResult();
    }

    @Override
    public void onCraftMatrixChanged(net.minecraft.inventory.IInventory inventoryIn) {
        updateResult();
    }

    @Override
    public void detectAndSendChanges() {
        updateResult();
        super.detectAndSendChanges();
    }

    void updateResult() {
        result.setInventorySlotContents(0, tile.getCraftingResult());
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.WORKTABLE
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        if (index == 0) {
            updateResult();
        }
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 0) {
                if (!mergeItemStack(stack, 14, 50, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, copy);
            } else if (index >= 14) {
                if (!mergeItemStack(stack, 1, 14, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 14, 50, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, stack);
        }
        return copy;
    }
}
