package elucent.eidolon.tile;

import elucent.eidolon.recipes.WorktableRecipe;
import elucent.eidolon.recipes.WorktableRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.tileentity.TileEntity;

public class WorktableTileEntity extends TileEntity implements IInventory {
    public static final int GRID_SIZE = 9;
    public static final int REAGENT_SIZE = 4;
    public static final int SLOT_COUNT = GRID_SIZE + REAGENT_SIZE;
    private static final Container CRAFTING_CONTAINER = new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }
    };

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.get(MathHelper.clamp(slot, 0, inventory.size() - 1));
    }

    public boolean addOne(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                ItemStack stored = stack.splitStack(1);
                inventory.set(i, stored);
                markDirty();
                return true;
            }
        }
        return false;
    }

    public ItemStack removeLast() {
        for (int i = inventory.size() - 1; i >= 0; i--) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                inventory.set(i, ItemStack.EMPTY);
                markDirty();
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public WorktableRecipe getMatchingRecipe() {
        return WorktableRecipes.findMatch(getGridStacks(), getReagentStacks());
    }

    public ItemStack getCraftingResult() {
        WorktableRecipe recipe = getMatchingRecipe();
        if (recipe != null) {
            return recipe.getResult();
        }
        IRecipe vanillaRecipe = getMatchingVanillaRecipe();
        return vanillaRecipe == null ? ItemStack.EMPTY : vanillaRecipe.getCraftingResult(createCraftingInventory()).copy();
    }

    public ItemStack craft(EntityPlayer player) {
        WorktableRecipe recipe = getMatchingRecipe();
        if (recipe != null) {
            ItemStack result = recipe.getResult();
            consumeInputs(SLOT_COUNT, getContainerItems(SLOT_COUNT), player);
            markDirty();
            return result;
        }

        IRecipe vanillaRecipe = getMatchingVanillaRecipe();
        if (vanillaRecipe != null) {
            InventoryCrafting craftingInventory = createCraftingInventory();
            ItemStack result = vanillaRecipe.getCraftingResult(craftingInventory).copy();
            consumeInputs(GRID_SIZE, vanillaRecipe.getRemainingItems(craftingInventory), player);
            markDirty();
            return result;
        }

        return ItemStack.EMPTY;
    }

    private IRecipe getMatchingVanillaRecipe() {
        if (world == null) {
            return null;
        }
        return CraftingManager.findMatchingRecipe(createCraftingInventory(), world);
    }

    private InventoryCrafting createCraftingInventory() {
        InventoryCrafting craftingInventory = new InventoryCrafting(CRAFTING_CONTAINER, 3, 3);
        for (int i = 0; i < GRID_SIZE; i++) {
            craftingInventory.setInventorySlotContents(i, inventory.get(i).copy());
        }
        return craftingInventory;
    }

    public ItemStack[] getGridStacks() {
        ItemStack[] stacks = new ItemStack[GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            stacks[i] = inventory.get(i);
        }
        return stacks;
    }

    public ItemStack[] getReagentStacks() {
        ItemStack[] stacks = new ItemStack[REAGENT_SIZE];
        for (int i = 0; i < REAGENT_SIZE; i++) {
            stacks[i] = inventory.get(GRID_SIZE + i);
        }
        return stacks;
    }

    private NonNullList<ItemStack> getContainerItems(int slotCount) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.getItem().hasContainerItem(stack)) {
                remaining.set(i, stack.getItem().getContainerItem(stack));
            }
        }
        return remaining;
    }

    private void consumeInputs(int slotCount, NonNullList<ItemStack> remainingItems, EntityPlayer player) {
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    inventory.set(i, ItemStack.EMPTY);
                }
            }
            ItemStack remaining = i < remainingItems.size() ? remainingItems.get(i).copy() : ItemStack.EMPTY;
            if (!remaining.isEmpty()) {
                putRemainingItem(i, remaining, player);
            }
        }
        markDirty();
    }

    private void putRemainingItem(int slot, ItemStack remaining, EntityPlayer player) {
        ItemStack current = inventory.get(slot);
        if (current.isEmpty()) {
            inventory.set(slot, remaining);
            return;
        }
        if (ItemStack.areItemsEqual(current, remaining) && ItemStack.areItemStackTagsEqual(current, remaining)) {
            current.grow(remaining.getCount());
            return;
        }
        if (player != null && player.inventory.addItemStackToInventory(remaining)) {
            return;
        }
        if (player != null) {
            player.dropItem(remaining, false);
        }
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStackHelper.getAndSplit(inventory, index, count);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(inventory, index);
        if (!stack.isEmpty()) {
            markDirty();
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public String getName() {
        return "container.eidolon.worktable";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return world != null && world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, inventory);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        ItemStackHelper.loadAllItems(compound, inventory);
    }
}
