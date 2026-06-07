package elucent.eidolon.tile;

import elucent.eidolon.research.Researches;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.research.Research;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ResearchTableTileEntity extends TileEntity implements IInventory, ITickable {
    public static final int SLOT_NOTES = 0;
    public static final int SLOT_SEAL = 1;
    public static final int SLOT_COUNT = 2;
    public static final int RESEARCH_PROGRESS_TICKS = 200;
    public static final int FIELD_PROGRESS = 0;
    public static final int FIELD_RESEARCH_SEED_LOW = 1;
    public static final int FIELD_RESEARCH_SEED_HIGH = 2;
    public static final int LEGACY_RESEARCH_SEED = 1418644859;
    private static final int WORLD_SEED_MULTIPLIER = 978060631;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int progress;
    private int worldSeed = LEGACY_RESEARCH_SEED;
    private boolean worldSeedInitialized;

    @Override
    public void update() {
        if (world == null || world.isRemote || progress <= 0) {
            return;
        }
        ItemStack notes = inventory.get(SLOT_NOTES);
        if (notes.isEmpty() || notes.getItem() != ModItems.RESEARCH_NOTES || !notes.hasTagCompound()) {
            progress = 0;
            markDirty();
            return;
        }

        progress--;
        if (progress == 0) {
            NBTTagCompound tag = notes.getTagCompound();
            Research research = Researches.find(new ResourceLocation(tag.getString("research")));
            if (research != null) {
                int stepsDone = tag.getInteger("stepsDone");
                if (stepsDone < research.getStars()) {
                    tag.setInteger("stepsDone", stepsDone + 1);
                    notes.setTagCompound(tag);
                    inventory.set(SLOT_NOTES, notes);
                }
            }
        }
        markDirty();
    }

    public void startResearchProgress() {
        progress = RESEARCH_PROGRESS_TICKS;
        markDirty();
    }

    public boolean isResearchInProgress() {
        return progress > 0;
    }

    public int getResearchSeed() {
        if (world != null && !world.isRemote) {
            int sourceSeed = getSourceResearchSeed(world.getSeed());
            if (!worldSeedInitialized || worldSeed != sourceSeed) {
                worldSeed = sourceSeed;
                worldSeedInitialized = true;
                markDirty();
            }
        } else if (!worldSeedInitialized) {
            worldSeed = LEGACY_RESEARCH_SEED;
            worldSeedInitialized = true;
        }
        return worldSeed;
    }

    private static int getSourceResearchSeed(long seed) {
        return LEGACY_RESEARCH_SEED + WORLD_SEED_MULTIPLIER * (int) seed;
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
    public ItemStack getStackInSlot(int index) {
        return inventory.get(index);
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
        if (index == SLOT_NOTES) {
            Researches.ensureDefaultResearch(stack);
            if (stack.isEmpty() || stack.getItem() != ModItems.RESEARCH_NOTES) {
                progress = 0;
            }
        }
        inventory.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public String getName() {
        return "container.eidolon.research_table";
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
        if (stack.isEmpty()) {
            return false;
        }
        if (index == SLOT_NOTES) {
            return stack.getItem() == ModItems.RESEARCH_NOTES || stack.getItem() == ModItems.COMPLETED_RESEARCH;
        }
        if (index == SLOT_SEAL) {
            return stack.getItem() == ModItems.ARCANE_SEAL;
        }
        return false;
    }

    @Override
    public int getField(int id) {
        if (id == FIELD_PROGRESS) {
            return progress;
        }
        if (id == FIELD_RESEARCH_SEED_LOW) {
            return getResearchSeed() & 0xffff;
        }
        if (id == FIELD_RESEARCH_SEED_HIGH) {
            return (getResearchSeed() >>> 16) & 0xffff;
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == FIELD_PROGRESS) {
            progress = value;
        } else if (id == FIELD_RESEARCH_SEED_LOW) {
            worldSeed = (worldSeed & 0xffff0000) | (value & 0xffff);
            worldSeedInitialized = true;
        } else if (id == FIELD_RESEARCH_SEED_HIGH) {
            worldSeed = (worldSeed & 0x0000ffff) | ((value & 0xffff) << 16);
            worldSeedInitialized = true;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
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
        compound.setInteger("progress", progress);
        compound.setInteger("worldSeed", getResearchSeed());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        for (int i = 0; i < inventory.size(); i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        ItemStackHelper.loadAllItems(compound, inventory);
        progress = compound.getInteger("progress");
        if (compound.hasKey("worldSeed")) {
            worldSeed = compound.getInteger("worldSeed");
            worldSeedInitialized = true;
        } else {
            worldSeed = LEGACY_RESEARCH_SEED;
            worldSeedInitialized = false;
        }
    }
}
