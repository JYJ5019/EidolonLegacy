package elucent.eidolon.tile;

import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.oredict.OreDictionary;

public class WoodenBrewingStandTileEntity extends MachineInventoryTileEntity implements ITickable, ISidedInventory {
    public static final int SLOT_BOTTLE_0 = 0;
    public static final int SLOT_BOTTLE_1 = 1;
    public static final int SLOT_BOTTLE_2 = 2;
    public static final int SLOT_INGREDIENT = 3;
    public static final int BREW_TIME_TOTAL = 800;
    private static final int[] SLOTS_FOR_UP = new int[] {SLOT_INGREDIENT};
    private static final int[] SLOTS_FOR_DOWN = new int[] {SLOT_BOTTLE_0, SLOT_BOTTLE_1, SLOT_BOTTLE_2, SLOT_INGREDIENT};
    private static final int[] OUTPUT_SLOTS = new int[] {SLOT_BOTTLE_0, SLOT_BOTTLE_1, SLOT_BOTTLE_2};

    private int brewTime;
    private int heat;
    private Item brewingIngredient = null;

    public WoodenBrewingStandTileEntity() {
        super("container.eidolon.wooden_brewing_stand", 4);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }
        if (world.getTotalWorldTime() % 20L == 0L) {
            int previousHeat = heat;
            heat = hasHeatSource() ? 1 : 0;
            if (previousHeat != heat) {
                markDirty();
            }
        }

        boolean canBrew = canBrew();
        if (brewTime > 0) {
            brewTime--;
            if (!canBrew || heat <= 0 || brewingIngredient != inventory.get(SLOT_INGREDIENT).getItem()) {
                brewTime = 0;
                brewingIngredient = null;
            } else if (brewTime == 0) {
                brewPotions();
                brewingIngredient = null;
            }
            markDirty();
        } else if (canBrew && heat > 0) {
            brewTime = BREW_TIME_TOTAL;
            brewingIngredient = inventory.get(SLOT_INGREDIENT).getItem();
            markDirty();
        }
    }

    public boolean hasBottle(int slot) {
        return slot >= SLOT_BOTTLE_0 && slot <= SLOT_BOTTLE_2 && !inventory.get(slot).isEmpty();
    }

    private boolean canBrew() {
        ItemStack ingredient = inventory.get(SLOT_INGREDIENT);
        if (ingredient.isEmpty() || !isBrewingIngredient(ingredient)) {
            return false;
        }
        return BrewingRecipeRegistry.canBrew(inventory, ingredient,
                new int[]{SLOT_BOTTLE_0, SLOT_BOTTLE_1, SLOT_BOTTLE_2});
    }

    private void brewPotions() {
        if (ForgeEventFactory.onPotionAttemptBrew(inventory)) {
            return;
        }
        ItemStack ingredient = inventory.get(SLOT_INGREDIENT);
        if (ingredient.isEmpty()) {
            return;
        }
        BrewingRecipeRegistry.brewPotions(inventory, ingredient,
                new int[]{SLOT_BOTTLE_0, SLOT_BOTTLE_1, SLOT_BOTTLE_2});
        ForgeEventFactory.onPotionBrewed(inventory);

        if (ingredient.getItem().hasContainerItem(ingredient)) {
            ItemStack container = ingredient.getItem().getContainerItem(ingredient);
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                inventory.set(SLOT_INGREDIENT, container);
            } else if (!container.isEmpty()) {
                dropStack(container);
            }
        } else {
            ingredient.shrink(1);
            if (ingredient.isEmpty()) {
                inventory.set(SLOT_INGREDIENT, ItemStack.EMPTY);
            }
        }
        if (world != null) {
            world.playEvent(1035, pos, 0);
        }
        onInventoryChanged();
    }

    private void dropStack(ItemStack stack) {
        if (world != null && !world.isRemote && !stack.isEmpty()) {
            world.spawnEntity(new net.minecraft.entity.item.EntityItem(world,
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
        }
    }

    private boolean hasHeatSource() {
        BlockPos below = pos.down();
        TileEntity tile = world.getTileEntity(below);
        return tile instanceof CrucibleTileEntity && ((CrucibleTileEntity) tile).isBoiling();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= SLOT_BOTTLE_0 && index <= SLOT_BOTTLE_2) {
            return isPotionInput(stack);
        }
        return index == SLOT_INGREDIENT && isBrewingIngredient(stack);
    }

    public static boolean isPotionInput(ItemStack stack) {
        return !stack.isEmpty() && BrewingRecipeRegistry.isValidInput(stack);
    }

    public static boolean isBrewingIngredient(ItemStack stack) {
        return !stack.isEmpty()
                && !isExcludedDust(stack)
                && BrewingRecipeRegistry.isValidIngredient(stack);
    }

    private static boolean isExcludedDust(ItemStack stack) {
        if (stack.getItem() == Items.REDSTONE || stack.getItem() == Items.GLOWSTONE_DUST) {
            return true;
        }
        for (int id : OreDictionary.getOreIDs(stack)) {
            String name = OreDictionary.getOreName(id);
            if ("dustRedstone".equals(name) || "dustGlowstone".equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getField(int id) {
        if (id == 0) {
            return brewTime;
        }
        if (id == 1) {
            return heat;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == 0) {
            brewTime = value;
        } else if (id == 1) {
            heat = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    protected void writeMachineNBT(NBTTagCompound compound) {
        compound.setInteger("BrewTime", brewTime);
    }

    @Override
    protected void readMachineNBT(NBTTagCompound compound) {
        brewTime = compound.getInteger("BrewTime");
        heat = 0;
        brewingIngredient = inventory.get(SLOT_INGREDIENT).isEmpty() ? null : inventory.get(SLOT_INGREDIENT).getItem();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP) {
            return SLOTS_FOR_UP;
        }
        return side == EnumFacing.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (index >= SLOT_BOTTLE_0 && index <= SLOT_BOTTLE_2) {
            return isPotionInput(itemStackIn) && inventory.get(index).isEmpty();
        }
        return index == SLOT_INGREDIENT && isBrewingIngredient(itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != SLOT_INGREDIENT || stack.getItem() == Items.GLASS_BOTTLE;
    }
}
