package elucent.eidolon.gui;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.tile.WoodenBrewingStandTileEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.ForgeEventFactory;

public class WoodenBrewingStandContainer extends Container {
    private final WoodenBrewingStandTileEntity tile;
    private final int[] lastFields = {-1, -1};

    public WoodenBrewingStandContainer(InventoryPlayer playerInventory, WoodenBrewingStandTileEntity tile) {
        this.tile = tile;

        addSlotToContainer(new PotionSlot(tile, WoodenBrewingStandTileEntity.SLOT_BOTTLE_0, 56, 51));
        addSlotToContainer(new PotionSlot(tile, WoodenBrewingStandTileEntity.SLOT_BOTTLE_1, 79, 58));
        addSlotToContainer(new PotionSlot(tile, WoodenBrewingStandTileEntity.SLOT_BOTTLE_2, 102, 51));
        addSlotToContainer(new IngredientSlot(tile, WoodenBrewingStandTileEntity.SLOT_INGREDIENT, 79, 17));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getBrewTime() {
        return tile.getField(0);
    }

    public int getHeat() {
        return tile.getField(1);
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
        return tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.WOODEN_BREWING_STAND
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            if (index < 4) {
                if (!mergeItemStack(stack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (WoodenBrewingStandTileEntity.isBrewingIngredient(stack)) {
                if (!mergeItemStack(stack, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (WoodenBrewingStandTileEntity.isPotionInput(stack)) {
                if (!mergeItemStack(stack, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 4 && index < 31) {
                if (!mergeItemStack(stack, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 31 && index < 40 && !mergeItemStack(stack, 4, 31, false)) {
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

    private static class PotionSlot extends Slot {
        private PotionSlot(net.minecraft.inventory.IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return WoodenBrewingStandTileEntity.isPotionInput(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
            if (thePlayer instanceof EntityPlayerMP) {
                ForgeEventFactory.onPlayerBrewedPotion(thePlayer, stack);
                CriteriaTriggers.BREWED_POTION.trigger((EntityPlayerMP) thePlayer, PotionUtils.getPotionFromItem(stack));
            }
            return super.onTake(thePlayer, stack);
        }
    }

    private static class IngredientSlot extends Slot {
        private IngredientSlot(net.minecraft.inventory.IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return WoodenBrewingStandTileEntity.isBrewingIngredient(stack);
        }
    }
}
