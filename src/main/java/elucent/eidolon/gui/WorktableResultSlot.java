package elucent.eidolon.gui;

import elucent.eidolon.tile.WorktableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WorktableResultSlot extends Slot {
    private final WorktableTileEntity tile;
    private final WorktableContainer container;

    public WorktableResultSlot(WorktableTileEntity tile, WorktableContainer container, IInventory inventoryIn,
                               int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.tile = tile;
        this.container = container;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return !tile.getCraftingResult().isEmpty();
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack result = tile.getCraftingResult();
        if (result.isEmpty()) {
            putStack(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }
        putStack(result);
        return super.decrStackSize(amount);
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        ItemStack crafted = tile.craft(thePlayer);
        if (!crafted.isEmpty()) {
            onCrafting(crafted);
        }
        ItemStack taken = super.onTake(thePlayer, stack);
        container.updateResult();
        return taken;
    }
}
