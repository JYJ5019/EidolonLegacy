package elucent.eidolon.tile;

import elucent.eidolon.recipes.IncubatorRecipe;
import elucent.eidolon.recipes.IncubatorRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

public class IncubatorTileEntity extends MachineInventoryTileEntity implements ITickable {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_CATALYST = 1;
    public static final int SLOT_OUTPUT = 2;

    private int progress;
    private int maxProgress = IncubatorRecipe.DEFAULT_TICKS;
    private ResourceLocation activeRecipeId;

    public IncubatorTileEntity() {
        super("container.eidolon.incubator", 3);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        IncubatorRecipe recipe = IncubatorRecipes.find(inventory.get(SLOT_INPUT), inventory.get(SLOT_CATALYST));
        if (recipe == null || !canOutput(recipe.getResult())) {
            resetProgress();
            return;
        }
        if (activeRecipeId == null || !activeRecipeId.equals(recipe.getId())) {
            activeRecipeId = recipe.getId();
            progress = 0;
            maxProgress = recipe.getTicks();
        }
        maxProgress = recipe.getTicks();
        progress++;
        if (progress >= maxProgress) {
            craft(recipe);
            resetProgress();
        }
        markDirty();
    }

    private void craft(IncubatorRecipe recipe) {
        ItemStack result = recipe.getResult();
        inventory.get(SLOT_INPUT).shrink(1);
        inventory.get(SLOT_CATALYST).shrink(1);
        if (inventory.get(SLOT_INPUT).isEmpty()) {
            inventory.set(SLOT_INPUT, ItemStack.EMPTY);
        }
        if (inventory.get(SLOT_CATALYST).isEmpty()) {
            inventory.set(SLOT_CATALYST, ItemStack.EMPTY);
        }
        ItemStack output = inventory.get(SLOT_OUTPUT);
        if (output.isEmpty()) {
            inventory.set(SLOT_OUTPUT, result.copy());
        } else {
            output.grow(result.getCount());
        }
        onInventoryChanged();
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = inventory.get(SLOT_OUTPUT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.areItemsEqual(output, result)
                && ItemStack.areItemStackTagsEqual(output, result)
                && output.getCount() + result.getCount() <= Math.min(getInventoryStackLimit(), output.getMaxStackSize());
    }

    private void resetProgress() {
        if (progress != 0 || activeRecipeId != null) {
            progress = 0;
            maxProgress = IncubatorRecipe.DEFAULT_TICKS;
            activeRecipeId = null;
            markDirty();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == SLOT_INPUT || index == SLOT_CATALYST;
    }

    @Override
    public int getField(int id) {
        if (id == 0) {
            return progress;
        }
        if (id == 1) {
            return maxProgress;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) {
            progress = value;
        } else if (id == 1) {
            maxProgress = Math.max(1, value);
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    protected void writeMachineNBT(NBTTagCompound compound) {
        compound.setInteger("Progress", progress);
        compound.setInteger("MaxProgress", maxProgress);
        if (activeRecipeId != null) {
            compound.setString("ActiveRecipe", activeRecipeId.toString());
        }
    }

    @Override
    protected void readMachineNBT(NBTTagCompound compound) {
        progress = compound.getInteger("Progress");
        maxProgress = compound.hasKey("MaxProgress")
                ? Math.max(1, compound.getInteger("MaxProgress")) : IncubatorRecipe.DEFAULT_TICKS;
        activeRecipeId = compound.hasKey("ActiveRecipe") ? new ResourceLocation(compound.getString("ActiveRecipe")) : null;
    }
}
