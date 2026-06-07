package elucent.eidolon.gui;

import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.research.Research;
import elucent.eidolon.research.ResearchTask;
import elucent.eidolon.research.Researches;
import elucent.eidolon.tile.ResearchTableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.ArrayList;

public class ResearchTableContainer extends Container {
    private static final int TASK_X = 189;
    private static final int TASK_Y = 17;
    private static final int TASK_SPACING = 36;
    private static final int TASK_SLOT_COUNT = 9;

    private final ResearchTableTileEntity tile;
    private final EntityPlayer player;
    private final InventoryBasic taskInventory = new InventoryBasic("research_tasks", false, 9);
    private final ResearchTaskSlot[] taskSlots = new ResearchTaskSlot[TASK_SLOT_COUNT];
    private String lastResearchKey = "";
    private int lastProgress = -1;
    private int lastSeedLow = -1;
    private int lastSeedHigh = -1;

    public ResearchTableContainer(InventoryPlayer playerInventory, ResearchTableTileEntity tile) {
        this.tile = tile;
        this.player = playerInventory.player;

        addSlotToContainer(new ResearchTableSlot(tile, ResearchTableTileEntity.SLOT_NOTES, 58, 68));
        addSlotToContainer(new ResearchTableSlot(tile, ResearchTableTileEntity.SLOT_SEAL, 58, 32));
        for (int i = 0; i < TASK_SLOT_COUNT; i++) {
            taskSlots[i] = new ResearchTaskSlot(taskInventory, i);
            addSlotToContainer(taskSlots[i]);
        }
        refreshTaskSlots(false);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 142 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInventory, col, 16 + col * 18, 200));
        }
    }

    private void refreshTaskSlots(boolean returnMismatched) {
        String key = getResearchKey();
        boolean changed = !key.equals(lastResearchKey);
        if (changed) {
            lastResearchKey = key;
            for (ResearchTaskSlot slot : taskSlots) {
                slot.clear(player);
            }
        }

        ItemStack notes = tile.getStackInSlot(ResearchTableTileEntity.SLOT_NOTES);
        if (tile.isResearchInProgress()) {
            hideAllTaskSlots(returnMismatched);
            return;
        }
        if (notes.isEmpty() || notes.getItem() != ModItems.RESEARCH_NOTES || !notes.hasTagCompound()) {
            hideAllTaskSlots(returnMismatched);
            return;
        }
        NBTTagCompound tag = notes.getTagCompound();
        Research research = Researches.find(new ResourceLocation(tag.getString("research")));
        if (research == null) {
            hideAllTaskSlots(returnMismatched);
            return;
        }
        int stepsDone = tag.getInteger("stepsDone");
        if (stepsDone >= research.getStars()) {
            hideAllTaskSlots(false);
            return;
        }
        if (!returnMismatched && !changed) {
            return;
        }

        List<ResearchTask> tasks = research.getTasks(getTaskSeed(notes), stepsDone);
        int taskSlot = 0;
        for (int i = 0; i < tasks.size(); i++) {
            ResearchTask task = tasks.get(i);
            if (task instanceof ResearchTask.ItemsTask) {
                List<ItemStack> items = ((ResearchTask.ItemsTask) task).getItems();
                for (int j = 0; j < items.size() && taskSlot < taskSlots.length; j++) {
                    taskSlots[taskSlot].configure(items.get(j), TASK_X + 11 + 17 * j, TASK_Y + i * TASK_SPACING + 7);
                    if (!taskSlots[taskSlot].matchesExpectation()) {
                        taskSlots[taskSlot].clear(player);
                        taskSlots[taskSlot].configure(items.get(j), TASK_X + 11 + 17 * j, TASK_Y + i * TASK_SPACING + 7);
                    }
                    taskSlot++;
                }
            }
        }
        hideUnusedTaskSlots(taskSlot, returnMismatched);
    }

    public void refreshVisibleTaskSlots() {
        refreshTaskSlots(false);
    }

    public boolean hasActiveTaskSlots() {
        for (ResearchTaskSlot slot : taskSlots) {
            if (slot.isActive()) {
                return true;
            }
        }
        return false;
    }

    public int getProgress() {
        return tile.getField(ResearchTableTileEntity.FIELD_PROGRESS);
    }

    public int getResearchSeed() {
        ItemStack notes = tile.getStackInSlot(ResearchTableTileEntity.SLOT_NOTES);
        return getTaskSeed(notes);
    }

    public boolean isTaskCompleteClient(List<ResearchTask> tasks, int taskIndex) {
        int slotStart = getTaskSlotStart(tasks, taskIndex);
        ResearchTask task = tasks.get(taskIndex);
        return task.isComplete(getTaskInputs(slotStart, task.getSlotCount()), player);
    }

    public void submitTask(int taskIndex) {
        if (player.world.isRemote) {
            return;
        }
        ItemStack notes = tile.getStackInSlot(ResearchTableTileEntity.SLOT_NOTES);
        if (notes.isEmpty() || notes.getItem() != ModItems.RESEARCH_NOTES || !notes.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = notes.getTagCompound();
        Research research = Researches.find(new ResourceLocation(tag.getString("research")));
        if (research == null) {
            return;
        }
        int stepsDone = tag.getInteger("stepsDone");
        if (stepsDone >= research.getStars()) {
            return;
        }
        List<ResearchTask> tasks = research.getTasks(getTaskSeed(notes), stepsDone);
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            return;
        }

        int slotStart = getTaskSlotStart(tasks, taskIndex);
        ResearchTask task = tasks.get(taskIndex);
        List<ItemStack> inputs = getTaskInputs(slotStart, task.getSlotCount());
        if (!task.isComplete(inputs, player)) {
            return;
        }
        task.consume(inputs, player);
        for (int i = 0; i < task.getSlotCount() && slotStart + i < taskSlots.length; i++) {
            taskSlots[slotStart + i].onSlotChanged();
        }
        tile.startResearchProgress();
        clearTaskInputsWithoutDropping();
        lastResearchKey = "";
        refreshTaskSlots(false);
        detectAndSendChanges();
    }

    public void stampResearch() {
        if (player.world.isRemote) {
            return;
        }
        ItemStack notes = tile.getStackInSlot(ResearchTableTileEntity.SLOT_NOTES);
        ItemStack seal = tile.getStackInSlot(ResearchTableTileEntity.SLOT_SEAL);
        if (notes.isEmpty()
                || notes.getItem() != ModItems.RESEARCH_NOTES
                || !notes.hasTagCompound()
                || seal.isEmpty()
                || seal.getItem() != ModItems.ARCANE_SEAL) {
            return;
        }

        NBTTagCompound tag = notes.getTagCompound();
        Research research = Researches.find(new ResourceLocation(tag.getString("research")));
        if (research == null || tag.getInteger("stepsDone") < research.getStars()) {
            return;
        }

        ItemStack completed = new ItemStack(ModItems.COMPLETED_RESEARCH);
        NBTTagCompound completedTag = new NBTTagCompound();
        completedTag.setString("research", tag.getString("research"));
        completed.setTagCompound(completedTag);
        seal.shrink(1);
        tile.setInventorySlotContents(ResearchTableTileEntity.SLOT_NOTES, completed);
        tile.setInventorySlotContents(ResearchTableTileEntity.SLOT_SEAL, seal);
        clearTaskInputsWithoutDropping();
        lastResearchKey = "";
        refreshTaskSlots(false);
        detectAndSendChanges();
    }

    private void clearTaskInputsWithoutDropping() {
        for (ResearchTaskSlot slot : taskSlots) {
            slot.clearVisual();
        }
    }

    private int getTaskSlotStart(List<ResearchTask> tasks, int taskIndex) {
        int slot = 0;
        for (int i = 0; i < taskIndex; i++) {
            slot += tasks.get(i).getSlotCount();
        }
        return slot;
    }

    private List<ItemStack> getTaskInputs(int slotStart, int count) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (slotStart + i < taskSlots.length) {
                inputs.add(taskSlots[slotStart + i].getStack());
            } else {
                inputs.add(ItemStack.EMPTY);
            }
        }
        return inputs;
    }

    private void hideUnusedTaskSlots(int firstUnusedSlot, boolean returnMismatched) {
        for (int i = firstUnusedSlot; i < taskSlots.length; i++) {
            if (!taskSlots[i].getStack().isEmpty() && returnMismatched) {
                taskSlots[i].clear(player);
            } else {
                taskSlots[i].clearVisual();
            }
        }
    }

    private void hideAllTaskSlots(boolean returnMismatched) {
        hideUnusedTaskSlots(0, returnMismatched);
    }

    private String getResearchKey() {
        ItemStack notes = tile.getStackInSlot(ResearchTableTileEntity.SLOT_NOTES);
        if (notes.isEmpty() || !notes.hasTagCompound()) {
            return "";
        }
        NBTTagCompound tag = notes.getTagCompound();
        return tag.getString("research") + ":" + tag.getInteger("stepsDone") + ":" + getTaskSeed(notes);
    }

    private int getTaskSeed(ItemStack notes) {
        return Researches.usesWorldSeed(notes)
                ? tile.getResearchSeed()
                : ResearchTableTileEntity.LEGACY_RESEARCH_SEED;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.getWorld().getBlockState(tile.getPos()).getBlock() == ModBlocks.RESEARCH_TABLE
                && playerIn.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            int taskSlotEnd = ResearchTableTileEntity.SLOT_COUNT + TASK_SLOT_COUNT;
            if (index < taskSlotEnd) {
                if (!mergeItemStack(stack, taskSlotEnd, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, taskSlotEnd, false)) {
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

    @Override
    public void detectAndSendChanges() {
        refreshTaskSlots(true);
        super.detectAndSendChanges();
        int progress = tile.getField(0);
        if (progress != lastProgress) {
            for (IContainerListener listener : listeners) {
                listener.sendWindowProperty(this, ResearchTableTileEntity.FIELD_PROGRESS, progress);
            }
            lastProgress = progress;
        }
        int seedLow = tile.getField(ResearchTableTileEntity.FIELD_RESEARCH_SEED_LOW);
        if (seedLow != lastSeedLow) {
            for (IContainerListener listener : listeners) {
                listener.sendWindowProperty(this, ResearchTableTileEntity.FIELD_RESEARCH_SEED_LOW, seedLow);
            }
            lastSeedLow = seedLow;
        }
        int seedHigh = tile.getField(ResearchTableTileEntity.FIELD_RESEARCH_SEED_HIGH);
        if (seedHigh != lastSeedHigh) {
            for (IContainerListener listener : listeners) {
                listener.sendWindowProperty(this, ResearchTableTileEntity.FIELD_RESEARCH_SEED_HIGH, seedHigh);
            }
            lastSeedHigh = seedHigh;
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tile.setField(id, data);
        if (id == ResearchTableTileEntity.FIELD_PROGRESS
                && (data == 0 || data == ResearchTableTileEntity.RESEARCH_PROGRESS_TICKS)) {
            lastResearchKey = "";
            refreshTaskSlots(false);
        } else if (id == ResearchTableTileEntity.FIELD_RESEARCH_SEED_LOW
                || id == ResearchTableTileEntity.FIELD_RESEARCH_SEED_HIGH) {
            lastResearchKey = "";
            refreshTaskSlots(false);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        if (inventoryIn == tile) {
            refreshTaskSlots(true);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.world.isRemote) {
            for (int i = 0; i < taskInventory.getSizeInventory(); i++) {
                ItemStack stack = taskInventory.removeStackFromSlot(i);
                if (!stack.isEmpty()) {
                    playerIn.dropItem(stack, false);
                }
            }
        }
    }
}
