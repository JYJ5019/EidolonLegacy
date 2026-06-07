package elucent.eidolon.gui;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.tile.IncubatorTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class IncubatorContainer extends Container {
    private final IncubatorTileEntity tile;
    private final int[] lastFields = {-1, -1};

    public IncubatorContainer(InventoryPlayer playerInventory, IncubatorTileEntity tile) {
        this.tile = tile;

        addSlotToContainer(new Slot(tile, IncubatorTileEntity.SLOT_INPUT, 44, 35));
        addSlotToContainer(new Slot(tile, IncubatorTileEntity.SLOT_CATALYST, 44, 59));
        addSlotToContainer(new OutputSlot(tile, IncubatorTileEntity.SLOT_OUTPUT, 116, 47));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getProgress() {
        return tile.getField(0);
    }

    public int getMaxProgress() {
        return Math.max(1, tile.getField(1));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < tile.getFieldCount(); i++) {
            int value = tile.getField(i);
            if (lastFields[i] != value) {
                for (IContainerListener listener : listeners) {
                    listener.sendWindowProperty(this, i, value);
                }
                lastFields[i] = value;
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tile.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.INCUBATOR
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index == 2) {
                if (!mergeItemStack(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, copy);
            } else if (index < 2) {
                if (!mergeItemStack(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 2, false)) {
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

    private static class OutputSlot extends Slot {
        private OutputSlot(net.minecraft.inventory.IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
